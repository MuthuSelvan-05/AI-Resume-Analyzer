package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.ResumeVersionResponse;
import com.resumeanalyzer.backend.service.ResumeVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes/{resumeId}/versions")
@RequiredArgsConstructor
public class ResumeVersionController {

    private final ResumeVersionService resumeVersionService;

    @PostMapping
    public ResponseEntity<ResumeVersionResponse> uploadResumeVersion(
            Authentication authentication,
            @PathVariable Long resumeId,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity
                .status(201)
                .body(
                        resumeVersionService.uploadResumeVersion(
                                authentication.getName(),
                                resumeId,
                                file
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ResumeVersionResponse>> getResumeVersions(
            Authentication authentication,
            @PathVariable Long resumeId) {

        return ResponseEntity.ok(
                resumeVersionService.getResumeVersions(
                        authentication.getName(),
                        resumeId
                )
        );
    }

    @GetMapping("/{versionNumber}/download")
    public ResponseEntity<Resource> downloadResumeVersion(
            Authentication authentication,
            @PathVariable Long resumeId,
            @PathVariable Integer versionNumber) {

        Resource resource =
                resumeVersionService.getResumeFile(
                        authentication.getName(),
                        resumeId,
                        versionNumber
                );

        String fileName = resource.getFilename();

        MediaType mediaType =
                MediaType.APPLICATION_OCTET_STREAM;

        if (fileName != null
                && fileName.toLowerCase().endsWith(".pdf")) {

            mediaType = MediaType.APPLICATION_PDF;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .body(resource);
    }
}