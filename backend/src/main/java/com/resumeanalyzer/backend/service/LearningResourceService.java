package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.LearningResourceResponse;
import com.resumeanalyzer.backend.entity.LearningResource;
import com.resumeanalyzer.backend.repository.LearningResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LearningResourceService {

    private final LearningResourceRepository learningResourceRepository;

    public List<LearningResourceResponse> getResourcesBySkill(
            Long skillId) {

        return learningResourceRepository
                .findBySkillIdOrderByIdAsc(skillId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LearningResourceResponse> getResourcesByRoadmapStep(
            Long roadmapStepId) {

        return learningResourceRepository
                .findByRoadmapStepIdOrderByIdAsc(roadmapStepId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LearningResourceResponse toResponse(
            LearningResource resource) {

        return new LearningResourceResponse(
                resource.getId(),
                resource.getTitle(),
                resource.getDescription(),
                resource.getUrl(),
                resource.getResourceType(),
                resource.getLevel()
        );
    }
}