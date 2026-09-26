package com.resumeanalyzer.backend.config;

import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class SkillSeeder implements CommandLineRunner {

    private final SkillRepository skillRepository;

    @Override
    public void run(String... args) {

        List<SkillData> skills = List.of(
                new SkillData("Java", "Programming"),
                new SkillData("Spring Boot", "Backend"),
                new SkillData("REST API", "Backend"),
                new SkillData("MySQL", "Database"),
                new SkillData("Git", "Version Control"),
                new SkillData("Docker", "DevOps"),
                new SkillData("React", "Frontend"),
                new SkillData("AWS", "Cloud"),
                new SkillData("Kubernetes", "DevOps")
        );

        for (SkillData skillData : skills) {

            if (skillRepository.findByNameIgnoreCase(skillData.name()).isEmpty()) {

                Skill skill = Skill.builder()
                        .name(skillData.name())
                        .category(skillData.category())
                        .build();

                skillRepository.save(skill);
            }
        }
    }

    private record SkillData(
            String name,
            String category
    ) {
    }
}