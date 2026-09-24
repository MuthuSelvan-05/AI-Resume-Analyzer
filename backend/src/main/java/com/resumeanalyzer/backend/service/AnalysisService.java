package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.ai.AiServiceClient;
import com.resumeanalyzer.backend.dto.AnalysisRequest;
import com.resumeanalyzer.backend.dto.AnalysisResponse;
import com.resumeanalyzer.backend.dto.SuggestionResponse;
import com.resumeanalyzer.backend.entity.Analysis;
import com.resumeanalyzer.backend.entity.AnalysisSkill;
import com.resumeanalyzer.backend.entity.Job;
import com.resumeanalyzer.backend.entity.JobSkill;
import com.resumeanalyzer.backend.entity.ResumeVersion;
import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.entity.Suggestion;
import com.resumeanalyzer.backend.entity.SkillProgress;
import com.resumeanalyzer.backend.entity.User;
import com.resumeanalyzer.backend.exception.BadRequestException;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.AnalysisRepository;
import com.resumeanalyzer.backend.repository.AnalysisSkillRepository;
import com.resumeanalyzer.backend.repository.JobRepository;
import com.resumeanalyzer.backend.repository.JobSkillRepository;
import com.resumeanalyzer.backend.repository.ResumeVersionRepository;
import com.resumeanalyzer.backend.repository.SkillRepository;
import com.resumeanalyzer.backend.repository.SuggestionRepository;
import com.resumeanalyzer.backend.repository.SkillProgressRepository;
import com.resumeanalyzer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisSkillRepository analysisSkillRepository;
    private final UserRepository userRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;
    private final AiServiceClient aiServiceClient;
    private final SuggestionRepository suggestionRepository;
    private final SkillProgressRepository skillProgressRepository;

    public AnalysisResponse createAnalysis(
            String email,
            AnalysisRequest request) {

        User user = getUserByEmail(email);

        ResumeVersion resumeVersion =
                resumeVersionRepository.findById(
                        request.getResumeVersionId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume version not found"
                        ));

        if (!resumeVersion.getResume()
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Resume version not found"
            );
        }

        Job job = jobRepository.findById(
                request.getJobId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Job not found"
                ));

        if (!job.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Job not found"
            );
        }

        List<Analysis> existingAnalyses =
                analysisRepository.findByUserIdAndJobId(
                        user.getId(),
                        job.getId()
                );

        for (Analysis existing : existingAnalyses) {

            if (existing.getResumeVersion()
                    .getId()
                    .equals(resumeVersion.getId())) {

                throw new BadRequestException(
                        "An analysis already exists for this resume version and job"
                );
            }
        }

        Analysis analysis = new Analysis();

        analysis.setUser(user);
        analysis.setResumeVersion(resumeVersion);
        analysis.setJob(job);

        analysis.setOverallScore(0.0);
        analysis.setSkillScore(0.0);
        analysis.setExperienceScore(0.0);
        analysis.setEducationScore(0.0);
        analysis.setKeywordScore(0.0);
        analysis.setSemanticScore(0.0);

        Analysis savedAnalysis =
                analysisRepository.save(analysis);

        return toResponse(savedAnalysis);
    }

    /*
     * Run the complete AI analysis for an existing analysis.
     */
    public AnalysisResponse runAnalysis(
            String email,
            Long analysisId) {

        Analysis analysis =
                getAnalysisEntity(email, analysisId);

        ResumeVersion resumeVersion =
                analysis.getResumeVersion();

        Job job =
                analysis.getJob();

        Path resumePath =
                Path.of(resumeVersion.getFilePath())
                        .toAbsolutePath()
                        .normalize();

        if (!Files.exists(resumePath)) {

            throw new ResourceNotFoundException(
                    "Stored resume file not found"
            );
        }

        try {

            /*
             * Step 1:
             * Extract structured resume information
             * from the uploaded PDF/DOCX.
             */
            AiServiceClient.ResumeData resumeData =
                    aiServiceClient.extractResumeFromFile(
                            resumePath
                    );

            /*
             * Step 2:
             * Load skills configured for this job.
             */
            List<JobSkill> jobSkills =
                    jobSkillRepository.findByJobId(
                            job.getId()
                    );

            List<String> requiredSkills =
                    new ArrayList<>();

            List<String> preferredSkills =
                    new ArrayList<>();

            for (JobSkill jobSkill : jobSkills) {

                String skillName =
                        jobSkill.getSkill().getName();

                if (jobSkill.isRequired()) {
                    requiredSkills.add(skillName);
                } else {
                    preferredSkills.add(skillName);
                }
            }

            /*
             * Step 3:
             * Build the AI resume input.
             */
            AiServiceClient.ResumeInput resumeInput =
                    new AiServiceClient.ResumeInput(
                            resumeData.name(),
                            resumeData.email(),
                            resumeData.phone(),
                            resumeData.education(),
                            resumeData.experience(),
                            resumeData.projects(),
                            resumeData.certifications(),
                            resumeData.achievements(),
                            resumeData.languages(),
                            resumeData.skills()
                    );

            /*
             * Step 4:
             * Build the AI job input.
             *
             * Experience and education requirements are currently
             * not stored as separate fields in the Job entity.
             * The complete job description is still supplied as
             * jobText for semantic/keyword analysis.
             */
            AiServiceClient.JobInput jobInput =
                    new AiServiceClient.JobInput(
                            job.getTitle(),
                            job.getCompany(),
                            requiredSkills,
                            preferredSkills,
                            0.0,
                            List.of(),
                            List.of()
                    );

            /*
             * Step 5:
             * Send everything to the Python matching engine.
             */
            AiServiceClient.AnalysisResult result =
                    aiServiceClient.analyzeResumeAgainstJob(
                            resumeTextFromResumeData(resumeData),
                            resumeInput,
                            job.getDescription(),
                            jobInput
                    );

            if (result == null) {
                throw new BadRequestException(
                        "AI service returned an empty analysis result"
                );
            }

            /*
             * Step 6:
             * Save calculated scores.
             */
            analysis.setOverallScore(
                    safeScore(result.overallScore())
            );

            analysis.setSkillScore(
                    safeScore(result.skillScore())
            );

            analysis.setExperienceScore(
                    safeScore(result.experienceScore())
            );

            analysis.setEducationScore(
                    safeScore(result.educationScore())
            );

            analysis.setKeywordScore(
                    safeScore(result.keywordScore())
            );

            analysis.setSemanticScore(
                    safeScore(result.semanticScore())
            );

            analysisRepository.save(analysis);

            /*
             * Step 7:
             * Replace old analysis-skill results.
             *
             * This makes the endpoint safe to run again.
             */
            List<AnalysisSkill> existingSkills =
                    analysisSkillRepository
                            .findByAnalysisId(analysisId);

            analysisSkillRepository.deleteAll(existingSkills);

            /*
             * Step 8:
             * Save matched/missing/related skills.
             */
            saveAnalysisSkills(
                    analysis,
                    result
            );

            generateSkillProgressForMissingSkills(
                    analysis,
                    result
            );

            generateAndSaveSuggestions(
                    analysis,
                    resumeTextFromResumeData(resumeData),
                    result
            );

            return toResponse(analysis);

        } catch (BadRequestException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new BadRequestException(
                    "AI analysis failed: "
                            + exception.getMessage()
            );
        }
    }

    @Transactional(readOnly = true)
    public List<AnalysisResponse> getMyAnalyses(
            String email) {

        User user = getUserByEmail(email);

        return analysisRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysis(
            String email,
            Long analysisId) {

        return toResponse(
                getAnalysisEntity(
                        email,
                        analysisId
                )
        );
    }

    @Transactional(readOnly = true)
    public Analysis getAnalysisEntity(
            String email,
            Long analysisId) {

        User user = getUserByEmail(email);

        return analysisRepository.findById(analysisId)
                .filter(analysis ->
                        analysis.getUser()
                                .getId()
                                .equals(user.getId()))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Analysis not found"
                        ));
    }

    public void deleteAnalysis(
            String email,
            Long analysisId) {

        Analysis analysis =
                getAnalysisEntity(
                        email,
                        analysisId
                );

        analysisRepository.delete(analysis);
    }

    private void saveAnalysisSkills(
            Analysis analysis,
            AiServiceClient.AnalysisResult result) {

        saveSkillList(
                analysis,
                result.matchedRequiredSkills(),
                "MATCHED"
        );

        saveSkillList(
                analysis,
                result.matchedPreferredSkills(),
                "MATCHED"
        );

        saveSkillList(
                analysis,
                result.missingRequiredSkills(),
                "MISSING"
        );

        saveSkillList(
                analysis,
                result.missingPreferredSkills(),
                "MISSING"
        );

        saveSkillList(
                analysis,
                result.additionalSkills(),
                "RELATED"
        );
    }

    private void saveSkillList(
            Analysis analysis,
            List<String> skillNames,
            String status) {

        if (skillNames == null) {
            return;
        }

        for (String skillName : skillNames) {

            if (skillName == null ||
                    skillName.isBlank()) {
                continue;
            }

            Skill skill =
                    skillRepository
                            .findByNameIgnoreCase(
                                    skillName.trim()
                            )
                            .orElse(null);

            /*
             * Do not create fake skills automatically.
             * Only skills already known by the application
             * are persisted in analysis_skills.
             */
            if (skill == null) {
                continue;
            }

            boolean alreadyExists =
                    analysisSkillRepository
                            .existsByAnalysisIdAndSkillId(
                                    analysis.getId(),
                                    skill.getId()
                            );

            if (alreadyExists) {
                continue;
            }

            AnalysisSkill analysisSkill =
                    new AnalysisSkill();

            analysisSkill.setAnalysis(analysis);
            analysisSkill.setSkill(skill);

            analysisSkill.setStatus(
                    AnalysisSkill.SkillMatchStatus.valueOf(
                            status
                    )
            );

            analysisSkill.setImportance(
                    getImportanceForSkill(
                            analysis.getJob(),
                            skill
                    )
            );

            analysisSkillRepository.save(
                    analysisSkill
            );
        }
    }

    private Integer getImportanceForSkill(
            Job job,
            Skill skill) {

        return jobSkillRepository
                .findByJobIdAndSkillId(
                        job.getId(),
                        skill.getId()
                )
                .map(JobSkill::getImportance)
                .orElse(1);
    }

    private void generateSkillProgressForMissingSkills(
            Analysis analysis,
            AiServiceClient.AnalysisResult result) {

        User user = analysis.getUser();

        List<String> missingSkillNames = new ArrayList<>();

        if (result.missingRequiredSkills() != null) {
            missingSkillNames.addAll(result.missingRequiredSkills());
        }

        if (result.missingPreferredSkills() != null) {
            missingSkillNames.addAll(result.missingPreferredSkills());
        }

        for (String skillName : missingSkillNames) {

            if (skillName == null || skillName.isBlank()) {
                continue;
            }

            Skill skill = skillRepository
                    .findByNameIgnoreCase(skillName.trim())
                    .orElse(null);

            /*
             * Only create progress for skills already known
             * by the application.
             */
            if (skill == null) {
                continue;
            }

            boolean alreadyTracked =
                    skillProgressRepository
                            .existsByUserIdAndSkillId(
                                    user.getId(),
                                    skill.getId()
                            );

            /*
             * Never overwrite existing progress.
             * This preserves the user's current learning status.
             */
            if (alreadyTracked) {
                continue;
            }

            SkillProgress skillProgress =
                    SkillProgress.builder()
                            .user(user)
                            .skill(skill)
                            .status(
                                    SkillProgress.ProgressStatus.NOT_STARTED
                            )
                            .progressPercentage(0)
                            .targetDate(null)
                            .build();

            skillProgressRepository.save(skillProgress);
        }
    }

    private void generateAndSaveSuggestions(
            Analysis analysis,
            String resumeText,
            AiServiceClient.AnalysisResult result) {

        List<Suggestion> existingSuggestions =
                suggestionRepository.findByAnalysisId(
                        analysis.getId()
                );

        suggestionRepository.deleteAll(existingSuggestions);

        List<AiServiceClient.SuggestionResult> aiSuggestions =
                aiServiceClient.generateSuggestions(
                        resumeText,
                        result
                );

        if (aiSuggestions == null) {
            return;
        }

        for (AiServiceClient.SuggestionResult aiSuggestion :
                aiSuggestions) {

            if (aiSuggestion == null ||
                    aiSuggestion.category() == null ||
                    aiSuggestion.title() == null ||
                    aiSuggestion.description() == null ||
                    aiSuggestion.priority() == null) {
                continue;
            }

            try {
                Suggestion suggestion =
                        Suggestion.builder()
                                .analysis(analysis)
                                .category(
                                        Suggestion.SuggestionCategory.valueOf(
                                                aiSuggestion.category()
                                                        .toUpperCase()
                                        )
                                )
                                .title(aiSuggestion.title())
                                .description(aiSuggestion.description())
                                .priority(
                                        Suggestion.SuggestionPriority.valueOf(
                                                aiSuggestion.priority()
                                                        .toUpperCase()
                                        )
                                )
                                .build();

                suggestionRepository.save(suggestion);

            } catch (IllegalArgumentException exception) {
                // Ignore invalid AI category or priority values.
            }
        }
    }

    private String resumeTextFromResumeData(
            AiServiceClient.ResumeData resumeData) {

        StringBuilder text =
                new StringBuilder();

        append(text, resumeData.name());
        append(text, resumeData.email());
        append(text, resumeData.phone());

        if (resumeData.skills() != null) {
            resumeData.skills()
                    .forEach(skill ->
                            append(text, skill));
        }

        if (resumeData.achievements() != null) {
            resumeData.achievements()
                    .forEach(achievement ->
                            append(text, achievement));
        }

        if (resumeData.languages() != null) {
            resumeData.languages()
                    .forEach(language ->
                            append(text, language));
        }

        if (resumeData.education() != null) {
            resumeData.education()
                    .forEach(education -> {

                        append(
                                text,
                                education.institution()
                        );

                        append(
                                text,
                                education.degree()
                        );

                        append(
                                text,
                                education.fieldOfStudy()
                        );

                        append(
                                text,
                                education.startDate()
                        );

                        append(
                                text,
                                education.endDate()
                        );
                    });
        }

        if (resumeData.experience() != null) {
            resumeData.experience()
                    .forEach(experience -> {

                        append(
                                text,
                                experience.company()
                        );

                        append(
                                text,
                                experience.role()
                        );

                        append(
                                text,
                                experience.description()
                        );
                    });
        }

        if (resumeData.projects() != null) {
            resumeData.projects()
                    .forEach(project -> {

                        append(
                                text,
                                project.name()
                        );

                        append(
                                text,
                                project.description()
                        );

                        if (project.technologies() != null) {
                            project.technologies()
                                    .forEach(technology ->
                                            append(
                                                    text,
                                                    technology
                                            ));
                        }
                    });
        }

        if (resumeData.certifications() != null) {
            resumeData.certifications()
                    .forEach(certification -> {

                        append(
                                text,
                                certification.name()
                        );

                        append(
                                text,
                                certification.issuer()
                        );
                    });
        }

        return text.toString().trim();
    }

    private void append(
            StringBuilder text,
            String value) {

        if (value != null &&
                !value.isBlank()) {

            text.append(value)
                    .append("\n");
        }
    }

    private double safeScore(Double score) {

        if (score == null) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(
                        100.0,
                        score
                )
        );
    }

    private User getUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private AnalysisResponse toResponse(
            Analysis analysis) {

        List<AnalysisSkill> analysisSkills =
                analysisSkillRepository
                        .findByAnalysisId(analysis.getId());

        List<String> matchedSkills =
                analysisSkills.stream()
                        .filter(skill ->
                                skill.getStatus() ==
                                        AnalysisSkill.SkillMatchStatus.MATCHED)
                        .map(skill -> skill.getSkill().getName())
                        .distinct()
                        .toList();

        List<String> missingSkills =
                analysisSkills.stream()
                        .filter(skill ->
                                skill.getStatus() ==
                                        AnalysisSkill.SkillMatchStatus.MISSING)
                        .map(skill -> skill.getSkill().getName())
                        .distinct()
                        .toList();

        List<String> relatedSkills =
                analysisSkills.stream()
                        .filter(skill ->
                                skill.getStatus() ==
                                        AnalysisSkill.SkillMatchStatus.RELATED)
                        .map(skill -> skill.getSkill().getName())
                        .distinct()
                        .toList();

        List<SuggestionResponse> suggestions =
                suggestionRepository
                        .findByAnalysisIdOrderByPriorityAsc(analysis.getId())
                        .stream()
                        .map(suggestion ->
                                new SuggestionResponse(
                                        suggestion.getId(),
                                        suggestion.getCategory(),
                                        suggestion.getTitle(),
                                        suggestion.getDescription(),
                                        suggestion.getPriority()
                                )
                        )
                        .toList();

        return new AnalysisResponse(
                analysis.getId(),
                analysis.getResumeVersion().getId(),
                analysis.getJob().getId(),
                analysis.getOverallScore(),
                analysis.getSkillScore(),
                analysis.getExperienceScore(),
                analysis.getEducationScore(),
                analysis.getKeywordScore(),
                analysis.getSemanticScore(),
                matchedSkills,
                missingSkills,
                relatedSkills,
                suggestions,
                analysis.getCreatedAt()
        );
    }
}
