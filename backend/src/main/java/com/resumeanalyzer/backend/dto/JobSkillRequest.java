package com.resumeanalyzer.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobSkillRequest {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Required status is required")
    private Boolean required;

    @NotNull(message = "Importance is required")
    @Min(value = 1, message = "Importance must be at least 1")
    private Integer importance;
}