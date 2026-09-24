package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.SkillResponse;
import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.exception.BadRequestException;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillResponse createSkill(
            String name,
            String category) {

        String normalizedName = name.trim();

        if (skillRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new BadRequestException(
                    "A skill with this name already exists"
            );
        }

        Skill skill = new Skill();

        skill.setName(normalizedName);
        skill.setCategory(
                category == null || category.isBlank()
                        ? "OTHER"
                        : category.trim()
        );

        Skill savedSkill = skillRepository.save(skill);

        return toResponse(savedSkill);
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {

        return skillRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> getSkillsByCategory(
            String category) {

        return skillRepository
                .findByCategoryIgnoreCase(category.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkill(Long skillId) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill not found"
                        ));

        return toResponse(skill);
    }

    @Transactional(readOnly = true)
    public Skill getSkillEntity(Long skillId) {

        return skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill not found"
                        ));
    }

    private SkillResponse toResponse(Skill skill) {

        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getCategory()
        );
    }
}