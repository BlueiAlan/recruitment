package com.company.aiinterview.ai;

public interface AiClient {
    String generateOpeningQuestionJson(String resumeText, String jdText);

    String evaluateAnswerJson(String resumeText, String jdText, String historyText, String question, String answer, boolean allowNextQuestion);

    String summarizeJson(String resumeText, String jdText, String historyText);
}
