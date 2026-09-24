package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.RoadmapStep;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoadmapStepResponse {

    private Long id;
    private Integer stepOrder;
    private String title;
    private String description;
    private String resourceUrl;
    private String resourceType;
    private RoadmapStep.StepStatus status;
    private Integer progressPercentage;
}