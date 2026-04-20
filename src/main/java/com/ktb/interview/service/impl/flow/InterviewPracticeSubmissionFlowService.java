package com.ktb.interview.service.impl.flow;

import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.domain.TurnType;
import com.ktb.interview.domain.InterviewHistoryItem;
import com.ktb.interview.domain.InterviewQuestionSnapshot;
import com.ktb.interview.domain.InterviewSession;
import com.ktb.interview.domain.InterviewSessionStatus;
import com.ktb.interview.dto.request.PracticeAnswerSubmitRequest;
import com.ktb.interview.dto.response.InterviewPracticeSubmitResponse;
import com.ktb.interview.service.InterviewSessionService;
import com.ktb.interview.service.impl.InterviewSubmissionValidator;
import com.ktb.question.domain.Question;
import com.ktb.question.exception.QuestionNotFoundException;
import com.ktb.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 연습 모드 답변 제출 플로우를 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewPracticeSubmissionFlowService {

    private static final String SESSION_STATUS_IN_PROGRESS = InterviewSessionStatus.IN_PROGRESS.name();

    private final InterviewSessionService interviewSessionService;
    private final QuestionRepository questionRepository;
    private final InterviewSubmissionValidator submissionValidator;

    /**
     * 연습 모드 답변 1건을 세션 이력에 반영합니다.
     */
    @Transactional
    public InterviewPracticeSubmitResponse submitPractice(Long accountId, PracticeAnswerSubmitRequest request, String clientIp) {
        log.info("submitPractice - accountId={}, sessionId={}, questionId={}, clientIp={}",
                accountId, request.sessionId(), request.questionId(), clientIp);
        InterviewSession session = interviewSessionService.getSession(accountId, request.sessionId());
        submissionValidator.validateSessionType(session, AnswerType.PRACTICE_INTERVIEW);
        submissionValidator.validateSessionAvailable(session);
        submissionValidator.validatePracticeSubmissionAvailable(session);

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new QuestionNotFoundException(request.questionId()));
        submissionValidator.validateQuestionTypeMatch(session.getQuestionType(), question.getType());

        int turnOrder = session.getTurnCount();
        Integer topicId = session.getCurrentTopicId() == null ? 1 : session.getCurrentTopicId();
        InterviewHistoryItem currentTurn = new InterviewHistoryItem(
                question.getId(),
                question.getContent(),
                question.getCategory(),
                request.answerText(),
                TurnType.NEW_TOPIC,
                turnOrder,
                topicId,
                null
        );

        session.appendHistory(currentTurn);
        session.updateNextQuestion(
                new InterviewQuestionSnapshot(question.getId(), question.getContent(), question.getCategory()),
                TurnType.SESSION_END,
                topicId
        );
        interviewSessionService.save(session);

        return new InterviewPracticeSubmitResponse(
                accountId,
                question.getId(),
                session.getSessionId(),
                SESSION_STATUS_IN_PROGRESS,
                TurnType.NEW_TOPIC.wireValue(),
                turnOrder,
                topicId,
                false
        );
    }
}
