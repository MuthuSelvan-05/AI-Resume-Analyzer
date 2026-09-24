package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.LearningResource;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LearningResourceResponse {

    private Long id;

    private String title;

    private String description;

    private String url;

    private LearningResource.ResourceType resourceType;

    private LearningResource.ResourceLevel level;
}