package com.resumeanalyzer.backend.service;
import com.resumeanalyzer.backend.ai.AiServiceClient;
import com.resumeanalyzer.backend.dto.InterviewGenerationRequest;
import com.resumeanalyzer.backend.dto.InterviewGenerationResponse;
import com.resumeanalyzer.backend.dto.InterviewQuestionRequest;
import com.resumeanalyzer.backend.entity.InterviewQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewGenerationService {

    private final AiServiceClient aiServiceClient;
    private final InterviewQuestionService interviewQuestionService;

    public InterviewGenerationResponse generateQuestions(
            String email,
            InterviewGenerationRequest request) {

        AiServiceClient.InterviewGenerationResponse aiResponse =
                aiServiceClient.generateInterviewQuestions(
                        request.getResumeText(),
                        request.getJobTitle(),
                        request.getJobDescription(),
                        request.getMatchedSkills(),
                        request.getMissingSkills(),
                        request.getQuestionCount()
                );

        List<InterviewGenerationResponse.GeneratedInterviewQuestion> questions =
                new ArrayList<>();

        for (AiServiceClient.InterviewGenerationResponse.GeneratedInterviewQuestion question
                : aiResponse.questions()) {

            InterviewQuestionRequest saveRequest =
                    new InterviewQuestionRequest();

            saveRequest.setAnalysisId(request.getAnalysisId());
            saveRequest.setQuestion(question.questionText());
            saveRequest.setCategory(
                    mapCategory(question.questionType())
            );
            saveRequest.setDifficulty(
                    mapDifficulty(question.difficulty())
            );
            saveRequest.setExpectedTopics(
                    question.skillName()
            );

            interviewQuestionService.createQuestion(
                    email,
                    saveRequest
            );

            questions.add(
                    new InterviewGenerationResponse.GeneratedInterviewQuestion(
                            question.questionText(),
                            question.questionType(),
                            question.difficulty(),
                            question.skillName()
                    )
            );
        }

        return new InterviewGenerationResponse(questions);
    }

    private InterviewQuestion.QuestionCategory mapCategory(
            String questionType) {

        if (questionType == null) {
            return InterviewQuestion.QuestionCategory.TECHNICAL;
        }

        return switch (questionType.toUpperCase()) {
            case "TECHNICAL" ->
                    InterviewQuestion.QuestionCategory.TECHNICAL;

            case "PROJECT" ->
                    InterviewQuestion.QuestionCategory.PROJECT;

            case "SCENARIO" ->
                    InterviewQuestion.QuestionCategory.SCENARIO;

            case "BEHAVIORAL", "HR" ->
                    InterviewQuestion.QuestionCategory.HR;

            default ->
                    InterviewQuestion.QuestionCategory.TECHNICAL;
        };
    }

    private InterviewQuestion.Difficulty mapDifficulty(
            String difficulty) {

        if (difficulty == null) {
            return InterviewQuestion.Difficulty.MEDIUM;
        }

        return switch (difficulty.toUpperCase()) {
            case "EASY" ->
                    InterviewQuestion.Difficulty.EASY;

            case "HARD" ->
                    InterviewQuestion.Difficulty.HARD;

            default ->
                    InterviewQuestion.Difficulty.MEDIUM;
        };
    }
}