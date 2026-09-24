package com.resumeanalyzer.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalysisRequest {

    @NotNull(message = "Resume version ID is required")
    private Long resumeVersionId;

    @NotNull(message = "Job ID is required")
    private Long jobId;
}