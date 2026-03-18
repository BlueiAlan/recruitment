package com.company.aiinterview.service;

import com.company.aiinterview.domain.dto.AnswerRequest;
import com.company.aiinterview.domain.dto.InterviewStartRequest;
import com.company.aiinterview.domain.vo.AnswerFeedbackVO;
import com.company.aiinterview.domain.vo.InterviewResultResponse;
import com.company.aiinterview.domain.vo.InterviewStartResponse;

public interface InterviewService {
    InterviewStartResponse start(InterviewStartRequest request);

    AnswerFeedbackVO answer(AnswerRequest request);

    InterviewResultResponse result(String sessionId);
}
