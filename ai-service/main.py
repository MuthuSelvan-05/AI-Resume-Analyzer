from fastapi import FastAPI, Request

from app.api.file_routes import router as file_router
from app.api.resume_routes import router as resume_router
from app.api.job_routes import router as job_router
from app.api.matching_routes import router as matching_router
from app.api.analysis_routes import router as analysis_router
from app.api.suggestion_routes import router as suggestion_router
from app.api.roadmap_routes import router as roadmap_router
from app.api.interview_routes import router as interview_router
from app.api.interview_evaluation_routes import (
    router as interview_evaluation_router,
)


app = FastAPI(
    title="AI Resume Analyzer Service",
    description="AI and NLP service for resume analysis and job matching",
    version="1.0.0",
)


@app.middleware("http")
async def debug_request(request: Request, call_next):

    if request.url.path == "/api/resume/extract-file":

        print("\n========== AI REQUEST DEBUG ==========")
        print("METHOD:", request.method)
        print("URL:", request.url)
        print(
            "CONTENT-TYPE:",
            request.headers.get("content-type")
        )
        print(
            "CONTENT-LENGTH:",
            request.headers.get("content-length")
        )

        content_type = request.headers.get(
            "content-type",
            ""
        )

        if "multipart/form-data" in content_type:
            print("MULTIPART REQUEST: YES")
        else:
            print("MULTIPART REQUEST: NO")

        print("======================================\n")

    return await call_next(request)


app.include_router(resume_router)

app.include_router(file_router)

app.include_router(job_router)

app.include_router(matching_router)

app.include_router(analysis_router)

app.include_router(suggestion_router)

app.include_router(roadmap_router)

app.include_router(interview_router)

app.include_router(interview_evaluation_router)


@app.get("/")
def root():

    return {
        "message": "AI Resume Analyzer AI Service is running"
    }


@app.get("/health")
def health():

    return {
        "status": "healthy"
    }