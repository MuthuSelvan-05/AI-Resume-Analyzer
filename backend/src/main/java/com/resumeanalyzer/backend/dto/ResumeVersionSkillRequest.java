package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.ResumeVersionSkill.SkillSource;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeVersionSkillRequest {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @DecimalMin(value = "0.0", message = "Confidence cannot be below 0")
    @DecimalMax(value = "1.0", message = "Confidence cannot exceed 1")
    private Double confidence;

    @NotNull(message = "Skill source is required")
    private SkillSource source;
}