from pathlib import Path
from tempfile import NamedTemporaryFile

from fastapi import APIRouter, File, HTTPException, UploadFile

from app.parsers.resume_parser import extract_resume_text
from app.services.resume_extraction_service import (
    extract_resume_information,
)


router = APIRouter(
    prefix="/api/resume",
    tags=["Resume File Analysis"],
)


ALLOWED_EXTENSIONS = {
    ".pdf",
    ".docx",
}


@router.post("/analyze-file")
async def analyze_resume_file(
    file: UploadFile = File(...)
):
    """
    Upload a PDF or DOCX resume and return
    extracted text and structured resume information.
    """

    if not file.filename:
        raise HTTPException(
            status_code=400,
            detail="Resume file is required",
        )

    extension = Path(file.filename).suffix.lower()

    if extension not in ALLOWED_EXTENSIONS:
        raise HTTPException(
            status_code=400,
            detail="Only PDF and DOCX files are supported",
        )

    temporary_path = None

    try:
        file_content = await file.read()

        if not file_content:
            raise HTTPException(
                status_code=400,
                detail="Uploaded file is empty",
            )

        with NamedTemporaryFile(
            suffix=extension,
            delete=False,
        ) as temporary_file:

            temporary_file.write(file_content)
            temporary_path = temporary_file.name

        extracted_text = extract_resume_text(
            temporary_path
        )

        resume_data = extract_resume_information(
            extracted_text
        )

        return {
            "fileName": file.filename,
            "fileType": extension.replace(".", "").upper(),
            "textLength": len(extracted_text),
            "extractedText": extracted_text,
            "resume": resume_data,
        }

    except ValueError as exception:
        raise HTTPException(
            status_code=400,
            detail=str(exception),
        )

    except FileNotFoundError as exception:
        raise HTTPException(
            status_code=404,
            detail=str(exception),
        )

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail=f"Resume analysis failed: {exception}",
        )

    finally:
        if temporary_path:
            temporary_file_path = Path(temporary_path)

            if temporary_file_path.exists():
                temporary_file_path.unlink()