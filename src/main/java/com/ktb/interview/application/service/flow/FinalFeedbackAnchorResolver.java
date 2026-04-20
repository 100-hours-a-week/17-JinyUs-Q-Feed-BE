package com.ktb.interview.application.service.flow;

import com.ktb.answer.domain.AnswerType;
import com.ktb.interview.session.domain.InterviewHistoryItem;
import com.ktb.interview.session.domain.InterviewQuestionSnapshot;
import com.ktb.interview.session.domain.InterviewSession;
import com.ktb.interview.session.exception.InterviewSessionInvalidStateException;
import com.ktb.question.domain.Question;
import com.ktb.question.domain.QuestionType;
import com.ktb.question.exception.QuestionNotFoundException;
import com.ktb.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 세션 최종 피드백 생성 시 영속/통계용 질문 앵커를 선택합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinalFeedbackAnchorResolver {

    private static final String ERROR_PRACTICE_QUESTION_ID_MISSING =
            "practice interview questionId is missing in session history";

    private final QuestionRepository questionRepository;

    /**
     * 세션 타입에 따라 영속용 질문 앵커를 선택합니다.
     */
    public Question resolve(InterviewSession session, InterviewHistoryItem latestTurn) {
        if (session.getInterviewType() == AnswerType.PRACTICE_INTERVIEW) {
            if (latestTurn.questionId() == null) {
                throw new InterviewSessionInvalidStateException(ERROR_PRACTICE_QUESTION_ID_MISSING);
            }
            return questionRepository.findById(latestTurn.questionId())
                    .orElseThrow(() -> new QuestionNotFoundException(latestTurn.questionId()));
        }
        return resolvePersistenceQuestionForReal(session);
    }

    private Question resolvePersistenceQuestionForReal(InterviewSession session) {
        Long initialQuestionId = session.getInitialQuestionId();
        if (initialQuestionId != null) {
            return questionRepository.findById(initialQuestionId)
                    .orElseThrow(() -> new QuestionNotFoundException(initialQuestionId));
        }

        InterviewQuestionSnapshot snapshot = session.getCurrentQuestion();
        QuestionType questionType = session.getQuestionType();
        if (snapshot == null) {
            return questionRepository.findRandomActiveByType(questionType.name())
                    .orElseThrow(() -> new QuestionNotFoundException(0L));
        }
        if (snapshot.questionId() != null) {
            return questionRepository.findById(snapshot.questionId())
                    .orElseThrow(() -> new QuestionNotFoundException(snapshot.questionId()));
        }
        if (snapshot.category() != null) {
            return questionRepository.findRandomActiveByTypeAndCategory(
                            questionType.name(),
                            snapshot.category().name()
                    )
                    .orElseGet(() -> questionRepository.findRandomActiveByType(questionType.name())
                            .orElseThrow(() -> new QuestionNotFoundException(0L)));
        }
        return questionRepository.findRandomActiveByType(questionType.name())
                .orElseThrow(() -> new QuestionNotFoundException(0L));
    }
}
