package com.company.aiinterview.service;

import java.util.List;

public interface AiService {
    List<String> generateQuestions(String resumeText, String jdText);

    AiScore scoreAnswer(String question, String answer);

    record AiScore(int score, String feedback) {}
}
