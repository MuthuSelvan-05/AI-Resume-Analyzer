package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.ResumeVersionSkill.SkillSource;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResumeVersionSkillResponse {

    private Long id;
    private Long skillId;
    private String skillName;
    private String category;
    private Double confidence;
    private SkillSource source;
}