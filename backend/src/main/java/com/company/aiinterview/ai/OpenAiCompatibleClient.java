package com.company.aiinterview.ai;

import com.company.aiinterview.common.ErrorCode;
import com.company.aiinterview.exception.ApiException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class OpenAiCompatibleClient implements AiClient {
    private final RestTemplate restTemplate;
    private final AiProperties properties;

    public OpenAiCompatibleClient(RestTemplate restTemplate, AiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public String generateQuestionsJson(String resumeText, String jdText) {
        String prompt = PromptTemplates.QUESTION_PROMPT
                .replace("{{resume}}", safe(resumeText))
                .replace("{{jd}}", safe(jdText));
        return callModel(prompt);
    }

    @Override
    public String scoreAnswerJson(String question, String answer) {
        String prompt = PromptTemplates.SCORE_PROMPT
                .replace("{{question}}", safe(question))
                .replace("{{answer}}", safe(answer));
        return callModel(prompt);
    }

    private String callModel(String prompt) {
        String endpoint = trim(properties.getEndpoint());
        String apiKey = trim(properties.getApiKey());
        String model = trim(properties.getModel());
        if (endpoint.isEmpty()) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai endpoint missing");
        }
        if (apiKey.isEmpty()) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai api key missing");
        }
        if (model.isEmpty()) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai model missing");
        }

        OpenAiRequest request = new OpenAiRequest(model, List.of(new Message("user", prompt)), 0.2);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<OpenAiResponse> response =
                    restTemplate.postForEntity(endpoint, entity, OpenAiResponse.class);
            OpenAiResponse body = response.getBody();
            if (body == null || body.choices == null || body.choices.isEmpty()) {
                throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai response empty");
            }
            Choice choice = body.choices.get(0);
            String content = "";
            if (choice.message != null && choice.message.content != null) {
                content = choice.message.content;
            } else if (choice.text != null) {
                content = choice.text;
            }
            content = normalizeContent(content);
            if (content.isEmpty()) {
                throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai response empty");
            }
            return content;
        } catch (RestClientException ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "ai request failed");
        }
    }

    private String normalizeContent(String content) {
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline > -1 && lastFence > firstNewline) {
                return trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    static class OpenAiRequest {
        private final String model;
        private final List<Message> messages;
        private final double temperature;

        OpenAiRequest(String model, List<Message> messages, double temperature) {
            this.model = model;
            this.messages = messages;
            this.temperature = temperature;
        }

        public String getModel() {
            return model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public double getTemperature() {
            return temperature;
        }
    }

    static class Message {
        private final String role;
        private final String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class OpenAiResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Choice {
        private MessageContent message;
        private String text;

        public MessageContent getMessage() {
            return message;
        }

        public void setMessage(MessageContent message) {
            this.message = message;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class MessageContent {
        @JsonProperty("content")
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
