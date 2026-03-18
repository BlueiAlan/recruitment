package com.company.aiinterview.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterviewStartRequest {
    @NotBlank(message = "resumeId required")
    private String resumeId;

    @NotBlank(message = "jd required")
    private String jdText;
}
