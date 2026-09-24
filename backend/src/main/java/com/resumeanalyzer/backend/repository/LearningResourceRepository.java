package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningResourceRepository
        extends JpaRepository<LearningResource, Long> {

    List<LearningResource> findBySkillId(Long skillId);

    List<LearningResource> findBySkillIdOrderByIdAsc(Long skillId);

    List<LearningResource> findByRoadmapStepIdOrderByIdAsc(Long roadmapStepId);
}