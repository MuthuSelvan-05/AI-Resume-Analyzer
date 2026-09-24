package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.InterviewQuestionRequest;
import com.resumeanalyzer.backend.dto.InterviewQuestionResponse;
import com.resumeanalyzer.backend.entity.Analysis;
import com.resumeanalyzer.backend.entity.InterviewQuestion;
import com.resumeanalyzer.backend.entity.User;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.AnalysisRepository;
import com.resumeanalyzer.backend.repository.InterviewQuestionRepository;
import com.resumeanalyzer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewQuestionService {

    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;

    public InterviewQuestionResponse createQuestion(
            String email,
            InterviewQuestionRequest request) {

        User user = getUserByEmail(email);

        InterviewQuestion question = new InterviewQuestion();

        question.setUser(user);
        question.setQuestion(request.getQuestion());
        question.setCategory(request.getCategory());
        question.setDifficulty(request.getDifficulty());
        question.setExpectedTopics(request.getExpectedTopics());

        if (request.getAnalysisId() != null) {

            Analysis analysis =
                    analysisRepository.findById(
                            request.getAnalysisId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Analysis not found"
                            ));

            if (!analysis.getUser()
                    .getId()
                    .equals(user.getId())) {

                throw new ResourceNotFoundException(
                        "Analysis not found"
                );
            }

            question.setAnalysis(analysis);
        }

        InterviewQuestion savedQuestion =
                interviewQuestionRepository.save(question);

        return toResponse(savedQuestion);
    }

    @Transactional(readOnly = true)
    public List<InterviewQuestionResponse> getMyQuestions(
            String email) {

        User user = getUserByEmail(email);

        return interviewQuestionRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InterviewQuestionResponse getQuestion(
            String email,
            Long questionId) {

        InterviewQuestion question =
                getQuestionEntity(email, questionId);

        return toResponse(question);
    }

    @Transactional(readOnly = true)
    public List<InterviewQuestionResponse> getQuestionsByAnalysis(
            String email,
            Long analysisId) {

        User user = getUserByEmail(email);

        Analysis analysis =
                analysisRepository.findById(analysisId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Analysis not found"
                                ));

        if (!analysis.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Analysis not found"
            );
        }

        return interviewQuestionRepository
                .findByUserIdAndAnalysisId(
                        user.getId(),
                        analysisId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteQuestion(
            String email,
            Long questionId) {

        InterviewQuestion question =
                getQuestionEntity(email, questionId);

        interviewQuestionRepository.delete(question);
    }

    private InterviewQuestion getQuestionEntity(
            String email,
            Long questionId) {

        User user = getUserByEmail(email);

        return interviewQuestionRepository
                .findById(questionId)
                .filter(question ->
                        question.getUser()
                                .getId()
                                .equals(user.getId()))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Interview question not found"
                        ));
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private InterviewQuestionResponse toResponse(
            InterviewQuestion question) {

        return new InterviewQuestionResponse(
                question.getId(),
                question.getAnalysis() != null
                        ? question.getAnalysis().getId()
                        : null,
                question.getQuestion(),
                question.getCategory(),
                question.getDifficulty(),
                question.getExpectedTopics(),
                question.getCreatedAt()
        );
    }
}