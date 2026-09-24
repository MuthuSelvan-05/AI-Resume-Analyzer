from pathlib import Path
from tempfile import NamedTemporaryFile

from fastapi import APIRouter, HTTPException, UploadFile, File
from pydantic import BaseModel

from app.models.resume_data import ResumeData
from app.services.resume_extraction_service import (
    extract_resume_information,
)
from app.parsers.resume_parser import extract_resume_text


router = APIRouter(
    prefix="/api/resume",
    tags=["Resume Analysis"],
)


class ResumeTextRequest(BaseModel):
    text: str


@router.post(
    "/extract",
    response_model=ResumeData,
)
def extract_resume(request: ResumeTextRequest):

    if not request.text.strip():
        raise HTTPException(
            status_code=400,
            detail="Resume text is required",
        )

    try:
        return extract_resume_information(
            request.text
        )

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"Resume extraction failed: {exception}",
        )


@router.post(
    "/extract-file",
    response_model=ResumeData,
)
async def extract_resume_file(
    file: UploadFile = File(...)
):
    if not file.filename:
        raise HTTPException(
            status_code=400,
            detail="Resume file is required",
        )

    extension = Path(file.filename).suffix.lower()

    if extension not in {".pdf", ".docx"}:
        raise HTTPException(
            status_code=400,
            detail="Only PDF and DOCX resume files are supported",
        )

    temporary_path = None

    try:
        file_content = await file.read()

        if not file_content:
            raise HTTPException(
                status_code=400,
                detail="The uploaded resume file is empty",
            )

        with NamedTemporaryFile(
            delete=False,
            suffix=extension,
        ) as temporary_file:
            temporary_file.write(file_content)
            temporary_path = temporary_file.name

        resume_text = extract_resume_text(
            temporary_path
        )

        if not resume_text.strip():
            raise HTTPException(
                status_code=400,
                detail="No readable text was found in the resume",
            )

        return extract_resume_information(
            resume_text
        )

    except HTTPException:
        raise

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"Resume file extraction failed: {exception}",
        )

    finally:
        if temporary_path:
            temporary_file_path = Path(temporary_path)

            if temporary_file_path.exists():
                temporary_file_path.unlink()