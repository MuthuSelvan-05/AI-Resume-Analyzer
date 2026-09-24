import re

from app.models.job_data import JobData
from app.services.skill_extraction_service import extract_skills


SECTION_HEADINGS = {
    "required skills",
    "requirements",
    "required qualifications",
    "must have",
    "technical requirements",
    "preferred skills",
    "preferred qualifications",
    "nice to have",
    "good to have",
    "desired skills",
    "education",
    "educational qualifications",
    "education requirements",
    "qualifications",
    "responsibilities",
    "job responsibilities",
    "roles and responsibilities",
    "experience",
    "work experience",
    "about the role",
    "about the job",
    "job description",
}


def extract_job_information(text: str) -> JobData:
    normalized_text = normalize_text(text)

    return JobData(
        title=extract_title(normalized_text),
        company=extract_company(normalized_text),
        required_skills=extract_required_skills(normalized_text),
        preferred_skills=extract_preferred_skills(normalized_text),
        experience_years=extract_experience_years(normalized_text),
        education_requirements=extract_education_requirements(normalized_text),
        keywords=extract_keywords(normalized_text),
    )


def normalize_text(text: str) -> str:
    text = text.replace("\r\n", "\n")
    text = text.replace("\r", "\n")

    lines = []

    for line in text.split("\n"):
        cleaned = re.sub(r"[ \t]+", " ", line).strip()

        if cleaned:
            lines.append(cleaned)

    return "\n".join(lines)


def extract_title(text: str) -> str | None:
    lines = text.split("\n")

    patterns = [
        r"^job title\s*:\s*(.+)$",
        r"^position\s*:\s*(.+)$",
        r"^role\s*:\s*(.+)$",
        r"^designation\s*:\s*(.+)$",
    ]

    for line in lines[:15]:
        for pattern in patterns:
            match = re.match(pattern, line, re.IGNORECASE)

            if match:
                return match.group(1).strip()

    common_titles = [
        "software engineer",
        "software developer",
        "java developer",
        "python developer",
        "backend developer",
        "frontend developer",
        "full stack developer",
        "full stack engineer",
        "data analyst",
        "data scientist",
        "machine learning engineer",
        "ai engineer",
        "devops engineer",
        "cloud engineer",
        "web developer",
        "android developer",
    ]

    for line in lines[:20]:
        lower_line = line.lower()

        for title in common_titles:
            if title in lower_line:
                return line.strip()

    return None


def extract_company(text: str) -> str | None:
    lines = text.split("\n")

    patterns = [
        r"^company\s*:\s*(.+)$",
        r"^organization\s*:\s*(.+)$",
        r"^employer\s*:\s*(.+)$",
    ]

    for line in lines[:20]:
        for pattern in patterns:
            match = re.match(pattern, line, re.IGNORECASE)

            if match:
                return match.group(1).strip()

    return None


def extract_required_skills(text: str) -> list[str]:
    section = extract_section(
        text,
        [
            "required skills",
            "requirements",
            "required qualifications",
            "must have",
            "technical requirements",
        ],
    )

    if not section:
        return []

    return extract_skills(section)


def extract_preferred_skills(text: str) -> list[str]:
    section = extract_section(
        text,
        [
            "preferred skills",
            "preferred qualifications",
            "nice to have",
            "good to have",
            "desired skills",
        ],
    )

    if not section:
        return []

    return extract_skills(section)


def extract_experience_years(text: str) -> float | None:
    patterns = [
        r"(\d+(?:\.\d+)?)\+?\s*(?:years?|yrs?)\s+of\s+experience",
        r"minimum\s+of\s+(\d+(?:\.\d+)?)\+?\s*(?:years?|yrs?)",
        r"at least\s+(\d+(?:\.\d+)?)\+?\s*(?:years?|yrs?)",
        r"(\d+(?:\.\d+)?)\+?\s*(?:years?|yrs?)\s+experience",
    ]

    for pattern in patterns:
        match = re.search(pattern, text, re.IGNORECASE)

        if match:
            return float(match.group(1))

    return None


def extract_education_requirements(text: str) -> list[str]:
    section = extract_section(
        text,
        [
            "education",
            "educational qualifications",
            "education requirements",
            "qualifications",
        ],
    )

    if not section:
        return []

    result = []

    for line in section.split("\n"):
        cleaned = line.strip()

        if cleaned:
            result.append(cleaned)

    return result[:10]


def extract_keywords(text: str) -> list[str]:
    skills = extract_skills(text)

    keyword_patterns = [
        "experience",
        "development",
        "software",
        "engineering",
        "database",
        "api",
        "cloud",
        "deployment",
        "testing",
        "debugging",
        "problem solving",
        "communication",
        "teamwork",
        "agile",
        "scrum",
    ]

    normalized_text = text.lower()

    found_keywords = set(skills)

    for keyword in keyword_patterns:
        if keyword in normalized_text:
            found_keywords.add(keyword)

    return sorted(found_keywords)


def extract_section(
    text: str,
    section_names: list[str],
) -> str:

    lines = text.split("\n")

    normalized_names = {
        normalize_heading(name)
        for name in section_names
    }

    start_index = None

    for index, line in enumerate(lines):
        normalized_line = normalize_heading(line)

        if normalized_line in normalized_names:
            start_index = index + 1
            break

    if start_index is None:
        return ""

    section_lines = []

    for line in lines[start_index:]:
        normalized_line = normalize_heading(line)

        if normalized_line in SECTION_HEADINGS:
            break

        section_lines.append(line)

    return "\n".join(section_lines).strip()


def normalize_heading(text: str) -> str:
    return re.sub(
        r"[^a-z0-9 ]",
        "",
        text.lower(),
    ).strip()