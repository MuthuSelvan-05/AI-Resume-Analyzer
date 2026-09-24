package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.JobSkillRequest;
import com.resumeanalyzer.backend.dto.JobSkillResponse;
import com.resumeanalyzer.backend.service.JobSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/{jobId}/skills")
@RequiredArgsConstructor
public class JobSkillController {

    private final JobSkillService jobSkillService;

    @PostMapping
    public ResponseEntity<JobSkillResponse> addSkillToJob(
            Authentication authentication,
            @PathVariable Long jobId,
            @Valid @RequestBody JobSkillRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        jobSkillService.addSkillToJob(
                                authentication.getName(),
                                jobId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<JobSkillResponse>> getJobSkills(
            Authentication authentication,
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                jobSkillService.getJobSkills(
                        authentication.getName(),
                        jobId
                )
        );
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<JobSkillResponse> updateJobSkill(
            Authentication authentication,
            @PathVariable Long jobId,
            @PathVariable Long skillId,
            @Valid @RequestBody JobSkillRequest request) {

        return ResponseEntity.ok(
                jobSkillService.updateJobSkill(
                        authentication.getName(),
                        jobId,
                        skillId,
                        request
                )
        );
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> removeSkillFromJob(
            Authentication authentication,
            @PathVariable Long jobId,
            @PathVariable Long skillId) {

        jobSkillService.removeSkillFromJob(
                authentication.getName(),
                jobId,
                skillId
        );

        return ResponseEntity.noContent().build();
    }
}