package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.JobRequest;
import com.resumeanalyzer.backend.dto.JobResponse;
import com.resumeanalyzer.backend.entity.Job;
import com.resumeanalyzer.backend.entity.User;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.JobRepository;
import com.resumeanalyzer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobResponse createJob(
            String email,
            JobRequest request) {

        User user = getUserByEmail(email);

        Job job = new Job();

        job.setUser(user);
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());

        Job savedJob = jobRepository.save(job);

        return toResponse(savedJob);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getMyJobs(String email) {

        User user = getUserByEmail(email);

        return jobRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Job getJobEntity(
            String email,
            Long jobId) {

        User user = getUserByEmail(email);

        return jobRepository.findById(jobId)
                .filter(job ->
                        job.getUser().getId().equals(user.getId()))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found"
                        ));
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(
            String email,
            Long jobId) {

        return toResponse(
                getJobEntity(email, jobId)
        );
    }

    public JobResponse updateJob(
            String email,
            Long jobId,
            JobRequest request) {

        Job job = getJobEntity(email, jobId);

        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());

        Job updatedJob = jobRepository.save(job);

        return toResponse(updatedJob);
    }

    public void deleteJob(
            String email,
            Long jobId) {

        Job job = getJobEntity(email, jobId);

        jobRepository.delete(job);
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private JobResponse toResponse(Job job) {

        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getDescription(),
                job.getLocation(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}