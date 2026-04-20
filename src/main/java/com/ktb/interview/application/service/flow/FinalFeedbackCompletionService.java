package com.ktb.interview.application.service.flow;

import com.ktb.interview.dto.ai.InterviewFeedbackDataResponse;
import com.ktb.interview.repository.InterviewSessionFinalFeedbackStore;
import com.ktb.interview.domain.InterviewSession;
import com.ktb.interview.session.service.InterviewSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinalFeedbackCompletionService {

    private final InterviewSessionFinalFeedbackStore finalFeedbackStore;
    private final InterviewSessionService interviewSessionService;

    @Transactional
    public void complete(InterviewSession session, InterviewFeedbackDataResponse feedback) {
        session.markCompleted();
        finalFeedbackStore.persistFinalFeedback(session, feedback);
        interviewSessionService.deleteSession(session.getSessionId());
        log.info("FinalFeedbackCompletionService - completed, sessionId={}", session.getSessionId());
    }
}
