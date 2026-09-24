package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.ai.AiServiceClient;
import com.resumeanalyzer.backend.dto.InterviewAnswerRequest;
import com.resumeanalyzer.backend.dto.InterviewAnswerResponse;
import com.resumeanalyzer.backend.entity.InterviewAnswer;
import com.resumeanalyzer.backend.entity.InterviewQuestion;
import com.resumeanalyzer.backend.entity.User;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.InterviewAnswerRepository;
import com.resumeanalyzer.backend.repository.InterviewQuestionRepository;
import com.resumeanalyzer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewAnswerService {

    private final InterviewAnswerRepository interviewAnswerRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;
    private final AiServiceClient aiServiceClient;

    public InterviewAnswerResponse submitAnswer(
            String email,
            InterviewAnswerRequest request) {

        User user = getUserByEmail(email);

        InterviewQuestion question =
                interviewQuestionRepository.findById(
                        request.getQuestionId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Interview question not found"
                        ));

        if (!question.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Interview question not found"
            );
        }

        List<String> expectedTopics =
                question.getExpectedTopics() == null
                        || question.getExpectedTopics().isBlank()
                        ? List.of()
                        : List.of(
                                question.getExpectedTopics()
                        );

        AiServiceClient.InterviewEvaluationResponse evaluation =
                aiServiceClient.evaluateInterviewAnswer(
                        question.getQuestion(),
                        request.getAnswer(),
                        question.getCategory().name(),
                        question.getDifficulty().name(),
                        expectedTopics
                );

        InterviewAnswer answer = new InterviewAnswer();

        answer.setQuestion(question);
        answer.setUser(user);
        answer.setAnswer(request.getAnswer());
        answer.setScore(evaluation.overallScore());
        answer.setFeedback(buildFeedback(evaluation));

        InterviewAnswer savedAnswer =
                interviewAnswerRepository.save(answer);

        return toResponse(savedAnswer);
    }

    @Transactional(readOnly = true)
    public List<InterviewAnswerResponse> getMyAnswers(
            String email) {

        User user = getUserByEmail(email);

        return interviewAnswerRepository
                .findByUserIdOrderByAnsweredAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewAnswerResponse> getAnswersForQuestion(
            String email,
            Long questionId) {

        User user = getUserByEmail(email);

        InterviewQuestion question =
                interviewQuestionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview question not found"
                                ));

        if (!question.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Interview question not found"
            );
        }

        return interviewAnswerRepository
                .findByUserIdAndQuestionId(
                        user.getId(),
                        questionId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String buildFeedback(
            AiServiceClient.InterviewEvaluationResponse evaluation) {

        StringBuilder feedback = new StringBuilder();

        if (evaluation.feedback() != null
                && !evaluation.feedback().isBlank()) {

            feedback.append(evaluation.feedback());
        }

        if (evaluation.strengths() != null
                && !evaluation.strengths().isEmpty()) {

            if (!feedback.isEmpty()) {
                feedback.append("\n\n");
            }

            feedback.append("Strengths:\n");

            for (String strength : evaluation.strengths()) {
                feedback.append("• ")
                        .append(strength)
                        .append("\n");
            }
        }

        if (evaluation.improvements() != null
                && !evaluation.improvements().isEmpty()) {

            if (!feedback.isEmpty()) {
                feedback.append("\n");
            }

            feedback.append("Improvements:\n");

            for (String improvement : evaluation.improvements()) {
                feedback.append("• ")
                        .append(improvement)
                        .append("\n");
            }
        }

        return feedback.toString().trim();
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private InterviewAnswerResponse toResponse(
            InterviewAnswer answer) {

        return new InterviewAnswerResponse(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getAnswer(),
                answer.getScore(),
                answer.getFeedback(),
                answer.getAnsweredAt()
        );
    }
}