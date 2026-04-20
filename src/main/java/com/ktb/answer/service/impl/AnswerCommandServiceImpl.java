package com.ktb.answer.service.impl;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerStatus;
import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.exception.AnswerAccessDeniedException;
import com.ktb.answer.exception.AnswerInvalidContentException;
import com.ktb.answer.exception.DuplicateAnswerException;
import com.ktb.answer.exception.InvalidAnswerContentException;
import com.ktb.answer.exception.InvalidAnswerStatusTransitionException;
import com.ktb.answer.service.AnswerCommandService;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.exception.account.AccountNotFoundException;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.question.domain.Question;
import com.ktb.question.exception.QuestionNotFoundException;
import com.ktb.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerCommandServiceImpl implements AnswerCommandService {

    private static final int MAX_ANSWER_CONTENT_LENGTH = 1_500;

    private final QuestionRepository questionRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public Answer create(Long accountId, Long questionId, String content, AnswerType type) {
        log.debug("Creating answer - accountId={}, questionId={}", accountId, questionId);

        UserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        return Answer.create(question, account, content, type);
    }

    @Override
    public void validateOwnership(Answer answer, Long accountId) throws AnswerAccessDeniedException {
        log.debug("Validating ownership - answerId={}, accountId={}", answer.getId(), accountId);

        if (!answer.isOwnedBy(accountId)) {
            log.warn("Answer access denied - answerId={}, requestedAccountId={}", answer.getId(), accountId);
            throw new AnswerAccessDeniedException(answer.getId(), accountId);
        }
    }

    @Override
    public void transitionStatus(Answer answer, AnswerStatus nextStatus) throws InvalidAnswerStatusTransitionException {
        log.debug("Transitioning answer status - answerId={}, from={}, to={}",
                answer.getId(), answer.getStatus(), nextStatus);

        answer.transitionTo(nextStatus);
    }

    @Override
    public void checkDuplicate(String sessionId, Long questionId) throws DuplicateAnswerException {
        log.debug("Checking duplicate answer - sessionId={}, questionId={}", sessionId, questionId);
        // TODO: ANSWER_SESSION 엔티티 구현 후 활성화
    }

    @Override
    public void validateContent(String answerText) {
        log.debug("Validating answer content - hasText={}", answerText != null && !answerText.isBlank());

        boolean hasText = answerText != null && !answerText.isBlank();
        if (!hasText || answerText.length() > MAX_ANSWER_CONTENT_LENGTH) {
            log.warn("Invalid answer content - hasText={}, length={}",
                    hasText, answerText == null ? null : answerText.length());
            throw new AnswerInvalidContentException();
        }
    }
}
