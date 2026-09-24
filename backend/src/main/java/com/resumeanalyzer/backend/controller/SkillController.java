package com.resumeanalyzer.backend.controller;

import com.resumeanalyzer.backend.dto.SkillResponse;
import com.resumeanalyzer.backend.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(
            @RequestParam String name,
            @RequestParam(required = false) String category) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        skillService.createSkill(
                                name,
                                category
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {

        return ResponseEntity.ok(
                skillService.getAllSkills()
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SkillResponse>> getSkillsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                skillService.getSkillsByCategory(category)
        );
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillResponse> getSkill(
            @PathVariable Long skillId) {

        return ResponseEntity.ok(
                skillService.getSkill(skillId)
        );
    }
}