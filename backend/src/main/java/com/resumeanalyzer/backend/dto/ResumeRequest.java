package com.resumeanalyzer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeRequest {

    @NotBlank(message = "Resume title is required")
    @Size(max = 150, message = "Resume title must not exceed 150 characters")
    private String title;
}