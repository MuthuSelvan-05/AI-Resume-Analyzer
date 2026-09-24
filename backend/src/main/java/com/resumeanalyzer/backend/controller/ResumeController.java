package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.ResumeRequest;
import com.resumeanalyzer.backend.dto.ResumeResponse;
import com.resumeanalyzer.backend.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<ResumeResponse> createResume(
            Authentication authentication,
            @Valid @RequestBody ResumeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        resumeService.createResume(
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getMyResumes(
            Authentication authentication) {

        return ResponseEntity.ok(
                resumeService.getMyResumes(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> getResume(
            Authentication authentication,
            @PathVariable Long resumeId) {

        return ResponseEntity.ok(
                resumeService.getResume(
                        authentication.getName(),
                        resumeId
                )
        );
    }

    @PutMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> updateResume(
            Authentication authentication,
            @PathVariable Long resumeId,
            @Valid @RequestBody ResumeRequest request) {

        return ResponseEntity.ok(
                resumeService.updateResume(
                        authentication.getName(),
                        resumeId,
                        request
                )
        );
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(
            Authentication authentication,
            @PathVariable Long resumeId) {

        resumeService.deleteResume(
                authentication.getName(),
                resumeId
        );

        return ResponseEntity.noContent().build();
    }
}