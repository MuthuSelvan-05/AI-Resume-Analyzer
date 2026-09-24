from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field

from app.services.suggestion_service import generate_suggestions


router = APIRouter(
    prefix="/api/suggestions",
    tags=["AI Suggestions"],
)


class SuggestionRequest(BaseModel):
    resume_text: str = Field(..., min_length=1)

    matched_required_skills: list[str] = Field(
        default_factory=list
    )

    missing_required_skills: list[str] = Field(
        default_factory=list
    )

    matched_preferred_skills: list[str] = Field(
        default_factory=list
    )

    missing_preferred_skills: list[str] = Field(
        default_factory=list
    )

    additional_skills: list[str] = Field(
        default_factory=list
    )

    experience_score: float = 0.0
    education_score: float = 0.0
    keyword_score: float = 0.0


class SuggestionResponse(BaseModel):
    category: str
    title: str
    description: str
    priority: str


@router.post(
    "/generate",
    response_model=list[SuggestionResponse],
)
def generate_ai_suggestions(
    request: SuggestionRequest,
):

    try:
        return generate_suggestions(
            resume_text=request.resume_text,
            matched_required_skills=request.matched_required_skills,
            missing_required_skills=request.missing_required_skills,
            matched_preferred_skills=request.matched_preferred_skills,
            missing_preferred_skills=request.missing_preferred_skills,
            additional_skills=request.additional_skills,
            experience_score=request.experience_score,
            education_score=request.education_score,
            keyword_score=request.keyword_score,
        )

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"Suggestion generation failed: {exception}",
        )