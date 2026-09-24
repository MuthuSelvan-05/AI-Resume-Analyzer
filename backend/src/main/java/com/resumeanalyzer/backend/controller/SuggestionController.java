package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.SuggestionRequest;
import com.resumeanalyzer.backend.dto.SuggestionResponse;
import com.resumeanalyzer.backend.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyses/{analysisId}/suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    @PostMapping
    public ResponseEntity<SuggestionResponse> createSuggestion(
            Authentication authentication,
            @PathVariable Long analysisId,
            @Valid @RequestBody SuggestionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        suggestionService.createSuggestion(
                                authentication.getName(),
                                analysisId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<SuggestionResponse>> getSuggestions(
            Authentication authentication,
            @PathVariable Long analysisId) {

        return ResponseEntity.ok(
                suggestionService.getSuggestions(
                        authentication.getName(),
                        analysisId
                )
        );
    }

    @GetMapping("/{suggestionId}")
    public ResponseEntity<SuggestionResponse> getSuggestion(
            Authentication authentication,
            @PathVariable Long analysisId,
            @PathVariable Long suggestionId) {

        return ResponseEntity.ok(
                suggestionService.getSuggestion(
                        authentication.getName(),
                        analysisId,
                        suggestionId
                )
        );
    }

    @PutMapping("/{suggestionId}")
    public ResponseEntity<SuggestionResponse> updateSuggestion(
            Authentication authentication,
            @PathVariable Long analysisId,
            @PathVariable Long suggestionId,
            @Valid @RequestBody SuggestionRequest request) {

        return ResponseEntity.ok(
                suggestionService.updateSuggestion(
                        authentication.getName(),
                        analysisId,
                        suggestionId,
                        request
                )
        );
    }

    @DeleteMapping("/{suggestionId}")
    public ResponseEntity<Void> deleteSuggestion(
            Authentication authentication,
            @PathVariable Long analysisId,
            @PathVariable Long suggestionId) {

        suggestionService.deleteSuggestion(
                authentication.getName(),
                analysisId,
                suggestionId
        );

        return ResponseEntity.noContent().build();
    }
}