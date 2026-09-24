package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.RoadmapStep;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoadmapStepProgressRequest {

    @NotNull
    private RoadmapStep.StepStatus status;

    @Min(0)
    @Max(100)
    private Integer progressPercentage;
}