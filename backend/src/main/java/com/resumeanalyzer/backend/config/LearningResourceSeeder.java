package com.resumeanalyzer.backend.config;

import com.resumeanalyzer.backend.entity.LearningResource;
import com.resumeanalyzer.backend.entity.RoadmapStep;
import com.resumeanalyzer.backend.entity.Skill;
import com.resumeanalyzer.backend.repository.LearningResourceRepository;
import com.resumeanalyzer.backend.repository.RoadmapStepRepository;
import com.resumeanalyzer.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LearningResourceSeeder implements CommandLineRunner {

    private final SkillRepository skillRepository;
    private final RoadmapStepRepository roadmapStepRepository;
    private final LearningResourceRepository resourceRepository;

    private static final Map<String, List<ResourceTemplate>> RESOURCES =
            Map.ofEntries(

                    Map.entry(
                            "Spring Boot",
                            List.of(
                                    new ResourceTemplate(
                                            "Spring Boot Official Documentation",
                                            "Official Spring Boot documentation for understanding the framework and its core concepts.",
                                            "https://docs.spring.io/spring-boot/documentation.html",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "Spring Guides",
                                            "Official hands-on guides for building Spring Boot applications.",
                                            "https://spring.io/guides",
                                            LearningResource.ResourceType.TUTORIAL,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "Spring Boot Projects",
                                            "Build practical applications using Spring Boot and REST APIs.",
                                            "https://spring.io/guides/gs/rest-service/",
                                            LearningResource.ResourceType.PROJECT,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    ),
                                    new ResourceTemplate(
                                            "Spring Boot Reference",
                                            "Detailed Spring Boot reference material for advanced development.",
                                            "https://docs.spring.io/spring-boot/reference/",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.ADVANCED
                                    ),
                                    new ResourceTemplate(
                                            "Spring Boot Interview Questions",
                                            "Practice common Spring Boot interview topics and questions.",
                                            "https://www.baeldung.com/spring-boot-interview-questions",
                                            LearningResource.ResourceType.INTERVIEW,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    )
                            )
                    ),

                    Map.entry(
                            "REST API",
                            List.of(
                                    new ResourceTemplate(
                                            "MDN HTTP Documentation",
                                            "Learn HTTP methods, status codes, headers and web API fundamentals.",
                                            "https://developer.mozilla.org/en-US/docs/Web/HTTP",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "MDN Web API Guide",
                                            "Learn how HTTP-based APIs work and how clients communicate with servers.",
                                            "https://developer.mozilla.org/en-US/docs/Learn_web_development/Extensions/Server-side",
                                            LearningResource.ResourceType.TUTORIAL,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "REST API Practice",
                                            "Practice designing and consuming REST APIs using realistic examples.",
                                            "https://restfulapi.net/",
                                            LearningResource.ResourceType.PRACTICE,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    ),
                                    new ResourceTemplate(
                                            "HTTP Semantics",
                                            "Study advanced HTTP methods, status codes, caching and request handling.",
                                            "https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.ADVANCED
                                    ),
                                    new ResourceTemplate(
                                            "REST API Interview Questions",
                                            "Prepare for common REST API and HTTP interview questions.",
                                            "https://www.geeksforgeeks.org/rest-api-interview-questions/",
                                            LearningResource.ResourceType.INTERVIEW,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    )
                            )
                    ),

                    Map.entry(
                            "MySQL",
                            List.of(
                                    new ResourceTemplate(
                                            "MySQL Reference Manual",
                                            "Official MySQL documentation and reference material.",
                                            "https://dev.mysql.com/doc/",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "MySQL Tutorial",
                                            "Learn MySQL SQL syntax, tables, queries and database fundamentals.",
                                            "https://www.mysqltutorial.org/",
                                            LearningResource.ResourceType.TUTORIAL,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "SQL Practice",
                                            "Practice SQL queries and database problems.",
                                            "https://sqlzoo.net/wiki/SQL_Tutorial",
                                            LearningResource.ResourceType.PRACTICE,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    ),
                                    new ResourceTemplate(
                                            "MySQL Optimization",
                                            "Learn indexing, query optimization and advanced MySQL concepts.",
                                            "https://dev.mysql.com/doc/refman/8.4/en/optimization.html",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.ADVANCED
                                    ),
                                    new ResourceTemplate(
                                            "MySQL Interview Questions",
                                            "Practice common SQL and MySQL interview questions.",
                                            "https://www.geeksforgeeks.org/mysql-interview-questions/",
                                            LearningResource.ResourceType.INTERVIEW,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    )
                            )
                    ),

                    Map.entry(
                            "Git",
                            List.of(
                                    new ResourceTemplate(
                                            "Git Documentation",
                                            "Official Git documentation for learning Git fundamentals and commands.",
                                            "https://git-scm.com/doc",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "Git Book",
                                            "A complete guide to learning Git and version control.",
                                            "https://git-scm.com/book/en/v2",
                                            LearningResource.ResourceType.TUTORIAL,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "Learn Git Branching",
                                            "Interactive exercises for practicing Git branching and commands.",
                                            "https://learngitbranching.js.org/",
                                            LearningResource.ResourceType.PRACTICE,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    ),
                                    new ResourceTemplate(
                                            "Git Advanced Documentation",
                                            "Explore advanced Git workflows, branching and repository management.",
                                            "https://git-scm.com/docs",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.ADVANCED
                                    ),
                                    new ResourceTemplate(
                                            "Git Interview Questions",
                                            "Practice common Git and version-control interview questions.",
                                            "https://www.geeksforgeeks.org/git-interview-questions/",
                                            LearningResource.ResourceType.INTERVIEW,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    )
                            )
                    ),

                    Map.entry(
                            "Docker",
                            List.of(
                                    new ResourceTemplate(
                                            "Docker Get Started",
                                            "Official Docker learning path for containers and Docker fundamentals.",
                                            "https://docs.docker.com/get-started/",
                                            LearningResource.ResourceType.TUTORIAL,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "Docker Documentation",
                                            "Official Docker documentation covering containers and images.",
                                            "https://docs.docker.com/",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "Docker Labs",
                                            "Hands-on Docker exercises for practicing container workflows.",
                                            "https://training.play-with-docker.com/",
                                            LearningResource.ResourceType.PRACTICE,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    ),
                                    new ResourceTemplate(
                                            "Docker Compose Documentation",
                                            "Learn advanced multi-container application development with Docker Compose.",
                                            "https://docs.docker.com/compose/",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.ADVANCED
                                    ),
                                    new ResourceTemplate(
                                            "Docker Interview Questions",
                                            "Practice common Docker and containerization interview questions.",
                                            "https://www.geeksforgeeks.org/docker-interview-questions/",
                                            LearningResource.ResourceType.INTERVIEW,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    )
                            )
                    ),

                    Map.entry(
                            "React",
                            List.of(
                                    new ResourceTemplate(
                                            "React Learn",
                                            "Official React learning documentation.",
                                            "https://react.dev/learn",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "React Tutorial",
                                            "Official tutorial for building a React application.",
                                            "https://react.dev/learn/tutorial-tic-tac-toe",
                                            LearningResource.ResourceType.TUTORIAL,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "React Practice",
                                            "Practice React concepts by building interactive user interfaces.",
                                            "https://react.dev/learn/thinking-in-react",
                                            LearningResource.ResourceType.PRACTICE,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    ),
                                    new ResourceTemplate(
                                            "React Advanced Learn",
                                            "Explore advanced React concepts and application architecture.",
                                            "https://react.dev/reference/react",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.ADVANCED
                                    ),
                                    new ResourceTemplate(
                                            "React Interview Questions",
                                            "Practice common React interview questions and concepts.",
                                            "https://www.geeksforgeeks.org/reactjs-interview-questions/",
                                            LearningResource.ResourceType.INTERVIEW,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    )
                            )
                    ),

                    Map.entry(
                            "AWS",
                            List.of(
                                    new ResourceTemplate(
                                            "AWS Skill Builder",
                                            "AWS learning resources and training material.",
                                            "https://skillbuilder.aws/",
                                            LearningResource.ResourceType.COURSE,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "AWS Getting Started",
                                            "Official AWS getting-started resources for cloud fundamentals.",
                                            "https://aws.amazon.com/getting-started/",
                                            LearningResource.ResourceType.TUTORIAL,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "AWS Hands-on Tutorials",
                                            "Hands-on tutorials for practicing AWS cloud services.",
                                            "https://aws.amazon.com/getting-started/hands-on/",
                                            LearningResource.ResourceType.PRACTICE,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    ),
                                    new ResourceTemplate(
                                            "AWS Architecture Center",
                                            "Study advanced AWS architectures and cloud design patterns.",
                                            "https://aws.amazon.com/architecture/",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.ADVANCED
                                    ),
                                    new ResourceTemplate(
                                            "AWS Interview Questions",
                                            "Practice common AWS cloud and infrastructure interview questions.",
                                            "https://www.geeksforgeeks.org/aws-interview-questions/",
                                            LearningResource.ResourceType.INTERVIEW,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    )
                            )
                    ),

                    Map.entry(
                            "Kubernetes",
                            List.of(
                                    new ResourceTemplate(
                                            "Kubernetes Basics",
                                            "Official Kubernetes learning material and interactive tutorials.",
                                            "https://kubernetes.io/docs/tutorials/kubernetes-basics/",
                                            LearningResource.ResourceType.TUTORIAL,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "Kubernetes Documentation",
                                            "Official Kubernetes documentation for containers and orchestration.",
                                            "https://kubernetes.io/docs/home/",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.BEGINNER
                                    ),
                                    new ResourceTemplate(
                                            "Kubernetes Interactive Tutorials",
                                            "Hands-on interactive tutorials for practicing Kubernetes.",
                                            "https://kubernetes.io/docs/tutorials/",
                                            LearningResource.ResourceType.PRACTICE,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    ),
                                    new ResourceTemplate(
                                            "Kubernetes Concepts",
                                            "Study advanced Kubernetes architecture, networking and workloads.",
                                            "https://kubernetes.io/docs/concepts/",
                                            LearningResource.ResourceType.DOCUMENTATION,
                                            LearningResource.ResourceLevel.ADVANCED
                                    ),
                                    new ResourceTemplate(
                                            "Kubernetes Interview Questions",
                                            "Practice common Kubernetes and container orchestration interview questions.",
                                            "https://www.geeksforgeeks.org/kubernetes-interview-questions/",
                                            LearningResource.ResourceType.INTERVIEW,
                                            LearningResource.ResourceLevel.INTERMEDIATE
                                    )
                            )
                    )
            );

    @Override
    @Transactional
    public void run(String... args) {

        for (Map.Entry<String, List<ResourceTemplate>> entry
                : RESOURCES.entrySet()) {

            seedResourcesForSkill(
                    entry.getKey(),
                    entry.getValue()
            );
        }
    }

    private void seedResourcesForSkill(
            String skillName,
            List<ResourceTemplate> templates) {

        Skill skill = skillRepository
                .findByNameIgnoreCase(skillName)
                .orElse(null);

        if (skill == null) {
            return;
        }

        List<RoadmapStep> steps =
                roadmapStepRepository
                        .findStepsWithRoadmapAndSkillBySkillId(
                                skill.getId()
                        );

        if (steps.isEmpty()) {
            return;
        }

        for (int i = 0; i < templates.size(); i++) {

            ResourceTemplate template = templates.get(i);

            boolean alreadyExists =
                    resourceRepository
                            .findBySkillId(skill.getId())
                            .stream()
                            .anyMatch(resource ->
                                    resource.getUrl()
                                            .equals(template.url()));

            if (alreadyExists) {
                continue;
            }

            RoadmapStep roadmapStep =
                    steps.get(Math.min(i, steps.size() - 1));

            LearningResource resource =
                    LearningResource.builder()
                            .title(template.title())
                            .description(template.description())
                            .url(template.url())
                            .resourceType(template.resourceType())
                            .level(template.level())
                            .skill(skill)
                            .roadmapStep(roadmapStep)
                            .build();

            resourceRepository.save(resource);
        }
    }

    private record ResourceTemplate(
            String title,
            String description,
            String url,
            LearningResource.ResourceType resourceType,
            LearningResource.ResourceLevel level
    ) {
    }
}