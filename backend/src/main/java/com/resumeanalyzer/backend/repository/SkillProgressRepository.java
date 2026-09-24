package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.SkillProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillProgressRepository extends JpaRepository<SkillProgress, Long> {

    List<SkillProgress> findByUserId(Long userId);

    Optional<SkillProgress> findByIdAndUserId(Long id, Long userId);

    Optional<SkillProgress> findByUserIdAndSkillId(Long userId, Long skillId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);
}