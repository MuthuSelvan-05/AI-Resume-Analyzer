# AI Resume Analyzer & Job Matcher

An AI-powered career assistant that analyzes resumes, compares them with job requirements, identifies skill gaps, generates personalized learning roadmaps, and provides interview practice.

## Overview

AI Resume Analyzer & Job Matcher is a full-stack SaaS-style application designed to help job seekers understand how well their resume matches a target role and what they can improve.

The platform combines:

- Resume parsing
- AI-powered resume analysis
- Job description management
- Skill matching
- Missing skill detection
- Personalized learning roadmaps
- Learning resources
- Interview question generation
- AI-assisted interview answer evaluation
- Progress tracking
- Secure authentication

## Architecture

```text
                    ┌──────────────────────┐
                    │   React Frontend     │
                    │ React + Vite         │
                    │ Tailwind CSS         │
                    └──────────┬───────────┘
                               │ REST API
                               ▼
                    ┌──────────────────────┐
                    │ Spring Boot Backend  │
                    │ Java 21              │
                    │ Spring Security      │
                    │ JWT                  │
                    │ JPA / Hibernate      │
                    └───────┬───────┬──────┘
                            │       │
                    REST API│       │JPA
                            │       ▼
                            │  ┌─────────────┐
                            │  │   MySQL     │
                            │  │  Database   │
                            │  └─────────────┘
                            │
                            ▼
                    ┌──────────────────────┐
                    │ Python AI Service    │
                    │ FastAPI              │
                    │ NLP / Parsing        │
                    │ Embeddings           │
                    └──────────────────────┘