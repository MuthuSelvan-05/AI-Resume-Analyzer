package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.ResumeVersionSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeVersionSkillRepository
        extends JpaRepository<ResumeVersionSkill, Long> {

    List<ResumeVersionSkill> findByResumeVersionId(Long resumeVersionId);

    List<ResumeVersionSkill> findByResumeVersionIdOrderByConfidenceDesc(
            Long resumeVersionId
    );

    Optional<ResumeVersionSkill> findByResumeVersionIdAndSkillId(
            Long resumeVersionId,
            Long skillId
    );

    boolean existsByResumeVersionIdAndSkillId(
            Long resumeVersionId,
            Long skillId
    );
}