package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewQuestionRepository
        extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<InterviewQuestion> findByUserIdAndAnalysisId(
            Long userId,
            Long analysisId
    );
}