from typing import Any


QUESTION_TEMPLATES = {
    "TECHNICAL": [
        {
            "question": "Explain your experience with {skill}.",
            "difficulty": "EASY",
            "topics": ["fundamentals", "practical usage"],
        },
        {
            "question": "How have you used {skill} in a project?",
            "difficulty": "MEDIUM",
            "topics": ["implementation", "problem solving"],
        },
        {
            "question": "What challenges can occur when working with {skill}, and how would you solve them?",
            "difficulty": "HARD",
            "topics": ["troubleshooting", "best practices"],
        },
    ],
    "PROJECT": [
        {
            "question": "Explain the project you worked on and your specific contribution.",
            "difficulty": "EASY",
            "topics": ["project overview", "individual contribution"],
        },
        {
            "question": "What was the most difficult technical problem you faced in your project?",
            "difficulty": "MEDIUM",
            "topics": ["problem solving", "technical decisions"],
        },
        {
            "question": "If you redesigned this project today, what would you improve and why?",
            "difficulty": "HARD",
            "topics": ["architecture", "improvements", "decision making"],
        },
    ],
    "HR": [
        {
            "question": "Tell me about yourself.",
            "difficulty": "EASY",
            "topics": ["introduction", "communication"],
        },
        {
            "question": "Why are you interested in this role?",
            "difficulty": "MEDIUM",
            "topics": ["career goals", "role understanding"],
        },
        {
            "question": "Tell me about a situation where you had to learn something quickly.",
            "difficulty": "MEDIUM",
            "topics": ["adaptability", "learning"],
        },
    ],
    "SCENARIO": [
        {
            "question": "You are given a production issue that you have never encountered before. How would you approach solving it?",
            "difficulty": "MEDIUM",
            "topics": ["debugging", "communication", "problem solving"],
        },
        {
            "question": "A teammate disagrees with your technical approach. How would you handle the situation?",
            "difficulty": "MEDIUM",
            "topics": ["teamwork", "communication", "technical reasoning"],
        },
        {
            "question": "You have a deadline tomorrow but discover a major technical issue. What would you do?",
            "difficulty": "HARD",
            "topics": ["prioritization", "risk management", "communication"],
        },
    ],
}


def generate_interview_questions(
    skills: list[str],
    projects: list[str],
    categories: list[str] | None = None,
    questions_per_category: int = 3,
) -> list[dict[str, Any]]:
    if categories is None:
        categories = [
            "TECHNICAL",
            "PROJECT",
            "HR",
            "SCENARIO",
        ]

    questions = []

    normalized_categories = [
        category.upper().strip()
        for category in categories
        if category.strip()
    ]

    for category in normalized_categories:
        if category not in QUESTION_TEMPLATES:
            continue

        templates = QUESTION_TEMPLATES[category]

        if category == "TECHNICAL":
            questions.extend(
                generate_technical_questions(
                    skills,
                    templates,
                    questions_per_category,
                )
            )

        elif category == "PROJECT":
            questions.extend(
                generate_project_questions(
                    projects,
                    templates,
                    questions_per_category,
                )
            )

        else:
            for template in templates[:questions_per_category]:
                questions.append({
                    "question": template["question"],
                    "category": category,
                    "difficulty": template["difficulty"],
                    "expected_topics": template["topics"],
                })

    return questions


def generate_technical_questions(
    skills: list[str],
    templates: list[dict[str, Any]],
    limit: int,
) -> list[dict[str, Any]]:
    result = []

    normalized_skills = []
    for skill in skills:
        cleaned = skill.strip()
        if cleaned:
            normalized_skills.append(cleaned)

    if not normalized_skills:
        normalized_skills = ["your primary technical skill"]

    for index, template in enumerate(templates[:limit]):
        skill = normalized_skills[index % len(normalized_skills)]

        result.append({
            "question": template["question"].format(skill=skill),
            "category": "TECHNICAL",
            "difficulty": template["difficulty"],
            "expected_topics": template["topics"],
        })

    return result


def generate_project_questions(
    projects: list[str],
    templates: list[dict[str, Any]],
    limit: int,
) -> list[dict[str, Any]]:
    result = []

    if not projects:
        projects = ["your main project"]

    for index, template in enumerate(templates[:limit]):
        project = projects[index % len(projects)]

        question = template["question"]

        if index == 0:
            question = (
                f"Explain your project '{project}' and your specific contribution."
            )

        result.append({
            "question": question,
            "category": "PROJECT",
            "difficulty": template["difficulty"],
            "expected_topics": template["topics"],
        })

    return result