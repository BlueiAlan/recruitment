package com.company.aiinterview.service.impl;

import com.company.aiinterview.ai.AiClient;
import com.company.aiinterview.common.ErrorCode;
import com.company.aiinterview.exception.ApiException;
import com.company.aiinterview.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiServiceImpl implements AiService {
    private final AiClient aiClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AiServiceImpl(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @Override
    public List<String> generateQuestions(String resumeText, String jdText) {
        String json = aiClient.generateQuestionsJson(resumeText, jdText);
        try {
            JsonNode node = mapper.readTree(json);
            JsonNode arr = node.get("questions");
            List<String> list = new ArrayList<>();
            if (arr != null && arr.isArray()) {
                for (JsonNode q : arr) {
                    list.add(q.asText());
                }
            }
            if (list.isEmpty()) {
                throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai questions empty");
            }
            return list;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai parse failed");
        }
    }

    @Override
    public AiScore scoreAnswer(String question, String answer) {
        String json = aiClient.scoreAnswerJson(question, answer);
        try {
            JsonNode node = mapper.readTree(json);
            int score = node.get("score").asInt();
            String feedback = node.get("feedback").asText();
            return new AiScore(score, feedback);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai parse failed");
        }
    }
}
