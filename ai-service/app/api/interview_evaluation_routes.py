from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field

from app.services.interview_evaluation_service import (
    evaluate_interview_answer,
)


router = APIRouter(
    prefix="/api/interview",
    tags=["Interview Evaluation"],
)


class InterviewEvaluationRequest(BaseModel):
    question: str = Field(..., min_length=1)
    answer: str = Field(..., min_length=1)
    category: str = Field(default="TECHNICAL")
    difficulty: str = Field(default="MEDIUM")
    expected_topics: list[str] = Field(default_factory=list)


class InterviewEvaluationResponse(BaseModel):
    overall_score: float
    relevance_score: float
    completeness_score: float
    technical_score: float
    communication_score: float
    strengths: list[str]
    improvements: list[str]
    feedback: str


@router.post(
    "/evaluate",
    response_model=InterviewEvaluationResponse,
)
def evaluate_answer(
    request: InterviewEvaluationRequest,
):
    try:
        result = evaluate_interview_answer(
            question=request.question,
            answer=request.answer,
            category=request.category,
            difficulty=request.difficulty,
            expected_topics=request.expected_topics,
        )

        return InterviewEvaluationResponse(**result)

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"Interview answer evaluation failed: {exception}",
        )