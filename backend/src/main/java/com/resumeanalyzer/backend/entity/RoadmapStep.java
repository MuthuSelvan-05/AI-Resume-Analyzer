package com.resumeanalyzer.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "learning_roadmap_steps",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_roadmap_step_order",
                        columnNames = {"roadmap_id", "step_order"}
                )
        },
        indexes = {
                @Index(name = "idx_roadmap_step_roadmap", columnList = "roadmap_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String resourceUrl;

    @Column(length = 100)
    private String resourceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StepStatus status;

    @Column(nullable = false)
    private Integer progressPercentage;

    public enum StepStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = StepStatus.NOT_STARTED;
        }

        if (progressPercentage == null) {
            progressPercentage = 0;
        }
    }
}