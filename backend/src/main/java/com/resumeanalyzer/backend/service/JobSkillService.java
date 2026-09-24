package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.JobSkillRequest;
import com.resumeanalyzer.backend.dto.JobSkillResponse;
import com.resumeanalyzer.backend.entity.Job;
import com.resumeanalyzer.backend.entity.JobSkill;
import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.exception.BadRequestException;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.JobSkillRepository;
import com.resumeanalyzer.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSkillService {

    private final JobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;
    private final JobService jobService;

    public JobSkillResponse addSkillToJob(
            String email,
            Long jobId,
            JobSkillRequest request) {

        Job job = jobService.getJobEntity(email, jobId);

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill not found"
                        ));

        if (jobSkillRepository.existsByJobIdAndSkillId(
                jobId,
                request.getSkillId())) {

            throw new BadRequestException(
                    "This skill is already added to the job"
            );
        }

        JobSkill jobSkill = new JobSkill();

        jobSkill.setJob(job);
        jobSkill.setSkill(skill);
        jobSkill.setRequired(request.getRequired());
        jobSkill.setImportance(request.getImportance());

        JobSkill savedJobSkill =
                jobSkillRepository.save(jobSkill);

        return toResponse(savedJobSkill);
    }

    @Transactional(readOnly = true)
    public List<JobSkillResponse> getJobSkills(
            String email,
            Long jobId) {

        jobService.getJobEntity(email, jobId);

        return jobSkillRepository
                .findByJobIdOrderByImportanceDesc(jobId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public JobSkillResponse updateJobSkill(
            String email,
            Long jobId,
            Long skillId,
            JobSkillRequest request) {

        jobService.getJobEntity(email, jobId);

        JobSkill jobSkill =
                jobSkillRepository
                        .findByJobIdAndSkillId(jobId, skillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job skill not found"
                                ));

        if (!skillId.equals(request.getSkillId())
                && jobSkillRepository.existsByJobIdAndSkillId(
                        jobId,
                        request.getSkillId())) {

            throw new BadRequestException(
                    "This skill is already added to the job"
            );
        }

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill not found"
                        ));

        jobSkill.setSkill(skill);
        jobSkill.setRequired(request.getRequired());
        jobSkill.setImportance(request.getImportance());

        JobSkill updatedJobSkill =
                jobSkillRepository.save(jobSkill);

        return toResponse(updatedJobSkill);
    }

    public void removeSkillFromJob(
            String email,
            Long jobId,
            Long skillId) {

        jobService.getJobEntity(email, jobId);

        JobSkill jobSkill =
                jobSkillRepository
                        .findByJobIdAndSkillId(jobId, skillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job skill not found"
                                ));

        jobSkillRepository.delete(jobSkill);
    }

    private JobSkillResponse toResponse(JobSkill jobSkill) {

        return new JobSkillResponse(
                jobSkill.getId(),
                jobSkill.getSkill().getId(),
                jobSkill.getSkill().getName(),
                jobSkill.isRequired(),
                jobSkill.getImportance()
        );
    }
}