package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.InterviewGenerationRequest;
import com.resumeanalyzer.backend.dto.InterviewGenerationResponse;
import com.resumeanalyzer.backend.service.InterviewGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview/generation")
@RequiredArgsConstructor
public class InterviewGenerationController {

    private final InterviewGenerationService interviewGenerationService;

    @PostMapping
    public ResponseEntity<InterviewGenerationResponse> generateQuestions(
            Authentication authentication,
            @Valid @RequestBody InterviewGenerationRequest request) {

        return ResponseEntity.ok(
                interviewGenerationService.generateQuestions(
                        authentication.getName(),
                        request
                )
        );
    }
}