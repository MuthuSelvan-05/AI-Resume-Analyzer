package com.resumeanalyzer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InterviewGenerationResponse {

    private List<GeneratedInterviewQuestion> questions;

    @Getter
    @AllArgsConstructor
    public static class GeneratedInterviewQuestion {

        private String questionText;
        private String questionType;
        private String difficulty;
        private String skillName;
    }
}