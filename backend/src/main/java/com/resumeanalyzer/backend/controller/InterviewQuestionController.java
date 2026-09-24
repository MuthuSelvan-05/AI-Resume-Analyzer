package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.InterviewQuestionRequest;
import com.resumeanalyzer.backend.dto.InterviewQuestionResponse;
import com.resumeanalyzer.backend.service.InterviewQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview/questions")
@RequiredArgsConstructor
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;

    @PostMapping
    public ResponseEntity<InterviewQuestionResponse> createQuestion(
            Authentication authentication,
            @Valid @RequestBody InterviewQuestionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        interviewQuestionService.createQuestion(
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<InterviewQuestionResponse>> getMyQuestions(
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewQuestionService.getMyQuestions(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/analysis/{analysisId}")
    public ResponseEntity<List<InterviewQuestionResponse>>
    getQuestionsByAnalysis(
            Authentication authentication,
            @PathVariable Long analysisId) {

        return ResponseEntity.ok(
                interviewQuestionService.getQuestionsByAnalysis(
                        authentication.getName(),
                        analysisId
                )
        );
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<InterviewQuestionResponse> getQuestion(
            Authentication authentication,
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                interviewQuestionService.getQuestion(
                        authentication.getName(),
                        questionId
                )
        );
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            Authentication authentication,
            @PathVariable Long questionId) {

        interviewQuestionService.deleteQuestion(
                authentication.getName(),
                questionId
        );

        return ResponseEntity.noContent().build();
    }
}