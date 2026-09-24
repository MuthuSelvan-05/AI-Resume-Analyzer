from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field

from app.models.resume_data import ResumeData
from app.models.job_data import JobData
from app.services.semantic_matching_service import (
    calculate_similarity_percentage,
)
from app.services.matching_engine_service import (
    calculate_match_analysis,
)


router = APIRouter(
    prefix="/api/analysis",
    tags=["AI Analysis"],
)


class AnalysisRequest(BaseModel):
    resume_text: str = Field(..., min_length=1)
    resume: ResumeData
    job_text: str = Field(..., min_length=1)
    job: JobData


class AnalysisResponse(BaseModel):
    overall_score: float
    skill_score: float
    experience_score: float
    education_score: float
    keyword_score: float
    semantic_score: float

    matched_required_skills: list[str]
    missing_required_skills: list[str]

    matched_preferred_skills: list[str]
    missing_preferred_skills: list[str]

    additional_skills: list[str]


@router.post(
    "/match",
    response_model=AnalysisResponse,
)
def analyze_resume_against_job(
    request: AnalysisRequest,
):

    try:
        semantic_score = calculate_similarity_percentage(
            request.resume_text,
            request.job_text,
        )

        result = calculate_match_analysis(
            resume_text=request.resume_text,
            resume_skills=request.resume.skills,
            resume_experience_years=extract_resume_experience_years(
                request.resume
            ),
            resume_education=extract_resume_education(
                request.resume
            ),
            required_skills=request.job.required_skills,
            preferred_skills=request.job.preferred_skills,
            required_experience_years=request.job.experience_years,
            required_education=request.job.education_requirements,
            job_keywords=request.job.keywords,
            semantic_score=semantic_score,
        )

        return AnalysisResponse(**result)

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"AI analysis failed: {exception}",
        )


def extract_resume_experience_years(
    resume: ResumeData,
) -> float | None:

    if not resume.experience:
        return None

    return estimate_experience_years(
        resume.experience
    )


def estimate_experience_years(
    experience_items,
) -> float | None:

    total_years = 0.0
    found_years = False

    for experience in experience_items:

        text = experience.description

        patterns = [
            r"(\d+(?:\.\d+)?)\+?\s*(?:years?|yrs?)",
            r"(\d+(?:\.\d+)?)\s*year",
        ]

        for pattern in patterns:
            import re

            match = re.search(
                pattern,
                text,
                re.IGNORECASE,
            )

            if match:
                total_years += float(
                    match.group(1)
                )
                found_years = True
                break

    if found_years:
        return total_years

    return None


def extract_resume_education(
    resume: ResumeData,
) -> list[str]:

    education_items = []

    for education in resume.education:

        parts = [
            education.institution,
            education.degree,
            education.field_of_study,
        ]

        text = " ".join(
            part
            for part in parts
            if part
        ).strip()

        if text:
            education_items.append(text)

    return education_items