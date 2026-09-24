package com.resumeanalyzer.backend.repository;

import com.resumeanalyzer.backend.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewAnswerRepository
        extends JpaRepository<InterviewAnswer, Long> {

    List<InterviewAnswer> findByUserIdOrderByAnsweredAtDesc(
            Long userId
    );

    List<InterviewAnswer> findByUserIdAndQuestionId(
            Long userId,
            Long questionId
    );
}