package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    List<Analysis> findByUserId(Long userId);

    List<Analysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Analysis> findByResumeVersionId(Long resumeVersionId);

    List<Analysis> findByJobId(Long jobId);

    List<Analysis> findByUserIdAndJobId(
            Long userId,
            Long jobId
    );

    List<Analysis> findByUserIdAndResumeVersionId(
            Long userId,
            Long resumeVersionId
    );
}