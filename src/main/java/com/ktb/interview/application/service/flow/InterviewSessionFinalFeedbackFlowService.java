package com.ktb.interview.application.service.flow;

import com.ktb.answer.domain.Answer;
import com.ktb.hashtag.domain.QuestionHashtag;
import com.ktb.hashtag.repository.QuestionHashtagRepository;
import com.ktb.interview.application.InterviewFeedbackOrchestrator;
import com.ktb.interview.dto.ai.InterviewFeedbackDataResponse;
import com.ktb.interview.mapper.InterviewSubmissionResponseMapper;
import com.ktb.interview.domain.InterviewHistoryItem;
import com.ktb.interview.domain.InterviewSession;
import com.ktb.interview.domain.InterviewSessionFeedback;
import com.ktb.interview.domain.InterviewSessionStatus;
import com.ktb.interview.dto.response.InterviewSessionFinalFeedbackResponse;
import com.ktb.interview.exception.InterviewSessionInvalidStateException;
import com.ktb.interview.session.mapper.InterviewSessionFeedbackMapper;
import com.ktb.interview.session.persistence.InterviewFinalFeedbackPersistenceService;
import com.ktb.interview.repository.InterviewSessionFeedbackRepository;
import com.ktb.interview.session.service.InterviewSessionService;
import com.ktb.interview.validator.InterviewSubmissionValidator;
import com.ktb.question.domain.Question;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 세션 최종 피드백 생성/저장 플로우를 조율합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewSessionFinalFeedbackFlowService {

    private static final String SESSION_STATUS_COMPLETED = InterviewSessionStatus.COMPLETED.name();
    private static final String ERROR_EMPTY_INTERVIEW_HISTORY_FOR_FINAL_FEEDBACK =
            "interview history is empty for final feedback";

    private final InterviewSessionService interviewSessionService;
    private final InterviewSubmissionValidator submissionValidator;
    private final InterviewSubmissionResponseMapper responseMapper;
    private final InterviewSessionFeedbackRepository feedbackRepository;
    private final InterviewFeedbackOrchestrator interviewFeedbackOrchestrator;
    private final InterviewFinalFeedbackPersistenceService finalFeedbackPersistenceService;
    private final QuestionHashtagRepository questionHashtagRepository;
    private final FinalFeedbackAnchorResolver anchorResolver;
    private final FinalFeedbackAnswerFactory answerFactory;
    private final FinalFeedbackCompletionService completionService;

    /**
     * 세션(연습/실전) 누적 이력 기반으로 최종 AI 피드백을 생성합니다.
     */
    public InterviewSessionFinalFeedbackResponse requestSessionFinalFeedback(
            Long accountId, String sessionId, String clientIp) {
        log.info("requestSessionFinalFeedback - accountId={}, sessionId={}, clientIp={}",
                accountId, sessionId, clientIp);

        InterviewSession session = interviewSessionService.getSession(accountId, sessionId);
        submissionValidator.validateSessionReadyForFinalFeedback(session);

        Optional<InterviewSessionFeedback> cached = feedbackRepository.findBySessionId(sessionId);
        if (cached.isPresent()) {
            log.info("requestSessionFinalFeedback using cached feedback - sessionId={}", sessionId);
            InterviewFeedbackDataResponse cachedFeedback = InterviewSessionFeedbackMapper.toDto(cached.get())
                    .withStatus(SESSION_STATUS_COMPLETED);
            completionService.complete(session, cachedFeedback);
            return responseMapper.toSessionFinalFeedbackResponse(
                    cachedFeedback,
                    session.getInterviewType(),
                    session.getInterviewHistoryView()
            );
        }

        List<InterviewHistoryItem> history = session.getInterviewHistoryView();
        if (history.isEmpty()) {
            log.warn("requestSessionFinalFeedback failed - empty history, sessionId={}", sessionId);
            throw new InterviewSessionInvalidStateException(ERROR_EMPTY_INTERVIEW_HISTORY_FOR_FINAL_FEEDBACK);
        }
        log.debug("requestSessionFinalFeedback history resolved - sessionId={}, historySize={}",
                sessionId, history.size());

        InterviewHistoryItem latestTurn = history.get(history.size() - 1);
        Question persistenceQuestion = anchorResolver.resolve(session, latestTurn);

        String finalAnswerText = latestTurn.answerText();
        if (finalAnswerText == null || finalAnswerText.isBlank()) {
            finalAnswerText = "(final feedback request)";
        }

        Answer answer = answerFactory.createAndSave(
                accountId,
                persistenceQuestion,
                finalAnswerText,
                session.getSessionId(),
                session.getInterviewType()
        );

        List<QuestionHashtag> questionHashtags =
                questionHashtagRepository.findKeywordNamesByQuestionId(persistenceQuestion.getId());
        log.debug("requestSessionFinalFeedback anchor resolved - sessionId={}, questionId={}, hashtagCount={}",
                sessionId, persistenceQuestion.getId(), questionHashtags.size());

        InterviewFeedbackDataResponse feedback = interviewFeedbackOrchestrator.generateFeedback(
                accountId,
                session,
                persistenceQuestion,
                answer,
                history,
                questionHashtags
        );

        finalFeedbackPersistenceService.persistAnswerFeedback(answer, feedback, questionHashtags);

        InterviewFeedbackDataResponse completed = responseMapper.toFinalSessionFeedbackResponse(feedback, answer.getId());
        completionService.complete(session, completed);

        log.info("requestSessionFinalFeedback completed - accountId={}, sessionId={}, answerId={}",
                accountId, sessionId, answer.getId());
        return responseMapper.toSessionFinalFeedbackResponse(
                completed,
                session.getInterviewType(),
                session.getInterviewHistoryView()
        );
    }
}
