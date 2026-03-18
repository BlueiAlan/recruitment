package com.company.aiinterview.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class InterviewResultResponse {
    private String sessionId;
    private double averageScore;
    private String overallAdvice;
    private List<AnswerSummary> answers;

    @Data
    public static class AnswerSummary {
        private String question;
        private String answer;
        private int score;
        private String feedback;
    }
}
