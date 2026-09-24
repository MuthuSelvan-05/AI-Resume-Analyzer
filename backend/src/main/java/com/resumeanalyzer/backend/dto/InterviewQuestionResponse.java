package com.resumeanalyzer.backend.dto;

import com.resumeanalyzer.backend.entity.InterviewQuestion;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InterviewQuestionResponse {

    private Long id;
    private Long analysisId;
    private String question;
    private InterviewQuestion.QuestionCategory category;
    private InterviewQuestion.Difficulty difficulty;
    private String expectedTopics;
    private LocalDateTime createdAt;
}