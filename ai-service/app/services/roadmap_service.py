from typing import Any


SKILL_RESOURCES = {
    "java": {
        "level": "Beginner",
        "topics": [
            "Java syntax",
            "Object-oriented programming",
            "Collections",
            "Exception handling",
            "Streams and lambdas",
        ],
        "project": "Build a console-based task management application.",
    },
    "spring boot": {
        "level": "Intermediate",
        "topics": [
            "Spring Boot fundamentals",
            "REST APIs",
            "Dependency injection",
            "Spring Data JPA",
            "Spring Security",
        ],
        "project": "Build a REST API with Spring Boot and MySQL.",
    },
    "mysql": {
        "level": "Beginner",
        "topics": [
            "SQL basics",
            "Joins",
            "Indexes",
            "Constraints",
            "Transactions",
        ],
        "project": "Design a relational database and build CRUD operations.",
    },
    "docker": {
        "level": "Intermediate",
        "topics": [
            "Containers",
            "Images",
            "Dockerfiles",
            "Docker Compose",
            "Container networking",
        ],
        "project": "Containerize a Spring Boot application with Docker.",
    },
    "aws": {
        "level": "Intermediate",
        "topics": [
            "AWS fundamentals",
            "EC2",
            "S3",
            "IAM",
            "Cloud deployment",
        ],
        "project": "Deploy a small web application using AWS services.",
    },
    "react": {
        "level": "Intermediate",
        "topics": [
            "Components",
            "Props and state",
            "Hooks",
            "Routing",
            "API integration",
        ],
        "project": "Build a dashboard application using React.",
    },
    "python": {
        "level": "Beginner",
        "topics": [
            "Python syntax",
            "Functions",
            "Collections",
            "Object-oriented programming",
            "Modules and packages",
        ],
        "project": "Build a Python REST API.",
    },
    "machine learning": {
        "level": "Intermediate",
        "topics": [
            "Data preprocessing",
            "Feature engineering",
            "Supervised learning",
            "Model evaluation",
            "Model deployment",
        ],
        "project": "Build a machine-learning prediction application.",
    },
    "natural language processing": {
        "level": "Intermediate",
        "topics": [
            "Text preprocessing",
            "Tokenization",
            "Embeddings",
            "Text classification",
            "Semantic similarity",
        ],
        "project": "Build a document similarity application.",
    },
}


def generate_learning_roadmap(
    missing_required_skills: list[str],
    missing_preferred_skills: list[str],
) -> dict[str, Any]:

    required = normalize_skills(
        missing_required_skills
    )

    preferred = normalize_skills(
        missing_preferred_skills
    )

    required_roadmap = build_skill_items(
        required,
        priority="HIGH",
    )

    preferred_roadmap = build_skill_items(
        preferred,
        priority="MEDIUM",
    )

    return {
        "required_skill_gaps": required_roadmap,
        "preferred_skill_gaps": preferred_roadmap,
        "total_skill_gaps": (
            len(required_roadmap)
            + len(preferred_roadmap)
        ),
    }


def normalize_skills(
    skills: list[str],
) -> list[str]:

    normalized = set()

    for skill in skills:
        value = skill.strip().lower()

        if value:
            normalized.add(value)

    return sorted(normalized)


def build_skill_items(
    skills: list[str],
    priority: str,
) -> list[dict[str, Any]]:

    result = []

    for skill in skills:

        resource = SKILL_RESOURCES.get(
            skill,
            create_default_resource(skill),
        )

        result.append(
            {
                "skill": skill,
                "priority": priority,
                "recommended_level": resource["level"],
                "topics": resource["topics"],
                "practice_project": resource["project"],
            }
        )

    return result


def create_default_resource(
    skill: str,
) -> dict[str, Any]:

    return {
        "level": "Beginner",
        "topics": [
            f"{skill} fundamentals",
            f"{skill} core concepts",
            f"{skill} practical usage",
            f"{skill} best practices",
        ],
        "project": (
            f"Build a small practical project using {skill} "
            "and document what you learned."
        ),
    }