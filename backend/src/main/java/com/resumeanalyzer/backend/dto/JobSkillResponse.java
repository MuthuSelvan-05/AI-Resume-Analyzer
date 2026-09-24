package com.resumeanalyzer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobSkillResponse {

    private Long id;
    private Long skillId;
    private String skillName;
    private Boolean required;
    private Integer importance;
}