package com.resumeanalyzer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 150, message = "Job title must not exceed 150 characters")
    private String title;

    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String company;

    @NotBlank(message = "Job description is required")
    private String description;

    @Size(max = 150, message = "Location must not exceed 150 characters")
    private String location;
}