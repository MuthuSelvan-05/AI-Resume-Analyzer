from typing import List

from fastapi import APIRouter
from pydantic import BaseModel, Field


router = APIRouter(
    prefix="/api/interview",
    tags=["Interview"]
)


class InterviewGenerationRequest(BaseModel):
    resumeText: str | None = None
    jobTitle: str | None = None
    jobDescription: str | None = None
    matchedSkills: List[str] = Field(default_factory=list)
    missingSkills: List[str] = Field(default_factory=list)
    questionCount: int = Field(default=10, ge=1, le=20)


class GeneratedInterviewQuestion(BaseModel):
    questionText: str
    questionType: str
    difficulty: str
    skillName: str


class InterviewGenerationResponse(BaseModel):
    questions: List[GeneratedInterviewQuestion]


@router.post(
    "/generate",
    response_model=InterviewGenerationResponse
)
async def generate_interview_questions(
    request: InterviewGenerationRequest
):

    skills = request.missingSkills + request.matchedSkills

    if not skills:
        skills = ["General Software Engineering"]

    question_templates = [
        (
            "TECHNICAL",
            "EASY",
            "What are the fundamental concepts of {skill}?"
        ),
        (
            "TECHNICAL",
            "MEDIUM",
            "How would you use {skill} in a real-world software project?"
        ),
        (
            "SCENARIO",
            "MEDIUM",
            "Describe a situation where you would choose {skill} to solve a software engineering problem."
        ),
        (
            "TECHNICAL",
            "HARD",
            "What are some advanced concepts or common challenges when working with {skill}?"
        ),
        (
            "BEHAVIORAL",
            "MEDIUM",
            "Tell me about a project or experience where you worked with {skill}."
        ),
        (
            "TECHNICAL",
            "MEDIUM",
            "How would you troubleshoot a problem related to {skill}?"
        ),
        (
            "SCENARIO",
            "HARD",
            "What approach would you take if a production system using {skill} suddenly failed?"
        ),
        (
            "TECHNICAL",
            "HARD",
            "How would you improve the performance of an application using {skill}?"
        ),
        (
            "SCENARIO",
            "MEDIUM",
            "What factors would you consider before choosing {skill} for a new project?"
        ),
        (
            "BEHAVIORAL",
            "MEDIUM",
            "What difficulties have you faced while learning or using {skill}, and how did you overcome them?"
        ),
        (
            "TECHNICAL",
            "EASY",
            "What are the main advantages of using {skill}?"
        ),
        (
            "TECHNICAL",
            "MEDIUM",
            "What are some common mistakes developers make when working with {skill}?"
        ),
        (
            "SCENARIO",
            "HARD",
            "How would you design a scalable solution using {skill}?"
        ),
        (
            "TECHNICAL",
            "MEDIUM",
            "How does {skill} integrate with other technologies in a modern application?"
        ),
        (
            "BEHAVIORAL",
            "MEDIUM",
            "How would you explain {skill} to a beginner?"
        ),
        (
            "TECHNICAL",
            "HARD",
            "What security considerations should developers keep in mind when using {skill}?"
        ),
        (
            "SCENARIO",
            "MEDIUM",
            "If your team disagreed about using {skill}, how would you evaluate the decision?"
        ),
        (
            "TECHNICAL",
            "HARD",
            "How would you test an application feature built using {skill}?"
        ),
        (
            "TECHNICAL",
            "MEDIUM",
            "What tools or practices would you use to develop effectively with {skill}?"
        ),
        (
            "SCENARIO",
            "HARD",
            "How would you handle a large-scale project where {skill} is a critical technology?"
        )
    ]

    questions = []
    used_questions = set()

    index = 0

    while len(questions) < request.questionCount:

        skill = skills[index % len(skills)]

        question_type, difficulty, template = question_templates[
            index % len(question_templates)
        ]

        question_text = template.format(skill=skill)

        # Prevent duplicate questions.
        if question_text not in used_questions:
            questions.append(
                GeneratedInterviewQuestion(
                    questionText=question_text,
                    questionType=question_type,
                    difficulty=difficulty,
                    skillName=skill
                )
            )

            used_questions.add(question_text)

        index += 1

        # Safety protection in case the available combinations are exhausted.
        if index >= len(question_templates) * len(skills):
            break

    return InterviewGenerationResponse(
        questions=questions[:request.questionCount]
    )