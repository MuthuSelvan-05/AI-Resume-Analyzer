package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.InterviewAnswerRequest;
import com.resumeanalyzer.backend.dto.InterviewAnswerResponse;
import com.resumeanalyzer.backend.service.InterviewAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview/answers")
@RequiredArgsConstructor
public class InterviewAnswerController {

    private final InterviewAnswerService interviewAnswerService;

    @PostMapping
    public ResponseEntity<InterviewAnswerResponse> submitAnswer(
            Authentication authentication,
            @Valid @RequestBody InterviewAnswerRequest request) {

        return ResponseEntity.ok(
                interviewAnswerService.submitAnswer(
                        authentication.getName(),
                        request
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<InterviewAnswerResponse>> getMyAnswers(
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewAnswerService.getMyAnswers(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<InterviewAnswerResponse>>
    getAnswersForQuestion(
            Authentication authentication,
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                interviewAnswerService.getAnswersForQuestion(
                        authentication.getName(),
                        questionId
                )
        );
    }
}