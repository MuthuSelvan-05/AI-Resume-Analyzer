import re


SKILL_ALIASES = {
    "java": [
        "java",
    ],
    "python": [
        "python",
    ],
    "c": [
        "c programming",
        "c language",
    ],
    "c++": [
        "c++",
        "cpp",
    ],
    "c#": [
        "c#",
        "c sharp",
    ],
    "javascript": [
        "javascript",
        "js",
    ],
    "typescript": [
        "typescript",
    ],
    "html": [
        "html",
        "html5",
    ],
    "css": [
        "css",
        "css3",
    ],
    "react": [
        "react",
        "react.js",
        "reactjs",
    ],
    "angular": [
        "angular",
        "angular.js",
        "angularjs",
    ],
    "vue": [
        "vue",
        "vue.js",
        "vuejs",
    ],
    "node.js": [
        "node.js",
        "nodejs",
        "node",
    ],
    "express": [
        "express",
        "express.js",
        "expressjs",
    ],
    "spring": [
        "spring",
        "spring framework",
    ],
    "spring boot": [
        "spring boot",
        "springboot",
    ],
    "hibernate": [
        "hibernate",
    ],
    "rest api": [
        "rest api",
        "restful api",
        "rest services",
    ],
    "mysql": [
        "mysql",
    ],
    "postgresql": [
        "postgresql",
        "postgres",
        "postgres db",
    ],
    "mongodb": [
        "mongodb",
        "mongo db",
    ],
    "sql": [
        "sql",
    ],
    "git": [
        "git",
    ],
    "github": [
        "github",
    ],
    "docker": [
        "docker",
    ],
    "kubernetes": [
        "kubernetes",
        "k8s",
    ],
    "aws": [
        "aws",
        "amazon web services",
    ],
    "azure": [
        "azure",
        "microsoft azure",
    ],
    "google cloud": [
        "google cloud",
        "gcp",
        "google cloud platform",
    ],
    "machine learning": [
        "machine learning",
        "ml",
    ],
    "deep learning": [
        "deep learning",
        "dl",
    ],
    "natural language processing": [
        "natural language processing",
        "nlp",
    ],
    "tensorflow": [
        "tensorflow",
    ],
    "pytorch": [
        "pytorch",
    ],
    "scikit-learn": [
        "scikit-learn",
        "sklearn",
    ],
    "pandas": [
        "pandas",
    ],
    "numpy": [
        "numpy",
    ],
    "fastapi": [
        "fastapi",
    ],
    "flask": [
        "flask",
    ],
    "android": [
        "android",
    ],
    "android studio": [
        "android studio",
    ],
}


def normalize_skill(skill: str) -> str:
    """
    Convert a skill name or alias into a canonical skill name.
    """

    normalized = skill.strip().lower()

    for canonical_name, aliases in SKILL_ALIASES.items():

        if normalized in aliases:
            return canonical_name

    return normalized


def extract_skills(text: str) -> list[str]:
    """
    Detect known skills from resume text and return
    canonical skill names.
    """

    normalized_text = text.lower()

    found_skills = set()

    for canonical_name, aliases in SKILL_ALIASES.items():

        for alias in aliases:

            pattern = (
                r"(?<![a-z0-9])"
                + re.escape(alias.lower())
                + r"(?![a-z0-9])"
            )

            if re.search(pattern, normalized_text):
                found_skills.add(canonical_name)
                break

    return sorted(found_skills)