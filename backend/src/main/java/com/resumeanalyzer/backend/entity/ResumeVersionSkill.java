package com.resumeanalyzer.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "resume_version_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_resume_version_skill",
                        columnNames = {"resume_version_id", "skill_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_rvs_resume_version",
                        columnList = "resume_version_id"
                ),
                @Index(
                        name = "idx_rvs_skill",
                        columnList = "skill_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeVersionSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double confidence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SkillSource source;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_version_id", nullable = false)
    private ResumeVersion resumeVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    public enum SkillSource {
        AI_EXTRACTED,
        USER_ADDED
    }
}