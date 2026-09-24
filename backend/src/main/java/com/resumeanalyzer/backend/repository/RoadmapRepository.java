package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    List<Roadmap> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Roadmap> findByIdAndUserId(Long id, Long userId);

    Optional<Roadmap> findByUserIdAndSkillId(Long userId, Long skillId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);
}