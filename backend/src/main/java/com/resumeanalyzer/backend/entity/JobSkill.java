package com.resumeanalyzer.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "job_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_skill",
                        columnNames = {"job_id", "skill_id"}
                )
        },
        indexes = {
                @Index(name = "idx_job_skill_job", columnList = "job_id"),
                @Index(name = "idx_job_skill_skill", columnList = "skill_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean required;

    @Column(nullable = false)
    private Integer importance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}