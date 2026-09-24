package com.resumeanalyzer.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "suggestions",
        indexes = {
                @Index(name = "idx_suggestion_analysis", columnList = "analysis_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SuggestionCategory category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SuggestionPriority priority;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;

    public enum SuggestionCategory {
        RESUME,
        SKILL,
        EXPERIENCE,
        EDUCATION,
        KEYWORD,
        PROJECT
    }

    public enum SuggestionPriority {
        HIGH,
        MEDIUM,
        LOW
    }
}