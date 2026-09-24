package com.resumeanalyzer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResumeVersionResponse {

    private Long id;
    private Integer versionNumber;
    private String fileName;
    private String fileType;
    private LocalDateTime uploadedAt;
}