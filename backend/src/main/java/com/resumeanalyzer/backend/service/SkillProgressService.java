package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.SkillProgressRequest;
import com.resumeanalyzer.backend.dto.SkillProgressResponse;
import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.entity.SkillProgress;
import com.resumeanalyzer.backend.entity.User;
import com.resumeanalyzer.backend.repository.SkillProgressRepository;
import com.resumeanalyzer.backend.repository.SkillRepository;
import com.resumeanalyzer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillProgressService {

    private final SkillProgressRepository skillProgressRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Transactional
    public SkillProgressResponse createProgress(
            String email,
            SkillProgressRequest request) {

        User user = getUser(email);

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill not found"
                ));

        if (skillProgressRepository.existsByUserIdAndSkillId(
                user.getId(),
                skill.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Skill progress already exists for this skill"
            );
        }

        SkillProgress progress = SkillProgress.builder()
                .user(user)
                .skill(skill)
                .status(request.getStatus())
                .progressPercentage(
                        request.getProgressPercentage() == null
                                ? 0
                                : request.getProgressPercentage()
                )
                .targetDate(request.getTargetDate())
                .build();

        return toResponse(skillProgressRepository.save(progress));
    }

    @Transactional(readOnly = true)
    public List<SkillProgressResponse> getMyProgress(String email) {

        User user = getUser(email);

        return skillProgressRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SkillProgressResponse getProgress(
            String email,
            Long progressId) {

        User user = getUser(email);

        SkillProgress progress =
                skillProgressRepository.findByIdAndUserId(
                        progressId,
                        user.getId()
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill progress not found"
                ));

        return toResponse(progress);
    }

    @Transactional
    public SkillProgressResponse updateProgress(
            String email,
            Long progressId,
            SkillProgressRequest request) {

        User user = getUser(email);

        SkillProgress progress =
                skillProgressRepository.findByIdAndUserId(
                        progressId,
                        user.getId()
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill progress not found"
                ));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill not found"
                ));

        skillProgressRepository
                .findByUserIdAndSkillId(user.getId(), skill.getId())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(progressId)) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Skill progress already exists for this skill"
                        );
                    }
                });

        progress.setSkill(skill);
        progress.setStatus(request.getStatus());
        progress.setProgressPercentage(
                request.getProgressPercentage() == null
                        ? 0
                        : request.getProgressPercentage()
        );
        progress.setTargetDate(request.getTargetDate());

        return toResponse(skillProgressRepository.save(progress));
    }

    @Transactional
    public void deleteProgress(
            String email,
            Long progressId) {

        User user = getUser(email);

        SkillProgress progress =
                skillProgressRepository.findByIdAndUserId(
                        progressId,
                        user.getId()
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill progress not found"
                ));

        skillProgressRepository.delete(progress);
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    private SkillProgressResponse toResponse(
            SkillProgress progress) {

        Skill skill = progress.getSkill();

        return new SkillProgressResponse(
                progress.getId(),
                skill.getId(),
                skill.getName(),
                skill.getCategory(),
                progress.getStatus(),
                progress.getProgressPercentage(),
                progress.getTargetDate(),
                progress.getUpdatedAt()
        );
    }
}