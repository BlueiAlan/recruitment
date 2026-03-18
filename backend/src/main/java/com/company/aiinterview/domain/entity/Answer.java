package com.company.aiinterview.domain.entity;

import lombok.Data;

@Data
public class Answer {
    private String id;
    private String questionId;
    private String content;
    private int score;
    private String feedback;
}
