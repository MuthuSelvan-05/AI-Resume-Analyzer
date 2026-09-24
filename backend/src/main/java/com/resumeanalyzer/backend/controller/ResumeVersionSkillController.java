package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.ResumeVersionSkillRequest;
import com.resumeanalyzer.backend.dto.ResumeVersionSkillResponse;
import com.resumeanalyzer.backend.service.ResumeVersionSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/resumes/{resumeId}/versions/{versionNumber}/skills"
)
@RequiredArgsConstructor
public class ResumeVersionSkillController {

    private final ResumeVersionSkillService resumeVersionSkillService;

    @PostMapping
    public ResponseEntity<ResumeVersionSkillResponse> addSkill(
            Authentication authentication,
            @PathVariable Long resumeId,
            @PathVariable Integer versionNumber,
            @Valid @RequestBody ResumeVersionSkillRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        resumeVersionSkillService.addSkill(
                                authentication.getName(),
                                resumeId,
                                versionNumber,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ResumeVersionSkillResponse>> getSkills(
            Authentication authentication,
            @PathVariable Long resumeId,
            @PathVariable Integer versionNumber) {

        return ResponseEntity.ok(
                resumeVersionSkillService.getSkills(
                        authentication.getName(),
                        resumeId,
                        versionNumber
                )
        );
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<ResumeVersionSkillResponse> updateSkill(
            Authentication authentication,
            @PathVariable Long resumeId,
            @PathVariable Integer versionNumber,
            @PathVariable Long skillId,
            @Valid @RequestBody ResumeVersionSkillRequest request) {

        return ResponseEntity.ok(
                resumeVersionSkillService.updateSkill(
                        authentication.getName(),
                        resumeId,
                        versionNumber,
                        skillId,
                        request
                )
        );
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> removeSkill(
            Authentication authentication,
            @PathVariable Long resumeId,
            @PathVariable Integer versionNumber,
            @PathVariable Long skillId) {

        resumeVersionSkillService.removeSkill(
                authentication.getName(),
                resumeId,
                versionNumber,
                skillId
        );

        return ResponseEntity.noContent().build();
    }
}