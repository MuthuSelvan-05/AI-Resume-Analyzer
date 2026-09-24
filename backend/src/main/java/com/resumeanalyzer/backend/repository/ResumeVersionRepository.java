package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.ResumeVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, Long> {

    List<ResumeVersion> findByResumeIdOrderByVersionNumberDesc(Long resumeId);

    Optional<ResumeVersion> findByResumeIdAndVersionNumber(
            Long resumeId,
            Integer versionNumber
    );

    Optional<ResumeVersion> findTopByResumeIdOrderByVersionNumberDesc(
            Long resumeId
    );
}