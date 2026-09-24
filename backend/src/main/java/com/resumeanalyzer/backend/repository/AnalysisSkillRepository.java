package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.AnalysisSkill;
import com.resumeanalyzer.backend.entity.AnalysisSkill.SkillMatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisSkillRepository
        extends JpaRepository<AnalysisSkill, Long> {

    List<AnalysisSkill> findByAnalysisId(Long analysisId);

    List<AnalysisSkill> findByAnalysisIdAndStatus(
            Long analysisId,
            SkillMatchStatus status
    );

    List<AnalysisSkill> findByAnalysisIdOrderByImportanceDesc(
            Long analysisId
    );

    Optional<AnalysisSkill> findByAnalysisIdAndSkillId(
            Long analysisId,
            Long skillId
    );

    boolean existsByAnalysisIdAndSkillId(
            Long analysisId,
            Long skillId
    );
}