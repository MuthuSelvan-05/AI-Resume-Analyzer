import re

from app.models.resume_data import (
    CertificationItem,
    EducationItem,
    ExperienceItem,
    ProjectItem,
    ResumeData,
)
from app.services.skill_extraction_service import (
    extract_skills as extract_normalized_skills,
)


SECTION_ALIASES = {
    "education": {
        "education",
        "academic background",
        "academic qualifications",
        "qualifications",
    },
    "experience": {
        "experience",
        "work experience",
        "professional experience",
        "employment",
    },
    "projects": {
        "projects",
        "academic projects",
        "personal projects",
    },
    "certifications": {
        "certifications",
        "certificates",
        "courses",
    },
    "achievements": {
        "achievements",
        "awards",
        "accomplishments",
    },
    "languages": {
        "languages",
        "language",
    },
    "skills": {
        "skills",
        "technical skills",
        "technical expertise",
        "core skills",
    },
}


def extract_resume_information(text: str) -> ResumeData:
    """
    Extract structured information from resume text.
    """

    normalized_text = normalize_text(text)

    return ResumeData(
        name=extract_name(normalized_text),
        email=extract_email(normalized_text),
        phone=extract_phone(normalized_text),
        education=extract_education(normalized_text),
        experience=extract_experience(normalized_text),
        projects=extract_projects(normalized_text),
        certifications=extract_certifications(normalized_text),
        achievements=extract_achievements(normalized_text),
        languages=extract_languages(normalized_text),
        skills=extract_skills(normalized_text),
    )


def normalize_text(text: str) -> str:

    text = text.replace("\r\n", "\n")
    text = text.replace("\r", "\n")

    lines = []

    for line in text.split("\n"):
        cleaned = re.sub(
            r"[ \t]+",
            " ",
            line
        ).strip()

        if cleaned:
            lines.append(cleaned)

    return "\n".join(lines)


def extract_name(text: str) -> str | None:

    lines = text.split("\n")

    for line in lines[:10]:

        if "@" in line:
            continue

        if re.search(r"\d", line):
            continue

        words = line.split()

        if 2 <= len(words) <= 5:
            return line

    return None


def extract_email(text: str) -> str | None:

    match = re.search(
        r"\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b",
        text,
    )

    if match:
        return match.group(0)

    return None


def extract_phone(text: str) -> str | None:

    match = re.search(
        r"(?<!\d)(?:\+?\d[\d\s().-]{8,}\d)(?!\d)",
        text,
    )

    if match:
        return match.group(0).strip()

    return None


def normalize_section_heading(text: str) -> str:

    return re.sub(
        r"[^a-z ]",
        "",
        text.lower(),
    ).strip()


def extract_section(
    text: str,
    section_name: str,
) -> str:

    lines = text.split("\n")

    aliases = SECTION_ALIASES.get(
        section_name,
        {section_name},
    )

    normalized_aliases = {
        normalize_section_heading(alias)
        for alias in aliases
    }

    start_index = None

    for index, line in enumerate(lines):

        normalized_line = normalize_section_heading(line)

        if normalized_line in normalized_aliases:
            start_index = index + 1
            break

    if start_index is None:
        return ""

    all_aliases = set()

    for values in SECTION_ALIASES.values():
        all_aliases.update(
            normalize_section_heading(value)
            for value in values
        )

    section_lines = []

    for line in lines[start_index:]:

        normalized_line = normalize_section_heading(line)

        if normalized_line in all_aliases:
            break

        section_lines.append(line)

    return "\n".join(section_lines).strip()


def extract_education(
    text: str,
) -> list[EducationItem]:

    section = extract_section(
        text,
        "education",
    )

    if not section:
        return []

    result = []

    for line in section.split("\n"):

        cleaned = line.strip()

        if not cleaned:
            continue

        result.append(
            EducationItem(
                institution=cleaned,
                degree="",
                field_of_study="",
            )
        )

    return result[:10]


def extract_experience(
    text: str,
) -> list[ExperienceItem]:

    section = extract_section(
        text,
        "experience",
    )

    if not section:
        return []

    result = []

    for line in section.split("\n"):

        cleaned = line.strip()

        if not cleaned:
            continue

        result.append(
            ExperienceItem(
                company="",
                role=cleaned,
                description=cleaned,
            )
        )

    return result[:10]


def extract_projects(
    text: str,
) -> list[ProjectItem]:

    section = extract_section(
        text,
        "projects",
    )

    if not section:
        return []

    result = []

    for line in section.split("\n"):

        cleaned = line.strip()

        if not cleaned:
            continue

        result.append(
            ProjectItem(
                name=cleaned,
                description=cleaned,
                technologies=[],
            )
        )

    return result[:10]


def extract_certifications(
    text: str,
) -> list[CertificationItem]:

    section = extract_section(
        text,
        "certifications",
    )

    if not section:
        return []

    result = []

    for line in section.split("\n"):

        cleaned = line.strip()

        if not cleaned:
            continue

        result.append(
            CertificationItem(
                name=cleaned,
                issuer="",
            )
        )

    return result[:10]


def extract_achievements(
    text: str,
) -> list[str]:

    section = extract_section(
        text,
        "achievements",
    )

    if not section:
        return []

    return [
        line.strip()
        for line in section.split("\n")
        if line.strip()
    ][:20]


def extract_languages(
    text: str,
) -> list[str]:

    section = extract_section(
        text,
        "languages",
    )

    if not section:
        return []

    return [
        line.strip()
        for line in section.split("\n")
        if line.strip()
    ][:20]


def extract_skills(
    text: str,
) -> list[str]:

    return extract_normalized_skills(text)