package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.SkillProgress;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SkillProgressResponse {

    private Long id;
    private Long skillId;
    private String skillName;
    private String skillCategory;
    private SkillProgress.ProgressStatus status;
    private Integer progressPercentage;
    private LocalDate targetDate;
    private LocalDateTime updatedAt;
}