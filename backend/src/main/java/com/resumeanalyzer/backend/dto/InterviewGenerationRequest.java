package com.resumeanalyzer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewGenerationRequest {

    private Long analysisId;

    private String resumeText;

    private String jobTitle;

    private String jobDescription;

    private List<String> matchedSkills;

    private List<String> missingSkills;

    private Integer questionCount;
}