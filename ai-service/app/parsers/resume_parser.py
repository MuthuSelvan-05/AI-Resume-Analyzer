from pathlib import Path

from app.parsers.pdf_parser import extract_text_from_pdf
from app.parsers.docx_parser import extract_text_from_docx


SUPPORTED_EXTENSIONS = {".pdf", ".docx"}


def extract_resume_text(file_path: str) -> str:
    """
    Extract resume text based on the file extension.
    """

    path = Path(file_path)

    if not path.exists():
        raise FileNotFoundError(
            f"Resume file not found: {file_path}"
        )

    extension = path.suffix.lower()

    if extension not in SUPPORTED_EXTENSIONS:
        raise ValueError(
            "Only PDF and DOCX resume files are supported"
        )

    if extension == ".pdf":
        text = extract_text_from_pdf(file_path)

    else:
        text = extract_text_from_docx(file_path)

    if not text.strip():
        raise ValueError(
            "No readable text was found in the resume"
        )

    return text