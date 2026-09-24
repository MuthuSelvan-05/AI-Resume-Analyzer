package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.AnalysisSkillRequest;
import com.resumeanalyzer.backend.dto.AnalysisSkillResponse;
import com.resumeanalyzer.backend.service.AnalysisSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyses/{analysisId}/skills")
@RequiredArgsConstructor
public class AnalysisSkillController {

    private final AnalysisSkillService analysisSkillService;

    @PostMapping
    public ResponseEntity<AnalysisSkillResponse> addSkill(
            Authentication authentication,
            @PathVariable Long analysisId,
            @Valid @RequestBody AnalysisSkillRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        analysisSkillService.addSkill(
                                authentication.getName(),
                                analysisId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<AnalysisSkillResponse>> getSkills(
            Authentication authentication,
            @PathVariable Long analysisId) {

        return ResponseEntity.ok(
                analysisSkillService.getSkills(
                        authentication.getName(),
                        analysisId
                )
        );
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<AnalysisSkillResponse> updateSkill(
            Authentication authentication,
            @PathVariable Long analysisId,
            @PathVariable Long skillId,
            @Valid @RequestBody AnalysisSkillRequest request) {

        return ResponseEntity.ok(
                analysisSkillService.updateSkill(
                        authentication.getName(),
                        analysisId,
                        skillId,
                        request
                )
        );
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> removeSkill(
            Authentication authentication,
            @PathVariable Long analysisId,
            @PathVariable Long skillId) {

        analysisSkillService.removeSkill(
                authentication.getName(),
                analysisId,
                skillId
        );

        return ResponseEntity.noContent().build();
    }
}