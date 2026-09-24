package com.resumeanalyzer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AnalysisResponse {

    private Long id;

    private Long resumeVersionId;

    private Long jobId;

    private Double overallScore;

    private Double skillScore;

    private Double experienceScore;

    private Double educationScore;

    private Double keywordScore;

    private Double semanticScore;

    private List<String> matchedSkills;

    private List<String> missingSkills;

    private List<String> relatedSkills;

    private List<SuggestionResponse> suggestions;

    private LocalDateTime createdAt;
}