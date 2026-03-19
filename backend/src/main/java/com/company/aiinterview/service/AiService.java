package com.company.aiinterview.service;

public interface AiService {
    String generateOpeningQuestion(String resumeText, String jdText);

    AiTurn evaluateAndGenerateNext(
            String resumeText,
            String jdText,
            String historyText,
            String question,
            String answer,
            boolean allowNextQuestion
    );

    String summarize(String resumeText, String jdText, String historyText);

    record AiScore(int score, String feedback) {}

    record AiTurn(int score, String feedback, boolean shouldEnd, String nextQuestion) {}
}
