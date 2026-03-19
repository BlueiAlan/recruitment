package com.company.aiinterview.service.impl;

import com.company.aiinterview.ai.AiClient;
import com.company.aiinterview.common.ErrorCode;
import com.company.aiinterview.exception.ApiException;
import com.company.aiinterview.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {
    private final AiClient aiClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AiServiceImpl(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @Override
    public String generateOpeningQuestion(String resumeText, String jdText) {
        String json = aiClient.generateOpeningQuestionJson(resumeText, jdText);
        try {
            JsonNode node = mapper.readTree(json);
            String question = readText(node, "question");
            if (question.isBlank()) {
                throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai opening question empty");
            }
            return question;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai parse failed");
        }
    }

    @Override
    public AiTurn evaluateAndGenerateNext(String resumeText, String jdText, String historyText, String question, String answer, boolean allowNextQuestion) {
        String json = aiClient.evaluateAnswerJson(resumeText, jdText, historyText, question, answer, allowNextQuestion);
        try {
            JsonNode node = mapper.readTree(json);
            int score = clampScore(readInt(node, "score", 0));
            String feedback = readText(node, "feedback");
            boolean shouldEnd = node.path("shouldEnd").asBoolean(!allowNextQuestion);
            String nextQuestion = readText(node, "nextQuestion");

            if (!allowNextQuestion) {
                shouldEnd = true;
                nextQuestion = "";
            }
            if (feedback.isBlank()) {
                feedback = "Please provide more concrete evidence, trade-offs, and measurable outcomes.";
            }
            return new AiTurn(score, feedback, shouldEnd, nextQuestion);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai parse failed");
        }
    }

    @Override
    public String summarize(String resumeText, String jdText, String historyText) {
        String json = aiClient.summarizeJson(resumeText, jdText, historyText);
        try {
            JsonNode node = mapper.readTree(json);
            String advice = readText(node, "overallAdvice");
            if (advice.isBlank()) {
                return "Strengthen STAR structure and add measurable outcomes with clearer technical ownership.";
            }
            return advice;
        } catch (Exception e) {
            return "Strengthen STAR structure and add measurable outcomes with clearer technical ownership.";
        }
    }

    private int clampScore(int score) {
        return Math.max(0, Math.min(100, score));
    }

    private int readInt(JsonNode node, String field, int defaultValue) {
        JsonNode value = node.path(field);
        if (value.isNumber()) {
            return value.asInt(defaultValue);
        }
        if (value.isTextual()) {
            try {
                return Integer.parseInt(value.asText().trim());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private String readText(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? "" : value.asText("").trim();
    }
}
