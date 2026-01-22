package com.ktb.answer.service.impl;

import com.ktb.answer.domain.Answer;
import com.ktb.answer.domain.AnswerType;
import com.ktb.answer.dto.AnswerDetailQuery;
import com.ktb.answer.dto.AnswerDetailResult;
import com.ktb.answer.dto.AnswerSubmitCommand;
import com.ktb.answer.dto.AnswerSubmitResult;
import com.ktb.answer.dto.FeedbackResult;
import com.ktb.answer.dto.ImmediateFeedbackResult;
import com.ktb.answer.exception.AnswerAccessDeniedException;
import com.ktb.answer.exception.AnswerInvalidContentException;
import com.ktb.answer.exception.AnswerNotFoundException;
import com.ktb.answer.repository.AnswerRepository;
import com.ktb.answer.service.AnswerApplicationService;
import com.ktb.answer.service.ImmediateFeedbackService;
import com.ktb.auth.domain.UserAccount;
import com.ktb.auth.repository.UserAccountRepository;
import com.ktb.file.exception.FileAlreadyDeletedException;
import com.ktb.file.exception.FileExtensionNotAllowedException;
import com.ktb.file.exception.FileNotFoundException;
import com.ktb.file.exception.FileSizeExceededException;
import com.ktb.file.exception.FileStorageMigrationException;
import com.ktb.question.domain.Question;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.exception.QuestionDisabledException;
import com.ktb.question.exception.QuestionNotFoundException;
import com.ktb.question.repository.QuestionRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AnswerApplicationServiceImpl implements AnswerApplicationService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserAccountRepository userAccountRepository;
    private final ImmediateFeedbackService immediateFeedbackService;

    @Override
    public Slice<?> getList(
            Long accountId,
            AnswerType type,
            QuestionCategory category,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            LocalDateTime cursorCreatedAt,
            Long cursorAnswerId,
            int limit,
            String expand
    ) {
        // TODO: 구현 필요
        // 1. Repository에서 답변 목록 조회 (커서 페이지네이션)
        // 2. expand 파라미터에 따라 추가 데이터 조회 (question, feedback)
        // 3. DTO로 변환하여 반환

        log.debug("Retrieving answer list for accountId: {}", accountId);

        PageRequest pageRequest = PageRequest.of(0, limit + 1);

        Slice<Answer> answers = answerRepository.findByAccountIdWithFilters(
                accountId,
                type,
                category,
                dateFrom,
                dateTo,
                cursorCreatedAt,
                cursorAnswerId,
                pageRequest
        );

        // TODO: expand 처리 및 DTO 변환
        return answers;
    }

    @Override
    @Transactional
    public AnswerSubmitResult submit(Long accountId, AnswerSubmitCommand command)
            throws QuestionNotFoundException, QuestionDisabledException,
            FileSizeExceededException, FileExtensionNotAllowedException,
            FileNotFoundException, FileAlreadyDeletedException, FileStorageMigrationException {

        // TODO: 구현 필요
        // 1. 입력 검증 (answer_text 또는 audio_file 중 최소 1개 필수)
        // 2. Question 존재 및 활성화 검증
        // 3. 파일 업로드 처리 (있는 경우)
        // 4. STT 처리 (음성 파일인 경우)
        // 5. Answer 엔티티 생성 및 저장
        // 6. 즉각 피드백 생성 (동기)
        // 7. AI 피드백 요청 이벤트 발행 (비동기)
        // 8. 결과 반환

        log.info("Submitting answer for questionId: {}, accountId: {}", command.questionId(), accountId);

        validateSubmitCommand(command);

        Question question = findQuestionById(command.questionId());
        validateQuestionEnabled(question);

        UserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // TODO: 파일 업로드 및 STT 처리

        Answer answer = Answer.create(
                question,
                account,
                command.answerText(),
                command.answerType()
        );

        Answer savedAnswer = answerRepository.save(answer);

        ImmediateFeedbackResult immediateFeedback = immediateFeedbackService.evaluate(
                question.getId(),
                savedAnswer.getContent()
        );

        // TODO: MVP V2 AI 피드백 이벤트 발행

        return AnswerSubmitResult.processing(savedAnswer.getId(), immediateFeedback);
    }

    @Override
    @Transactional
    public AnswerSubmitResult submitWithSession(Long accountId, String sessionId, AnswerSubmitCommand command)
            throws QuestionNotFoundException {

        // TODO: MVP V2 구현 필요
        // 1. 세션 존재 및 소유권 검증
        // 2. 세션 상태 검증 (IN_PROGRESS만 허용)
        // 3. 중복 답변 검증 (동일 질문에 대한 답변)
        // 4. 파일 업로드 처리 (비디오 파일)
        // 5. Answer 생성 및 저장
        // 6. 즉각 피드백 생성
        // 7. 세션 depth 증가
        // 8. AI 피드백 이벤트 발행
        // 9. 다음 질문 생성 (필요 시)
        // 10. 결과 반환

        log.info("Submitting session answer for sessionId: {}, questionId: {}, accountId: {}",
                sessionId, command.questionId(), accountId);

        // TODO: ANSWER_SESSION 엔티티 구현 후 활성화
        throw new UnsupportedOperationException("Session-based answer submission not yet implemented");
    }

    @Override
    public AnswerDetailResult getDetail(Long accountId, Long answerId, AnswerDetailQuery query)
            throws AnswerNotFoundException, AnswerAccessDeniedException {

        // TODO: 구현 필요
        // 1. Answer 조회
        // 2. 소유권 검증
        // 3. expand 파라미터에 따라 추가 데이터 조회
        //    - expand=question: Question 정보 포함
        //    - expand=feedback: ANSWER_METRIC, AI 피드백 포함
        //    - expand=immediate_feedback: 즉각 피드백 포함
        // 4. DTO로 변환하여 반환

        log.debug("Retrieving answer detail for answerId: {}, accountId: {}", answerId, accountId);

        Answer answer = findAnswerById(answerId);
        validateOwnership(answer, accountId);

        // TODO: expand 처리 및 DTO 변환
        return null;
    }

    @Override
    public FeedbackResult getFeedback(Long accountId, Long answerId)
            throws AnswerNotFoundException, AnswerAccessDeniedException {

        // TODO: 구현 필요
        // 1. Answer 조회
        // 2. 소유권 검증
        // 3. Answer 상태에 따라 응답 분기
        //    - AI_FEEDBACK_PROCESSING: 202 응답 (retry_after 포함)
        //    - COMPLETED: 200 응답 (레이더 차트, AI 피드백 포함)
        //    - FAILED: 200 응답 (실패 사유 포함)
        // 4. ANSWER_METRIC 조회 (레이더 차트 데이터)
        // 5. DTO로 변환하여 반환

        log.debug("Retrieving feedback for answerId: {}, accountId: {}", answerId, accountId);

        Answer answer = findAnswerById(answerId);
        validateOwnership(answer, accountId);

        // TODO: 상태별 응답 처리 및 DTO 변환
        return null;
    }

    private void validateSubmitCommand(AnswerSubmitCommand command) {
        if (!command.hasText() && !command.hasAudio()) {
            throw new AnswerInvalidContentException();
        }
    }

    private Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
    }

    private void validateQuestionEnabled(Question question) {
        if (!question.isEnabled()) {
            throw new QuestionDisabledException(question.getId());
        }
    }

    private Answer findAnswerById(Long answerId) {
        Answer answer = answerRepository.findByIdWithQuestion(answerId);
        if (answer == null) {
            throw new AnswerNotFoundException(answerId);
        }
        return answer;
    }

    private void validateOwnership(Answer answer, Long accountId) {
        if (!answer.isOwnedBy(accountId)) {
            throw new AnswerAccessDeniedException(answer.getId(), accountId);
        }
    }
}
