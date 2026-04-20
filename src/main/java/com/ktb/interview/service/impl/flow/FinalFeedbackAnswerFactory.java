package com.ktb.interview.service.impl.flow;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.answer.service.AnswerDomainService;
import com.ktb.question.domain.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinalFeedbackAnswerFactory {

    private final AnswerDomainService answerDomainService;
    private final AnswerRepository answerRepository;

    @Transactional
    public Answer createAndSave(Long accountId, Question question, String answerText,
                                String sessionId, AnswerType type) {
        Answer answer = answerDomainService.createAnswer(accountId, question.getId(), answerText, type);
        answer.assignSessionId(sessionId);
        answerRepository.save(answer);
        log.debug("FinalFeedbackAnswerFactory - created answer, answerId={}, sessionId={}",
                answer.getId(), sessionId);
        return answer;
    }
}
