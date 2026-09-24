from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field

from app.services.semantic_matching_service import (
    calculate_semantic_similarity,
    calculate_similarity_percentage,
)


router = APIRouter(
    prefix="/api/matching",
    tags=["Semantic Matching"],
)


class SemanticMatchingRequest(BaseModel):
    resume_text: str = Field(..., min_length=1)
    job_description: str = Field(..., min_length=1)


class SemanticMatchingResponse(BaseModel):
    similarity: float
    similarity_percentage: float


@router.post(
    "/semantic",
    response_model=SemanticMatchingResponse,
)
def semantic_matching(
    request: SemanticMatchingRequest,
):

    try:
        similarity = calculate_semantic_similarity(
            request.resume_text,
            request.job_description,
        )

        percentage = calculate_similarity_percentage(
            request.resume_text,
            request.job_description,
        )

        return SemanticMatchingResponse(
            similarity=similarity,
            similarity_percentage=percentage,
        )

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"Semantic matching failed: {exception}",
        )