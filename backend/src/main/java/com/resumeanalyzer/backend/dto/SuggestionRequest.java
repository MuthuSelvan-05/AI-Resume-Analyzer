package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.Suggestion.SuggestionCategory;
import com.resumeanalyzer.backend.entity.Suggestion.SuggestionPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuggestionRequest {

    @NotNull(message = "Suggestion category is required")
    private SuggestionCategory category;

    @NotBlank(message = "Suggestion title is required")
    @Size(max = 200, message = "Suggestion title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Suggestion description is required")
    private String description;

    @NotNull(message = "Suggestion priority is required")
    private SuggestionPriority priority;
}