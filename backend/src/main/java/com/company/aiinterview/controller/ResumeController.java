package com.company.aiinterview.controller;

import com.company.aiinterview.common.Result;
import com.company.aiinterview.domain.dto.ResumeTextRequest;
import com.company.aiinterview.domain.entity.Resume;
import com.company.aiinterview.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestPart("file") MultipartFile file) {
        Resume resume = resumeService.upload(file);
        return Result.ok(Map.of("resumeId", resume.getId()));
    }

    @PostMapping("/text")
    public Result<Map<String, String>> text(@Valid @RequestBody ResumeTextRequest req) {
        Resume resume = resumeService.saveText(req.getText());
        return Result.ok(Map.of("resumeId", resume.getId()));
    }
}
