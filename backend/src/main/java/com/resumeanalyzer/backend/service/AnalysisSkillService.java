package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.AnalysisSkillRequest;
import com.resumeanalyzer.backend.dto.AnalysisSkillResponse;
import com.resumeanalyzer.backend.entity.Analysis;
import com.resumeanalyzer.backend.entity.AnalysisSkill;
import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.exception.BadRequestException;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.AnalysisSkillRepository;
import com.resumeanalyzer.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalysisSkillService {

    private final AnalysisSkillRepository analysisSkillRepository;
    private final SkillRepository skillRepository;
    private final AnalysisService analysisService;

    public AnalysisSkillResponse addSkill(
            String email,
            Long analysisId,
            AnalysisSkillRequest request) {

        Analysis analysis =
                analysisService.getAnalysisEntity(
                        email,
                        analysisId
                );

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill not found"
                        ));

        if (analysisSkillRepository
                .existsByAnalysisIdAndSkillId(
                        analysisId,
                        request.getSkillId())) {

            throw new BadRequestException(
                    "This skill is already added to the analysis"
            );
        }

        AnalysisSkill analysisSkill =
                new AnalysisSkill();

        analysisSkill.setAnalysis(analysis);
        analysisSkill.setSkill(skill);
        analysisSkill.setStatus(request.getStatus());
        analysisSkill.setImportance(request.getImportance());

        AnalysisSkill saved =
                analysisSkillRepository.save(analysisSkill);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AnalysisSkillResponse> getSkills(
            String email,
            Long analysisId) {

        analysisService.getAnalysisEntity(
                email,
                analysisId
        );

        return analysisSkillRepository
                .findByAnalysisIdOrderByImportanceDesc(
                        analysisId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AnalysisSkillResponse updateSkill(
            String email,
            Long analysisId,
            Long skillId,
            AnalysisSkillRequest request) {

        analysisService.getAnalysisEntity(
                email,
                analysisId
        );

        AnalysisSkill analysisSkill =
                analysisSkillRepository
                        .findByAnalysisIdAndSkillId(
                                analysisId,
                                skillId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Analysis skill not found"
                                ));

        if (!skillId.equals(request.getSkillId())
                && analysisSkillRepository
                        .existsByAnalysisIdAndSkillId(
                                analysisId,
                                request.getSkillId())) {

            throw new BadRequestException(
                    "This skill is already added to the analysis"
            );
        }

        Skill skill = skillRepository.findById(
                request.getSkillId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Skill not found"
                ));

        analysisSkill.setSkill(skill);
        analysisSkill.setStatus(request.getStatus());
        analysisSkill.setImportance(request.getImportance());

        return toResponse(
                analysisSkillRepository.save(
                        analysisSkill
                )
        );
    }

    public void removeSkill(
            String email,
            Long analysisId,
            Long skillId) {

        analysisService.getAnalysisEntity(
                email,
                analysisId
        );

        AnalysisSkill analysisSkill =
                analysisSkillRepository
                        .findByAnalysisIdAndSkillId(
                                analysisId,
                                skillId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Analysis skill not found"
                                ));

        analysisSkillRepository.delete(analysisSkill);
    }

    private AnalysisSkillResponse toResponse(
            AnalysisSkill analysisSkill) {

        return new AnalysisSkillResponse(
                analysisSkill.getId(),
                analysisSkill.getSkill().getId(),
                analysisSkill.getSkill().getName(),
                analysisSkill.getStatus(),
                analysisSkill.getImportance()
        );
    }
}