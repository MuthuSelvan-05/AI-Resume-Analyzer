package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.JobRequest;
import com.resumeanalyzer.backend.dto.JobResponse;
import com.resumeanalyzer.backend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            Authentication authentication,
            @Valid @RequestBody JobRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        jobService.createJob(
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getMyJobs(
            Authentication authentication) {

        return ResponseEntity.ok(
                jobService.getMyJobs(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(
            Authentication authentication,
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                jobService.getJob(
                        authentication.getName(),
                        jobId
                )
        );
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            Authentication authentication,
            @PathVariable Long jobId,
            @Valid @RequestBody JobRequest request) {

        return ResponseEntity.ok(
                jobService.updateJob(
                        authentication.getName(),
                        jobId,
                        request
                )
        );
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(
            Authentication authentication,
            @PathVariable Long jobId) {

        jobService.deleteJob(
                authentication.getName(),
                jobId
        );

        return ResponseEntity.noContent().build();
    }
}