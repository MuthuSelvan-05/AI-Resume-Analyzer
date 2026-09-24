from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity


MODEL_NAME = "all-MiniLM-L6-v2"

_model = None


def get_model():
    global _model

    if _model is None:
        _model = SentenceTransformer(MODEL_NAME)

    return _model


def calculate_semantic_similarity(
    resume_text: str,
    job_description: str,
) -> float:

    if not resume_text.strip() or not job_description.strip():
        return 0.0

    model = get_model()

    embeddings = model.encode(
        [
            resume_text,
            job_description,
        ]
    )

    similarity = cosine_similarity(
        [embeddings[0]],
        [embeddings[1]],
    )[0][0]

    similarity = max(0.0, min(1.0, float(similarity)))

    return round(similarity, 4)


def calculate_similarity_percentage(
    resume_text: str,
    job_description: str,
) -> float:

    similarity = calculate_semantic_similarity(
        resume_text,
        job_description,
    )

    return round(similarity * 100, 2)