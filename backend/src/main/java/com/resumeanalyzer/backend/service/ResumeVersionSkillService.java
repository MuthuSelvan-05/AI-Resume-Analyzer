package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.ResumeVersionSkillRequest;
import com.resumeanalyzer.backend.dto.ResumeVersionSkillResponse;
import com.resumeanalyzer.backend.entity.ResumeVersion;
import com.resumeanalyzer.backend.entity.ResumeVersionSkill;
import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.exception.BadRequestException;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.ResumeVersionSkillRepository;
import com.resumeanalyzer.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeVersionSkillService {

    private final ResumeVersionSkillRepository resumeVersionSkillRepository;
    private final SkillRepository skillRepository;
    private final ResumeVersionService resumeVersionService;

    public ResumeVersionSkillResponse addSkill(
            String email,
            Long resumeId,
            Integer versionNumber,
            ResumeVersionSkillRequest request) {

        ResumeVersion resumeVersion =
                resumeVersionService.getResumeVersionEntity(
                        email,
                        resumeId,
                        versionNumber
                );

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill not found"
                        ));

        if (resumeVersionSkillRepository
                .existsByResumeVersionIdAndSkillId(
                        resumeVersion.getId(),
                        request.getSkillId())) {

            throw new BadRequestException(
                    "This skill is already added to the resume version"
            );
        }

        ResumeVersionSkill resumeVersionSkill =
                new ResumeVersionSkill();

        resumeVersionSkill.setResumeVersion(resumeVersion);
        resumeVersionSkill.setSkill(skill);
        resumeVersionSkill.setConfidence(
                request.getConfidence()
        );
        resumeVersionSkill.setSource(
                request.getSource()
        );

        ResumeVersionSkill saved =
                resumeVersionSkillRepository.save(
                        resumeVersionSkill
                );

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ResumeVersionSkillResponse> getSkills(
            String email,
            Long resumeId,
            Integer versionNumber) {

        ResumeVersion resumeVersion =
                resumeVersionService.getResumeVersionEntity(
                        email,
                        resumeId,
                        versionNumber
                );

        return resumeVersionSkillRepository
                .findByResumeVersionIdOrderByConfidenceDesc(
                        resumeVersion.getId()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ResumeVersionSkillResponse updateSkill(
            String email,
            Long resumeId,
            Integer versionNumber,
            Long skillId,
            ResumeVersionSkillRequest request) {

        ResumeVersion resumeVersion =
                resumeVersionService.getResumeVersionEntity(
                        email,
                        resumeId,
                        versionNumber
                );

        ResumeVersionSkill resumeVersionSkill =
                resumeVersionSkillRepository
                        .findByResumeVersionIdAndSkillId(
                                resumeVersion.getId(),
                                skillId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resume version skill not found"
                                ));

        if (!skillId.equals(request.getSkillId())
                && resumeVersionSkillRepository
                        .existsByResumeVersionIdAndSkillId(
                                resumeVersion.getId(),
                                request.getSkillId())) {

            throw new BadRequestException(
                    "This skill is already added to the resume version"
            );
        }

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill not found"
                        ));

        resumeVersionSkill.setSkill(skill);
        resumeVersionSkill.setConfidence(
                request.getConfidence()
        );
        resumeVersionSkill.setSource(
                request.getSource()
        );

        return toResponse(
                resumeVersionSkillRepository.save(
                        resumeVersionSkill
                )
        );
    }

    public void removeSkill(
            String email,
            Long resumeId,
            Integer versionNumber,
            Long skillId) {

        ResumeVersion resumeVersion =
                resumeVersionService.getResumeVersionEntity(
                        email,
                        resumeId,
                        versionNumber
                );

        ResumeVersionSkill resumeVersionSkill =
                resumeVersionSkillRepository
                        .findByResumeVersionIdAndSkillId(
                                resumeVersion.getId(),
                                skillId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resume version skill not found"
                                ));

        resumeVersionSkillRepository.delete(
                resumeVersionSkill
        );
    }

    private ResumeVersionSkillResponse toResponse(
            ResumeVersionSkill resumeVersionSkill) {

        return new ResumeVersionSkillResponse(
                resumeVersionSkill.getId(),
                resumeVersionSkill.getSkill().getId(),
                resumeVersionSkill.getSkill().getName(),
                resumeVersionSkill.getSkill().getCategory(),
                resumeVersionSkill.getConfidence(),
                resumeVersionSkill.getSource()
        );
    }
}