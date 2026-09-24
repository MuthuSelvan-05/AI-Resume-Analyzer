from pydantic import BaseModel, Field
from typing import List, Optional


class EducationItem(BaseModel):
    institution: str = ""
    degree: str = ""
    field_of_study: str = ""
    start_date: Optional[str] = None
    end_date: Optional[str] = None


class ExperienceItem(BaseModel):
    company: str = ""
    role: str = ""
    description: str = ""


class ProjectItem(BaseModel):
    name: str = ""
    description: str = ""
    technologies: List[str] = Field(default_factory=list)


class CertificationItem(BaseModel):
    name: str = ""
    issuer: str = ""


class ResumeData(BaseModel):
    name: Optional[str] = None
    email: Optional[str] = None
    phone: Optional[str] = None

    education: List[EducationItem] = Field(
        default_factory=list
    )

    experience: List[ExperienceItem] = Field(
        default_factory=list
    )

    projects: List[ProjectItem] = Field(
        default_factory=list
    )

    certifications: List[CertificationItem] = Field(
        default_factory=list
    )

    achievements: List[str] = Field(
        default_factory=list
    )

    languages: List[str] = Field(
        default_factory=list
    )

    skills: List[str] = Field(
        default_factory=list
    )