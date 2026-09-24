package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.AnalysisSkill.SkillMatchStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalysisSkillRequest {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Skill match status is required")
    private SkillMatchStatus status;

    @NotNull(message = "Importance is required")
    @Min(value = 1, message = "Importance must be at least 1")
    private Integer importance;
}