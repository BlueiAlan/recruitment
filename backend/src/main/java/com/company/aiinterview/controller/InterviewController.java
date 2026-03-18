package com.company.aiinterview.controller;

import com.company.aiinterview.common.Result;
import com.company.aiinterview.domain.dto.AnswerRequest;
import com.company.aiinterview.domain.dto.InterviewStartRequest;
import com.company.aiinterview.domain.vo.AnswerFeedbackVO;
import com.company.aiinterview.domain.vo.InterviewResultResponse;
import com.company.aiinterview.domain.vo.InterviewStartResponse;
import com.company.aiinterview.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/start")
    public Result<InterviewStartResponse> start(@Valid @RequestBody InterviewStartRequest request) {
        return Result.ok(interviewService.start(request));
    }

    @PostMapping("/answer")
    public Result<AnswerFeedbackVO> answer(@Valid @RequestBody AnswerRequest request) {
        return Result.ok(interviewService.answer(request));
    }

    @GetMapping("/result")
    public Result<InterviewResultResponse> result(@RequestParam String sessionId) {
        return Result.ok(interviewService.result(sessionId));
    }
}
