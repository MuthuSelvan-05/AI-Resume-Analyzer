package com.resumeanalyzer.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "analyses",
        indexes = {
                @Index(name = "idx_analysis_user", columnList = "user_id"),
                @Index(name = "idx_analysis_resume", columnList = "resume_version_id"),
                @Index(name = "idx_analysis_job", columnList = "job_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double overallScore;

    @Column(nullable = false)
    private Double skillScore;

    @Column(nullable = false)
    private Double experienceScore;

    @Column(nullable = false)
    private Double educationScore;

    @Column(nullable = false)
    private Double keywordScore;

    @Column(nullable = false)
    private Double semanticScore;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_version_id", nullable = false)
    private ResumeVersion resumeVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}