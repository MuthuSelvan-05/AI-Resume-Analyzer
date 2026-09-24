package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.entity.Roadmap;
import com.resumeanalyzer.backend.entity.RoadmapStep;
import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.entity.SkillProgress;
import com.resumeanalyzer.backend.entity.User;
import com.resumeanalyzer.backend.repository.RoadmapRepository;
import com.resumeanalyzer.backend.repository.RoadmapStepRepository;
import com.resumeanalyzer.backend.repository.SkillProgressRepository;
import com.resumeanalyzer.backend.repository.SkillRepository;
import com.resumeanalyzer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapStepRepository roadmapStepRepository;
    private final SkillProgressRepository skillProgressRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    /**
     * Generates learning roadmaps for the user's tracked skills.
     *
     * Existing roadmaps are reused instead of creating duplicates.
     */
    @Transactional
    public List<Roadmap> generateRoadmaps(Long userId) {

        User user = getUser(userId);

        List<SkillProgress> skillProgressList =
                skillProgressRepository.findByUserId(userId);

        List<Roadmap> roadmaps = new ArrayList<>();

        for (SkillProgress progress : skillProgressList) {

            if (progress.getSkill() == null) {
                continue;
            }

            Skill skill = progress.getSkill();

            Roadmap roadmap =
                    roadmapRepository
                            .findByUserIdAndSkillId(userId, skill.getId())
                            .orElseGet(() -> createRoadmap(user, skill));

            if (roadmapStepRepository
                    .findByRoadmapIdOrderByStepOrderAsc(roadmap.getId())
                    .isEmpty()) {

                createDefaultSteps(roadmap, skill);
            }

            updateRoadmapProgress(roadmap);

            roadmaps.add(roadmap);
        }

        return roadmaps.stream()
                .sorted(Comparator.comparing(
                        Roadmap::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    /**
     * Returns all roadmaps belonging to a user.
     */
    @Transactional(readOnly = true)
    public List<Roadmap> getUserRoadmaps(Long userId) {

        getUser(userId);

        return roadmapRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Returns a single roadmap belonging to the user.
     */
    @Transactional(readOnly = true)
    public Roadmap getRoadmap(Long userId, Long roadmapId) {

        return roadmapRepository
                .findByIdAndUserId(roadmapId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Roadmap not found"
                        ));
    }

    /**
     * Returns the steps of a roadmap.
     */
    @Transactional(readOnly = true)
    public List<RoadmapStep> getRoadmapSteps(
            Long userId,
            Long roadmapId) {

        getRoadmap(userId, roadmapId);

        return roadmapStepRepository
                .findByRoadmapIdOrderByStepOrderAsc(roadmapId);
    }

    /**
     * Updates the progress of a roadmap step.
     */
    @Transactional
    public RoadmapStep updateStepProgress(
            Long userId,
            Long roadmapId,
            Long stepId,
            RoadmapStep.StepStatus status,
            Integer progressPercentage) {

        Roadmap roadmap = getRoadmap(userId, roadmapId);

        RoadmapStep step =
                roadmapStepRepository
                        .findByIdAndRoadmapId(stepId, roadmapId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Roadmap step not found"
                                ));

        if (progressPercentage == null) {
            progressPercentage = getDefaultProgress(status);
        }

        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new IllegalArgumentException(
                    "Progress percentage must be between 0 and 100"
            );
        }

        step.setStatus(status);
        step.setProgressPercentage(progressPercentage);

        RoadmapStep savedStep =
                roadmapStepRepository.save(step);

        updateRoadmapProgress(roadmap);

        return savedStep;
    }

    /**
     * Creates a new roadmap for a skill.
     */
    private Roadmap createRoadmap(
            User user,
            Skill skill) {

        Roadmap roadmap = Roadmap.builder()
                .title(skill.getName() + " Learning Roadmap")
                .description(
                        "A structured learning path to improve "
                                + skill.getName()
                                + " skills."
                )
                .user(user)
                .skill(skill)
                .status(Roadmap.RoadmapStatus.NOT_STARTED)
                .progressPercentage(0)
                .build();

        return roadmapRepository.save(roadmap);
    }

    /**
     * Creates structured default learning stages.
     */
    private void createDefaultSteps(
            Roadmap roadmap,
            Skill skill) {

        String skillName = skill.getName();

        List<RoadmapStep> steps = List.of(

                createStep(
                        roadmap,
                        1,
                        "Learn the fundamentals",
                        "Understand the core concepts and terminology of "
                                + skillName
                                + ".",
                        null,
                        "CONCEPT"
                ),

                createStep(
                        roadmap,
                        2,
                        "Practice the basics",
                        "Solve beginner-level exercises and build small "
                                + skillName
                                + " examples.",
                        null,
                        "PRACTICE"
                ),

                createStep(
                        roadmap,
                        3,
                        "Build a practical project",
                        "Apply "
                                + skillName
                                + " by creating a real-world project.",
                        null,
                        "PROJECT"
                ),

                createStep(
                        roadmap,
                        4,
                        "Learn advanced concepts",
                        "Study intermediate and advanced topics in "
                                + skillName
                                + ".",
                        null,
                        "ADVANCED"
                ),

                createStep(
                        roadmap,
                        5,
                        "Prepare for interviews",
                        "Review common interview questions and "
                                + skillName
                                + " problem-solving scenarios.",
                        null,
                        "INTERVIEW"
                )
        );

        roadmapStepRepository.saveAll(steps);
    }

    private RoadmapStep createStep(
            Roadmap roadmap,
            int order,
            String title,
            String description,
            String resourceUrl,
            String resourceType) {

        return RoadmapStep.builder()
                .roadmap(roadmap)
                .stepOrder(order)
                .title(title)
                .description(description)
                .resourceUrl(resourceUrl)
                .resourceType(resourceType)
                .status(RoadmapStep.StepStatus.NOT_STARTED)
                .progressPercentage(0)
                .build();
    }

    /**
     * Calculates roadmap progress from its steps.
     */
    private void updateRoadmapProgress(Roadmap roadmap) {

        List<RoadmapStep> steps =
                roadmapStepRepository
                        .findByRoadmapIdOrderByStepOrderAsc(
                                roadmap.getId()
                        );

        if (steps.isEmpty()) {
            roadmap.setProgressPercentage(0);
            roadmap.setStatus(
                    Roadmap.RoadmapStatus.NOT_STARTED
            );
            roadmapRepository.save(roadmap);
            return;
        }

        int totalProgress = steps.stream()
                .mapToInt(step ->
                        step.getProgressPercentage() == null
                                ? 0
                                : step.getProgressPercentage())
                .sum();

        int averageProgress =
                totalProgress / steps.size();

        roadmap.setProgressPercentage(averageProgress);

        if (averageProgress >= 100) {

            roadmap.setProgressPercentage(100);
            roadmap.setStatus(
                    Roadmap.RoadmapStatus.COMPLETED
            );

        } else if (averageProgress > 0) {

            roadmap.setStatus(
                    Roadmap.RoadmapStatus.IN_PROGRESS
            );

        } else {

            roadmap.setStatus(
                    Roadmap.RoadmapStatus.NOT_STARTED
            );
        }

        roadmapRepository.save(roadmap);
    }

    private int getDefaultProgress(
            RoadmapStep.StepStatus status) {

        return switch (status) {
            case NOT_STARTED -> 0;
            case IN_PROGRESS -> 50;
            case COMPLETED -> 100;
        };
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        ));
    }
}