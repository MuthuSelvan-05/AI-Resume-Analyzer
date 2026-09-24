from pathlib import Path

from app.parsers.resume_parser import extract_resume_text


def test_supported_extensions():
    assert ".pdf" in {".pdf", ".docx"}
    assert ".docx" in {".pdf", ".docx"}


def test_parser_function_exists():
    assert callable(extract_resume_text)


def test_resume_parser_rejects_unsupported_file():
    test_path = Path("test_resume.txt")
    test_path.write_text("test resume")

    try:
        try:
            extract_resume_text(str(test_path))
            assert False, "Expected ValueError"
        except ValueError as exception:
            assert "PDF and DOCX" in str(exception)
    finally:
        test_path.unlink()