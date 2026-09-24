from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from app.models.job_data import JobData
from app.services.job_extraction_service import extract_job_information


router = APIRouter(
    prefix="/api/job",
    tags=["Job Analysis"],
)


class JobTextRequest(BaseModel):
    text: str


@router.post("/extract", response_model=JobData)
def extract_job(request: JobTextRequest):

    if not request.text.strip():
        raise HTTPException(
            status_code=400,
            detail="Job description is required",
        )

    try:
        return extract_job_information(request.text)

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"Job extraction failed: {exception}",
        )