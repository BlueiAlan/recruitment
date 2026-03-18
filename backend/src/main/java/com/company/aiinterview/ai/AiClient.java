package com.company.aiinterview.ai;

public interface AiClient {
    String generateQuestionsJson(String resumeText, String jdText);

    String scoreAnswerJson(String question, String answer);
}
