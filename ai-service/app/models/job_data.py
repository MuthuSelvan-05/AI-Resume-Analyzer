from pydantic import BaseModel, Field
from typing import List, Optional


class JobData(BaseModel):
    title: Optional[str] = None
    company: Optional[str] = None

    required_skills: List[str] = Field(default_factory=list)
    preferred_skills: List[str] = Field(default_factory=list)

    experience_years: Optional[float] = None
    education_requirements: List[str] = Field(default_factory=list)

    keywords: List[str] = Field(default_factory=list)