package com.company.aiinterview.domain.entity;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class InterviewSession {
    private String id;
    private String resumeId;
    private String jdText;
    private Instant createdAt;
    private List<Question> questions = new ArrayList<>();
    private List<Answer> answers = new ArrayList<>();
    private int currentIndex;
}
