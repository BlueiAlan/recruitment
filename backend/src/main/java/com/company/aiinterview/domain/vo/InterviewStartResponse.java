package com.company.aiinterview.domain.vo;

import lombok.Data;

@Data
public class InterviewStartResponse {
    private String sessionId;
    private QuestionVO firstQuestion;
}
