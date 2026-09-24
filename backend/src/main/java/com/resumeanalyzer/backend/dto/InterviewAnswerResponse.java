package com.resumeanalyzer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InterviewAnswerResponse {

    private Long id;
    private Long questionId;
    private String answer;
    private Double score;
    private String feedback;
    private LocalDateTime answeredAt;
}