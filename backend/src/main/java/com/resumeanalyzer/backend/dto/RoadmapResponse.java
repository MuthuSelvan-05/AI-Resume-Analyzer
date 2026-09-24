package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.Roadmap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RoadmapResponse {

    private Long id;
    private String title;
    private String description;
    private Long skillId;
    private String skillName;
    private Roadmap.RoadmapStatus status;
    private Integer progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}