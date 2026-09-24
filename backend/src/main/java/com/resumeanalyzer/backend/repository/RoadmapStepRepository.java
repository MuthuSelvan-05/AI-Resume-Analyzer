package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoadmapStepRepository
        extends JpaRepository<RoadmapStep, Long> {

    List<RoadmapStep> findByRoadmapIdOrderByStepOrderAsc(
            Long roadmapId
    );

    Optional<RoadmapStep> findByIdAndRoadmapId(
            Long id,
            Long roadmapId
    );

    boolean existsByRoadmapIdAndStepOrder(
            Long roadmapId,
            Integer stepOrder
    );

    @Query("""
            SELECT rs
            FROM RoadmapStep rs
            JOIN FETCH rs.roadmap r
            JOIN FETCH r.skill s
            WHERE s.id = :skillId
            ORDER BY rs.stepOrder ASC
            """)
    List<RoadmapStep> findStepsWithRoadmapAndSkillBySkillId(
            Long skillId
    );
}