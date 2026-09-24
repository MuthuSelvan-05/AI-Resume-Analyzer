package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.InterviewQuestion.Difficulty;
import com.resumeanalyzer.backend.entity.InterviewQuestion.QuestionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewQuestionRequest {

    private Long analysisId;

    @NotBlank(message = "Question is required")
    @Size(max = 1000, message = "Question must not exceed 1000 characters")
    private String question;

    @NotNull(message = "Question category is required")
    private QuestionCategory category;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @Size(max = 2000, message = "Expected topics must not exceed 2000 characters")
    private String expectedTopics;
}