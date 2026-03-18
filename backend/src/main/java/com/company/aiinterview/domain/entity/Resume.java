package com.company.aiinterview.domain.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class Resume {
    private String id;
    private String text;
    private String fileName;
    private Instant createdAt;
}
