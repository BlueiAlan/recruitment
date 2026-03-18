package com.company.aiinterview.domain.vo;

import lombok.Data;

@Data
public class AnswerFeedbackVO {
    private int score;
    private String feedback;
    private QuestionVO nextQuestion;
}
