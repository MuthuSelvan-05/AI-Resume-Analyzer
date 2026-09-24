package com.resumeanalyzer.backend.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.file.Path;
import java.util.List;

@Service
public class AiServiceClient {

    private final RestClient restClient;

    public AiServiceClient(
            @Value("${ai.service.url}") String aiServiceUrl
    ) {

        CloseableHttpClient httpClient =
                HttpClients.createDefault();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(
                        httpClient
                );

        this.restClient = RestClient
                .builder()
                .baseUrl(aiServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public ResumeData extractResumeFromFile(Path filePath) {

        if (filePath == null) {
            throw new IllegalArgumentException(
                    "Resume file path cannot be null"
            );
        }

        FileSystemResource fileResource =
                new FileSystemResource(filePath.toFile());

        if (!fileResource.exists()) {
            throw new IllegalArgumentException(
                    "Resume file not found: " + filePath
            );
        }

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add("file", fileResource);

        try {

            return restClient
                    .post()
                    .uri("/api/resume/extract-file")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(ResumeData.class);

        } catch (RestClientException exception) {

            throw new RuntimeException(
                    "Failed to call AI resume extraction service: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    public AnalysisResult analyzeResumeAgainstJob(
            String resumeText,
            ResumeInput resume,
            String jobText,
            JobInput job
    ) {

        AnalysisRequest request =
                new AnalysisRequest(
                        resumeText,
                        resume,
                        jobText,
                        job
                );

        try {

            return restClient
                    .post()
                    .uri("/api/analysis/match")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AnalysisResult.class);

        } catch (RestClientException exception) {

            throw new RuntimeException(
                    "Failed to call AI matching service: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    public List<SuggestionResult> generateSuggestions(
            String resumeText,
            AnalysisResult analysisResult
    ) {

        SuggestionRequest request =
                new SuggestionRequest(
                        resumeText,
                        analysisResult.matchedRequiredSkills(),
                        analysisResult.missingRequiredSkills(),
                        analysisResult.matchedPreferredSkills(),
                        analysisResult.missingPreferredSkills(),
                        analysisResult.additionalSkills(),
                        safeValue(analysisResult.experienceScore()),
                        safeValue(analysisResult.educationScore()),
                        safeValue(analysisResult.keywordScore())
                );

        try {

            List<SuggestionResult> result =
                    restClient
                            .post()
                            .uri("/api/suggestions/generate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            List<SuggestionResult>
                                            >() {
                                    }
                            );

            return result == null
                    ? List.of()
                    : result;

        } catch (RestClientException exception) {

            throw new RuntimeException(
                    "Failed to call AI suggestion service: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    public InterviewGenerationResponse generateInterviewQuestions(
            String resumeText,
            String jobTitle,
            String jobDescription,
            List<String> matchedSkills,
            List<String> missingSkills,
            Integer questionCount
    ) {

        InterviewGenerationRequest request =
                new InterviewGenerationRequest(
                        resumeText,
                        jobTitle,
                        jobDescription,
                        matchedSkills,
                        missingSkills,
                        questionCount
                );

        try {

            InterviewGenerationResponse response =
                    restClient
                            .post()
                            .uri("/api/interview/generate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(
                                    InterviewGenerationResponse.class
                            );

            return response == null
                    ? new InterviewGenerationResponse(List.of())
                    : response;

        } catch (RestClientException exception) {

            throw new RuntimeException(
                    "Failed to call AI interview generation service: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    public InterviewEvaluationResponse evaluateInterviewAnswer(
            String question,
            String answer,
            String category,
            String difficulty,
            List<String> expectedTopics
    ) {

        InterviewEvaluationRequest request =
                new InterviewEvaluationRequest(
                        question,
                        answer,
                        category,
                        difficulty,
                        expectedTopics
                );

        try {

            InterviewEvaluationResponse response =
                    restClient
                            .post()
                            .uri("/api/interview/evaluate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(
                                    InterviewEvaluationResponse.class
                            );

            if (response == null) {
                throw new RuntimeException(
                        "AI interview evaluation service returned an empty response"
                );
            }

            return response;

        } catch (RestClientException exception) {

            throw new RuntimeException(
                    "Failed to call AI interview evaluation service: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private double safeValue(Double value) {
        return value == null ? 0.0 : value;
    }

    public record AnalysisRequest(
            @JsonProperty("resume_text")
            String resumeText,

            ResumeInput resume,

            @JsonProperty("job_text")
            String jobText,

            JobInput job
    ) {
    }

    public record SuggestionRequest(
            @JsonProperty("resume_text")
            String resumeText,

            @JsonProperty("matched_required_skills")
            List<String> matchedRequiredSkills,

            @JsonProperty("missing_required_skills")
            List<String> missingRequiredSkills,

            @JsonProperty("matched_preferred_skills")
            List<String> matchedPreferredSkills,

            @JsonProperty("missing_preferred_skills")
            List<String> missingPreferredSkills,

            @JsonProperty("additional_skills")
            List<String> additionalSkills,

            @JsonProperty("experience_score")
            Double experienceScore,

            @JsonProperty("education_score")
            Double educationScore,

            @JsonProperty("keyword_score")
            Double keywordScore
    ) {
    }

    public record InterviewGenerationRequest(
            String resumeText,
            String jobTitle,
            String jobDescription,
            List<String> matchedSkills,
            List<String> missingSkills,
            Integer questionCount
    ) {
    }

    public record InterviewGenerationResponse(
            List<GeneratedInterviewQuestion> questions
    ) {

        public record GeneratedInterviewQuestion(
                String questionText,
                String questionType,
                String difficulty,
                String skillName
        ) {
        }
    }

    public record InterviewEvaluationRequest(
            String question,
            String answer,
            String category,
            String difficulty,

            @JsonProperty("expected_topics")
            List<String> expectedTopics
    ) {
    }

    public record InterviewEvaluationResponse(

            @JsonProperty("overall_score")
            double overallScore,

            @JsonProperty("relevance_score")
            double relevanceScore,

            @JsonProperty("completeness_score")
            double completenessScore,

            @JsonProperty("technical_score")
            double technicalScore,

            @JsonProperty("communication_score")
            double communicationScore,

            List<String> strengths,

            List<String> improvements,

            String feedback
    ) {
    }

    public record SuggestionResult(
            String category,
            String title,
            String description,
            String priority
    ) {
    }

    public record ResumeData(
            String name,
            String email,
            String phone,
            List<EducationInput> education,
            List<ExperienceInput> experience,
            List<ProjectInput> projects,
            List<CertificationInput> certifications,
            List<String> achievements,
            List<String> languages,
            List<String> skills
    ) {
    }

    public record ResumeInput(
            String name,
            String email,
            String phone,
            List<EducationInput> education,
            List<ExperienceInput> experience,
            List<ProjectInput> projects,
            List<CertificationInput> certifications,
            List<String> achievements,
            List<String> languages,
            List<String> skills
    ) {
    }

    public record EducationInput(
            String institution,
            String degree,

            @JsonProperty("field_of_study")
            String fieldOfStudy,

            @JsonProperty("start_date")
            String startDate,

            @JsonProperty("end_date")
            String endDate
    ) {
    }

    public record ExperienceInput(
            String company,
            String role,
            String description
    ) {
    }

    public record ProjectInput(
            String name,
            String description,
            List<String> technologies
    ) {
    }

    public record CertificationInput(
            String name,
            String issuer
    ) {
    }

    public record JobInput(
            String title,
            String company,

            @JsonProperty("required_skills")
            List<String> requiredSkills,

            @JsonProperty("preferred_skills")
            List<String> preferredSkills,

            @JsonProperty("experience_years")
            Double experienceYears,

            @JsonProperty("education_requirements")
            List<String> educationRequirements,

            List<String> keywords
    ) {
    }

    public record AnalysisResult(
            @JsonProperty("overall_score")
            Double overallScore,

            @JsonProperty("skill_score")
            Double skillScore,

            @JsonProperty("experience_score")
            Double experienceScore,

            @JsonProperty("education_score")
            Double educationScore,

            @JsonProperty("keyword_score")
            Double keywordScore,

            @JsonProperty("semantic_score")
            Double semanticScore,

            @JsonProperty("matched_required_skills")
            List<String> matchedRequiredSkills,

            @JsonProperty("missing_required_skills")
            List<String> missingRequiredSkills,

            @JsonProperty("matched_preferred_skills")
            List<String> matchedPreferredSkills,

            @JsonProperty("missing_preferred_skills")
            List<String> missingPreferredSkills,

            @JsonProperty("additional_skills")
            List<String> additionalSkills
    ) {
    }
}