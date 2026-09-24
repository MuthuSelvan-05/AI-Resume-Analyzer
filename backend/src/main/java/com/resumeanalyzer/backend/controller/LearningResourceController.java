package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.LearningResourceResponse;
import com.resumeanalyzer.backend.service.LearningResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-resources")
@RequiredArgsConstructor
public class LearningResourceController {

    private final LearningResourceService learningResourceService;

    @GetMapping("/skill/{skillId}")
    public ResponseEntity<List<LearningResourceResponse>> getResourcesBySkill(
            @PathVariable Long skillId) {

        return ResponseEntity.ok(
                learningResourceService.getResourcesBySkill(skillId)
        );
    }

    @GetMapping("/roadmap-step/{roadmapStepId}")
    public ResponseEntity<List<LearningResourceResponse>> getResourcesByRoadmapStep(
            @PathVariable Long roadmapStepId) {

        return ResponseEntity.ok(
                learningResourceService.getResourcesByRoadmapStep(
                        roadmapStepId
                )
        );
    }
}