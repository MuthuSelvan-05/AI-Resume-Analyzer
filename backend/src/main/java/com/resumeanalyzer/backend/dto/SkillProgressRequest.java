package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.SkillProgress;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SkillProgressRequest {

    @NotNull
    private Long skillId;

    @NotNull
    private SkillProgress.ProgressStatus status;

    @Min(0)
    @Max(100)
    private Integer progressPercentage;

    private LocalDate targetDate;
}