package com.company.aiinterview.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerRequest {
    @NotBlank(message = "sessionId required")
    private String sessionId;

    @NotBlank(message = "questionId required")
    private String questionId;

    @NotBlank(message = "answer required")
    private String answer;
}
