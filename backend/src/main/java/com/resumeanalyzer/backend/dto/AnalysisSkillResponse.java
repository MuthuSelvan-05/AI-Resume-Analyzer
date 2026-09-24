package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.AnalysisSkill.SkillMatchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalysisSkillResponse {

    private Long id;
    private Long skillId;
    private String skillName;
    private SkillMatchStatus status;
    private Integer importance;
}