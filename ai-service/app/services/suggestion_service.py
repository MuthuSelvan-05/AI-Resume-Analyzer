from typing import Any


def generate_suggestions(
    resume_text: str,
    matched_required_skills: list[str],
    missing_required_skills: list[str],
    matched_preferred_skills: list[str],
    missing_preferred_skills: list[str],
    additional_skills: list[str],
    experience_score: float,
    education_score: float,
    keyword_score: float,
) -> list[dict[str, Any]]:
    suggestions = []

    for skill in missing_required_skills:
        suggestions.append({
            "category": "SKILL",
            "title": f"Address the missing skill: {skill}",
            "description": (
                f"The job description lists {skill} as a required skill, "
                "but it was not identified in the resume. "
                f"If you genuinely have experience with {skill}, "
                "add it to the appropriate skills or project section "
                "and provide supporting evidence."
            ),
            "priority": "HIGH",
        })

    for skill in missing_preferred_skills:
        suggestions.append({
            "category": "SKILL",
            "title": f"Consider developing {skill}",
            "description": (
                f"{skill} is listed as a preferred skill for the job. "
                "If it is relevant to your career goals, consider "
                "learning it and adding a genuine project or practical "
                "experience before including it on your resume."
            ),
            "priority": "MEDIUM",
        })

    if experience_score < 100:
        suggestions.append({
            "category": "EXPERIENCE",
            "title": "Strengthen experience evidence",
            "description": (
                "The resume does not clearly demonstrate the full "
                "experience requirement identified in the job description. "
                "If you have relevant experience, make the duration, "
                "responsibilities, technologies, and measurable outcomes "
                "more explicit."
            ),
            "priority": "HIGH",
        })

    if education_score < 100:
        suggestions.append({
            "category": "EDUCATION",
            "title": "Clarify educational qualifications",
            "description": (
                "Review the education section and clearly mention your "
                "degree, field of study, institution, and relevant "
                "qualification details when they are applicable."
            ),
            "priority": "MEDIUM",
        })

    if keyword_score < 70:
        suggestions.append({
            "category": "KEYWORD",
            "title": "Improve relevant keyword coverage",
            "description": (
                "Several relevant terms from the job description were "
                "not clearly identified in the resume. Where the terms "
                "accurately describe your existing experience, use the "
                "same terminology naturally in your skills, project, "
                "or experience sections."
            ),
            "priority": "MEDIUM",
        })

    if len(resume_text.strip()) < 500:
        suggestions.append({
            "category": "RESUME",
            "title": "Add more supporting resume content",
            "description": (
                "The extracted resume text is relatively short. "
                "Consider adding relevant projects, responsibilities, "
                "technical skills, achievements, or measurable results "
                "that genuinely represent your experience."
            ),
            "priority": "MEDIUM",
        })

    if missing_required_skills:
        suggestions.append({
            "category": "PROJECT",
            "title": "Use projects to demonstrate missing capabilities",
            "description": (
                "If you are currently learning any of the missing "
                "required skills, consider building a genuine project "
                "that demonstrates practical usage. Add it to the resume "
                "only after completing meaningful work with the technology."
            ),
            "priority": "MEDIUM",
        })

    if matched_required_skills:
        suggestions.append({
            "category": "RESUME",
            "title": "Highlight your matched technical skills",
            "description": (
                "The resume already contains several skills that match "
                "the job requirements. Make these skills visible in "
                "relevant project and experience descriptions instead "
                "of listing them only in a skills section."
            ),
            "priority": "LOW",
        })

    priority_order = {
        "HIGH": 1,
        "MEDIUM": 2,
        "LOW": 3,
    }

    suggestions.sort(
        key=lambda item: priority_order.get(item["priority"], 3)
    )

    return suggestions[:15]