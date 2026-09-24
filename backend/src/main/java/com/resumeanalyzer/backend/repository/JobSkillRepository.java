package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {

    List<JobSkill> findByJobId(Long jobId);

    List<JobSkill> findByJobIdOrderByImportanceDesc(Long jobId);

    List<JobSkill> findByJobIdAndRequiredTrue(Long jobId);

    List<JobSkill> findByJobIdAndRequiredFalse(Long jobId);

    Optional<JobSkill> findByJobIdAndSkillId(
            Long jobId,
            Long skillId
    );

    boolean existsByJobIdAndSkillId(
            Long jobId,
            Long skillId
    );
}