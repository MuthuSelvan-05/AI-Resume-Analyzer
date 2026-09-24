package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByUserId(Long userId);

    List<Job> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByUserIdAndTitleContainingIgnoreCase(
            Long userId,
            String title
    );
}