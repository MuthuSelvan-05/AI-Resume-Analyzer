from typing import List, Dict, Any


def normalize_skill(skill: str) -> str:
    return skill.strip().lower()


def calculate_skill_match(
    resume_skills: List[str],
    required_skills: List[str],
    preferred_skills: List[str],
) -> Dict[str, Any]:

    resume_set = {
        normalize_skill(skill)
        for skill in resume_skills
    }

    required_set = {
        normalize_skill(skill)
        for skill in required_skills
    }

    preferred_set = {
        normalize_skill(skill)
        for skill in preferred_skills
    }

    matched_required = sorted(
        resume_set.intersection(required_set)
    )

    missing_required = sorted(
        required_set.difference(resume_set)
    )

    matched_preferred = sorted(
        resume_set.intersection(preferred_set)
    )

    missing_preferred = sorted(
        preferred_set.difference(resume_set)
    )

    additional_skills = sorted(
        resume_set
        .difference(required_set)
        .difference(preferred_set)
    )

    required_score = calculate_percentage(
        len(matched_required),
        len(required_set),
    )

    preferred_score = calculate_percentage(
        len(matched_preferred),
        len(preferred_set),
    )

    if required_set and preferred_set:
        skill_score = (
            required_score * 0.75
            + preferred_score * 0.25
        )
    elif required_set:
        skill_score = required_score
    elif preferred_set:
        skill_score = preferred_score
    else:
        skill_score = 0.0

    return {
        "matched_required": matched_required,
        "missing_required": missing_required,
        "matched_preferred": matched_preferred,
        "missing_preferred": missing_preferred,
        "additional_skills": additional_skills,
        "required_score": round(required_score, 2),
        "preferred_score": round(preferred_score, 2),
        "skill_score": round(skill_score, 2),
    }


def calculate_experience_score(
    resume_experience_years: float | None,
    required_experience_years: float | None,
) -> float:

    if required_experience_years is None:
        return 100.0

    if resume_experience_years is None:
        return 0.0

    if required_experience_years <= 0:
        return 100.0

    score = (
        resume_experience_years
        / required_experience_years
    ) * 100

    return round(
        min(100.0, score),
        2,
    )


def calculate_education_score(
    resume_education: List[str],
    required_education: List[str],
) -> float:

    if not required_education:
        return 100.0

    if not resume_education:
        return 0.0

    resume_text = " ".join(
        resume_education
    ).lower()

    matched = 0

    for requirement in required_education:
        requirement_words = [
            word
            for word in requirement.lower().split()
            if len(word) > 3
        ]

        if not requirement_words:
            continue

        matches = sum(
            1
            for word in requirement_words
            if word in resume_text
        )

        match_ratio = (
            matches / len(requirement_words)
        )

        if match_ratio >= 0.4:
            matched += 1

    return round(
        (matched / len(required_education)) * 100,
        2,
    )


def calculate_keyword_score(
    resume_text: str,
    job_keywords: List[str],
) -> float:

    if not job_keywords:
        return 100.0

    normalized_resume = resume_text.lower()

    matched = 0

    for keyword in job_keywords:
        if keyword.lower() in normalized_resume:
            matched += 1

    return round(
        (matched / len(job_keywords)) * 100,
        2,
    )


def calculate_final_score(
    skill_score: float,
    experience_score: float,
    education_score: float,
    keyword_score: float,
    semantic_score: float,
) -> float:

    final_score = (
        skill_score * 0.35
        + experience_score * 0.15
        + education_score * 0.10
        + keyword_score * 0.15
        + semantic_score * 0.25
    )

    return round(
        max(0.0, min(100.0, final_score)),
        2,
    )


def calculate_match_analysis(
    resume_text: str,
    resume_skills: List[str],
    resume_experience_years: float | None,
    resume_education: List[str],
    required_skills: List[str],
    preferred_skills: List[str],
    required_experience_years: float | None,
    required_education: List[str],
    job_keywords: List[str],
    semantic_score: float,
) -> Dict[str, Any]:

    skill_result = calculate_skill_match(
        resume_skills,
        required_skills,
        preferred_skills,
    )

    experience_score = calculate_experience_score(
        resume_experience_years,
        required_experience_years,
    )

    education_score = calculate_education_score(
        resume_education,
        required_education,
    )

    keyword_score = calculate_keyword_score(
        resume_text,
        job_keywords,
    )

    final_score = calculate_final_score(
        skill_result["skill_score"],
        experience_score,
        education_score,
        keyword_score,
        semantic_score,
    )

    return {
        "overall_score": final_score,
        "skill_score": skill_result["skill_score"],
        "experience_score": experience_score,
        "education_score": education_score,
        "keyword_score": keyword_score,
        "semantic_score": round(
            semantic_score,
            2,
        ),
        "matched_required_skills": skill_result[
            "matched_required"
        ],
        "missing_required_skills": skill_result[
            "missing_required"
        ],
        "matched_preferred_skills": skill_result[
            "matched_preferred"
        ],
        "missing_preferred_skills": skill_result[
            "missing_preferred"
        ],
        "additional_skills": skill_result[
            "additional_skills"
        ],
    }


def calculate_percentage(
    numerator: int,
    denominator: int,
) -> float:

    if denominator == 0:
        return 0.0

    return (
        numerator / denominator
    ) * 100