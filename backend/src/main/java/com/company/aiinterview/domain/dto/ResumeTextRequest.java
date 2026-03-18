package com.company.aiinterview.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResumeTextRequest {
    @NotBlank(message = "resume text required")
    private String text;
}
