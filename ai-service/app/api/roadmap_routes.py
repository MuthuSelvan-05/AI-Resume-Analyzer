from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field

from app.services.roadmap_service import (
    generate_learning_roadmap,
)


router = APIRouter(
    prefix="/api/roadmap",
    tags=["Learning Roadmap"],
)


class RoadmapRequest(BaseModel):
    missing_required_skills: list[str] = Field(
        default_factory=list
    )

    missing_preferred_skills: list[str] = Field(
        default_factory=list
    )


class SkillRoadmapItem(BaseModel):
    skill: str
    priority: str
    recommended_level: str
    topics: list[str]
    practice_project: str


class RoadmapResponse(BaseModel):
    required_skill_gaps: list[SkillRoadmapItem]
    preferred_skill_gaps: list[SkillRoadmapItem]
    total_skill_gaps: int


@router.post(
    "/generate",
    response_model=RoadmapResponse,
)
def generate_roadmap(
    request: RoadmapRequest,
):

    try:
        result = generate_learning_roadmap(
            missing_required_skills=(
                request.missing_required_skills
            ),
            missing_preferred_skills=(
                request.missing_preferred_skills
            ),
        )

        return RoadmapResponse(**result)

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"Roadmap generation failed: {exception}",
        )