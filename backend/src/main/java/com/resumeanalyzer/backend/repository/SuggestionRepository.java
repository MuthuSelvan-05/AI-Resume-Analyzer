package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.Suggestion;
import com.resumeanalyzer.backend.entity.Suggestion.SuggestionCategory;
import com.resumeanalyzer.backend.entity.Suggestion.SuggestionPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    List<Suggestion> findByAnalysisId(Long analysisId);

    List<Suggestion> findByAnalysisIdOrderByPriorityAsc(Long analysisId);

    List<Suggestion> findByAnalysisIdAndCategory(
            Long analysisId,
            SuggestionCategory category
    );

    List<Suggestion> findByAnalysisIdAndPriority(
            Long analysisId,
            SuggestionPriority priority
    );
}