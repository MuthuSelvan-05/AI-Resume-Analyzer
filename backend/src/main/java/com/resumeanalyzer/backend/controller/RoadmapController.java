package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.RoadmapResponse;
import com.resumeanalyzer.backend.dto.RoadmapStepProgressRequest;
import com.resumeanalyzer.backend.dto.RoadmapStepResponse;
import com.resumeanalyzer.backend.entity.Roadmap;
import com.resumeanalyzer.backend.entity.RoadmapStep;
import com.resumeanalyzer.backend.entity.User;
import com.resumeanalyzer.backend.repository.UserRepository;
import com.resumeanalyzer.backend.service.RoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final UserRepository userRepository;

    /**
     * Generate roadmaps from the user's tracked skills.
     */
    @PostMapping("/generate")
    @Transactional
    public ResponseEntity<List<RoadmapResponse>> generateRoadmaps(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);

        List<Roadmap> roadmaps =
                roadmapService.generateRoadmaps(userId);

        return ResponseEntity.ok(
                roadmaps.stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    /**
     * Get all roadmaps belonging to the authenticated user.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<RoadmapResponse>> getRoadmaps(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);

        List<Roadmap> roadmaps =
                roadmapService.getUserRoadmaps(userId);

        return ResponseEntity.ok(
                roadmaps.stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    /**
     * Get one roadmap.
     */
    @GetMapping("/{roadmapId}")
    @Transactional(readOnly = true)
    public ResponseEntity<RoadmapResponse> getRoadmap(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long roadmapId) {

        Long userId = getUserId(userDetails);

        Roadmap roadmap =
                roadmapService.getRoadmap(userId, roadmapId);

        return ResponseEntity.ok(toResponse(roadmap));
    }

    /**
     * Get all steps belonging to a roadmap.
     */
    @GetMapping("/{roadmapId}/steps")
    @Transactional(readOnly = true)
    public ResponseEntity<List<RoadmapStepResponse>> getRoadmapSteps(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long roadmapId) {

        Long userId = getUserId(userDetails);

        List<RoadmapStep> steps =
                roadmapService.getRoadmapSteps(
                        userId,
                        roadmapId
                );

        return ResponseEntity.ok(
                steps.stream()
                        .map(this::toStepResponse)
                        .toList()
        );
    }

    /**
     * Update a roadmap step's progress.
     */
    @PutMapping("/{roadmapId}/steps/{stepId}")
    public ResponseEntity<RoadmapStepResponse> updateStepProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long roadmapId,
            @PathVariable Long stepId,
            @Valid @RequestBody RoadmapStepProgressRequest request) {

        Long userId = getUserId(userDetails);

        RoadmapStep step =
                roadmapService.updateStepProgress(
                        userId,
                        roadmapId,
                        stepId,
                        request.getStatus(),
                        request.getProgressPercentage()
                );

        return ResponseEntity.ok(toStepResponse(step));
    }

    /**
     * Get the database user ID from the authenticated email.
     */
    private Long getUserId(UserDetails userDetails) {

        if (userDetails == null) {
            throw new IllegalArgumentException(
                    "Authenticated user not found"
            );
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        ));

        return user.getId();
    }

    private RoadmapResponse toResponse(Roadmap roadmap) {

        return new RoadmapResponse(
                roadmap.getId(),
                roadmap.getTitle(),
                roadmap.getDescription(),
                roadmap.getSkill().getId(),
                roadmap.getSkill().getName(),
                roadmap.getStatus(),
                roadmap.getProgressPercentage(),
                roadmap.getCreatedAt(),
                roadmap.getUpdatedAt()
        );
    }

    private RoadmapStepResponse toStepResponse(
            RoadmapStep step) {

        return new RoadmapStepResponse(
                step.getId(),
                step.getStepOrder(),
                step.getTitle(),
                step.getDescription(),
                step.getResourceUrl(),
                step.getResourceType(),
                step.getStatus(),
                step.getProgressPercentage()
        );
    }
}