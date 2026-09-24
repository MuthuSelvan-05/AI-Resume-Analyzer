package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.AnalysisRequest;
import com.resumeanalyzer.backend.dto.AnalysisResponse;
import com.resumeanalyzer.backend.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<AnalysisResponse> createAnalysis(
            Authentication authentication,
            @Valid @RequestBody AnalysisRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        analysisService.createAnalysis(
                                authentication.getName(),
                                request
                        )
                );
    }

    @PostMapping("/{analysisId}/run")
    public ResponseEntity<AnalysisResponse> runAnalysis(
            Authentication authentication,
            @PathVariable Long analysisId) {

        return ResponseEntity.ok(
                analysisService.runAnalysis(
                        authentication.getName(),
                        analysisId
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<AnalysisResponse>> getMyAnalyses(
            Authentication authentication) {

        return ResponseEntity.ok(
                analysisService.getMyAnalyses(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponse> getAnalysis(
            Authentication authentication,
            @PathVariable Long analysisId) {

        return ResponseEntity.ok(
                analysisService.getAnalysis(
                        authentication.getName(),
                        analysisId
                )
        );
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(
            Authentication authentication,
            @PathVariable Long analysisId) {

        analysisService.deleteAnalysis(
                authentication.getName(),
                analysisId
        );

        return ResponseEntity.noContent().build();
    }
}