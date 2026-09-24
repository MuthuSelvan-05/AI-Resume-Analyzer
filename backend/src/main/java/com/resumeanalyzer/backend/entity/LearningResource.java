package com.resumeanalyzer.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "learning_resources",
        indexes = {
                @Index(name = "idx_resource_skill", columnList = "skill_id"),
                @Index(name = "idx_resource_roadmap_step", columnList = "roadmap_step_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 500)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResourceLevel level;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_step_id")
    private RoadmapStep roadmapStep;

    public enum ResourceType {
        DOCUMENTATION,
        TUTORIAL,
        VIDEO,
        COURSE,
        PRACTICE,
        PROJECT,
        INTERVIEW
    }

    public enum ResourceLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }
}