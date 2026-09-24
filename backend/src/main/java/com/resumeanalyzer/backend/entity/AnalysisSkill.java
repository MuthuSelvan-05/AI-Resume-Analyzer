package com.resumeanalyzer.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "analysis_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_analysis_skill",
                        columnNames = {"analysis_id", "skill_id"}
                )
        },
        indexes = {
                @Index(name = "idx_analysis_skill_analysis", columnList = "analysis_id"),
                @Index(name = "idx_analysis_skill_skill", columnList = "skill_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SkillMatchStatus status;

    @Column(nullable = false)
    private Integer importance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    public enum SkillMatchStatus {
        MATCHED,
        MISSING,
        RELATED
    }
}