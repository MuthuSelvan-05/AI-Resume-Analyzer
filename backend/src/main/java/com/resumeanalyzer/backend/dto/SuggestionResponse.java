package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.Suggestion.SuggestionCategory;
import com.resumeanalyzer.backend.entity.Suggestion.SuggestionPriority;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuggestionResponse {

    private Long id;
    private SuggestionCategory category;
    private String title;
    private String description;
    private SuggestionPriority priority;
}