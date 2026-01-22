package com.ktb.answer.service.impl;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.exception.AnswerAccessDeniedException;
import com.ktb.answer.exception.AnswerInvalidContentException;
import com.ktb.answer.exception.DuplicateAnswerException;
import com.ktb.answer.exception.InvalidAnswerContentException;
import com.ktb.answer.exception.InvalidAnswerStatusTransitionException;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.answer.service.AnswerDomainService;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.question.domain.Question;
import com.ktb.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerDomainServiceImpl implements AnswerDomainService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserAccountRepository userAccountRepository;

    private static final int MAX_ANSWER_CONTENT_LENGTH = 1_500;

    @Override
    public Answer createAnswer(Long accountId, Long questionId, String answerContent,
                               String audioUrl, String videoUrl, AnswerType type) {
        // TODO: 구현 필요
        // 1. UserAccount 조회
        // 2. Question 조회
        // 3. Answer.create() 호출
        // 4. Answer 반환 (저장은 Application Service에서 수행)

        log.debug("Creating answer for accountId: {}, questionId: {}", accountId, questionId);

        UserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        return Answer.create(question, account, answerContent, type);
    }

    @Override
    public void validateOwnership(Answer answer, Long accountId)
            throws AnswerAccessDeniedException {
        // TODO: 구현 필요
        // 1. answer.isOwnedBy(accountId) 호출
        // 2. false면 AnswerAccessDeniedException 발생

        log.debug("Validating ownership for answerId: {}, accountId: {}", answer.getId(), accountId);

        if (!answer.isOwnedBy(accountId)) {
            throw new AnswerAccessDeniedException(answer.getId(), accountId);
        }
    }

    @Override
    public void transitionStatus(Answer answer, AnswerStatus nextStatus)
            throws InvalidAnswerStatusTransitionException {
        // TODO: 구현 필요
        // 1. answer.transitionTo(nextStatus) 호출
        // 2. 상태 전이 규칙은 Answer 엔티티에 위임

        log.debug("Transitioning answer status: answerId={}, from={}, to={}",
                answer.getId(), answer.getStatus(), nextStatus);

        answer.transitionTo(nextStatus);
    }

    @Override
    public void checkDuplicateAnswer(String sessionId, Long questionId)
            throws DuplicateAnswerException {
        // TODO: MVP V2 구현 필요
        // 1. ANSWER_SESSION 엔티티 구현 후 활성화
        // 2. AnswerRepository.existsBySessionIdAndQuestionId() 호출
        // 3. 존재하면 DuplicateAnswerException 발생

        log.debug("Checking duplicate answer for sessionId: {}, questionId: {}", sessionId, questionId);

        // TODO: ANSWER_SESSION 엔티티 구현 후 활성화
        // boolean exists = answerRepository.existsBySessionIdAndQuestionId(sessionId, questionId);
        // if (exists) {
        //     throw new DuplicateAnswerException(sessionId, questionId);
        // }
    }

    @Override
    public void validateAnswerContent(String answerText, String audioUrl)
            throws InvalidAnswerContentException {
        // TODO: 구현 필요
        // 1. answerText와 audioUrl 중 최소 1개 필수
        // 2. answerText가 있으면 길이 제한 검증 (1,500자)
        // 3. 유효하지 않으면 InvalidAnswerContentException 발생

        log.debug("Validating answer content: hasText={}, hasAudio={}",
                answerText != null && !answerText.isBlank(), audioUrl != null && !audioUrl.isBlank());

        boolean hasText = answerText != null && !answerText.isBlank();
        boolean hasAudio = audioUrl != null && !audioUrl.isBlank();

        if (!hasText && !hasAudio) {
            throw new AnswerInvalidContentException();
        }

        if (hasText && answerText.length() > MAX_ANSWER_CONTENT_LENGTH) {
            throw new AnswerInvalidContentException();
        }
    }
}
