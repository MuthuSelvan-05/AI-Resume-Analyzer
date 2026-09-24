package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.SkillProgressRequest;
import com.resumeanalyzer.backend.dto.SkillProgressResponse;
import com.resumeanalyzer.backend.service.SkillProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-progress")
@RequiredArgsConstructor
public class SkillProgressController {

    private final SkillProgressService skillProgressService;

    @PostMapping
    public ResponseEntity<SkillProgressResponse> createProgress(
            Authentication authentication,
            @Valid @RequestBody SkillProgressRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        skillProgressService.createProgress(
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<SkillProgressResponse>> getMyProgress(
            Authentication authentication) {

        return ResponseEntity.ok(
                skillProgressService.getMyProgress(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{progressId}")
    public ResponseEntity<SkillProgressResponse> getProgress(
            Authentication authentication,
            @PathVariable Long progressId) {

        return ResponseEntity.ok(
                skillProgressService.getProgress(
                        authentication.getName(),
                        progressId
                )
        );
    }

    @PutMapping("/{progressId}")
    public ResponseEntity<SkillProgressResponse> updateProgress(
            Authentication authentication,
            @PathVariable Long progressId,
            @Valid @RequestBody SkillProgressRequest request) {

        return ResponseEntity.ok(
                skillProgressService.updateProgress(
                        authentication.getName(),
                        progressId,
                        request
                )
        );
    }

    @DeleteMapping("/{progressId}")
    public ResponseEntity<Void> deleteProgress(
            Authentication authentication,
            @PathVariable Long progressId) {

        skillProgressService.deleteProgress(
                authentication.getName(),
                progressId
        );

        return ResponseEntity.noContent().build();
    }
}