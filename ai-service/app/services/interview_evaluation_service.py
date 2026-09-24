from typing import Any


def evaluate_interview_answer(
    question: str,
    answer: str,
    category: str,
    difficulty: str,
    expected_topics: list[str],
) -> dict[str, Any]:

    normalized_answer = answer.strip()

    if not normalized_answer:
        return {
            "overall_score": 0.0,
            "relevance_score": 0.0,
            "completeness_score": 0.0,
            "technical_score": 0.0,
            "communication_score": 0.0,
            "strengths": [],
            "improvements": [
                "Provide an answer to the interview question."
            ],
            "feedback": "No answer was provided.",
        }

    relevance_score = calculate_relevance_score(
        question,
        normalized_answer,
        expected_topics,
    )

    completeness_score = calculate_completeness_score(
        normalized_answer,
        expected_topics,
    )

    technical_score = calculate_technical_score(
        normalized_answer,
        category,
    )

    communication_score = calculate_communication_score(
        normalized_answer,
    )

    overall_score = round(
        relevance_score * 0.30
        + completeness_score * 0.25
        + technical_score * 0.25
        + communication_score * 0.20,
        2,
    )

    strengths = generate_strengths(
        normalized_answer,
        relevance_score,
        completeness_score,
        technical_score,
        communication_score,
    )

    improvements = generate_improvements(
        normalized_answer,
        relevance_score,
        completeness_score,
        technical_score,
        communication_score,
        expected_topics,
    )

    feedback = generate_feedback(
        overall_score,
        category,
    )

    return {
        "overall_score": overall_score,
        "relevance_score": relevance_score,
        "completeness_score": completeness_score,
        "technical_score": technical_score,
        "communication_score": communication_score,
        "strengths": strengths,
        "improvements": improvements,
        "feedback": feedback,
    }


def calculate_relevance_score(
    question: str,
    answer: str,
    expected_topics: list[str],
) -> float:

    answer_words = set(answer.lower().split())

    if not answer_words:
        return 0.0

    question_words = {
        word.strip(".,?!:;()")
        for word in question.lower().split()
        if len(word) > 3
    }

    topic_words = set()

    for topic in expected_topics:
        topic_words.update(
            word.strip(".,?!:;()")
            for word in topic.lower().split()
            if len(word) > 3
        )

    relevant_words = question_words.union(topic_words)

    if not relevant_words:
        return 50.0

    matches = sum(
        1
        for word in relevant_words
        if word in answer_words
    )

    score = (matches / len(relevant_words)) * 100

    return round(min(100.0, score), 2)


def calculate_completeness_score(
    answer: str,
    expected_topics: list[str],
) -> float:

    word_count = len(answer.split())

    if word_count < 20:
        length_score = 30.0
    elif word_count < 50:
        length_score = 60.0
    elif word_count < 100:
        length_score = 85.0
    else:
        length_score = 100.0

    if not expected_topics:
        return length_score

    answer_lower = answer.lower()

    matched_topics = sum(
        1
        for topic in expected_topics
        if topic.lower() in answer_lower
    )

    topic_score = (
        matched_topics / len(expected_topics)
    ) * 100

    return round(
        length_score * 0.5
        + topic_score * 0.5,
        2,
    )


def calculate_technical_score(
    answer: str,
    category: str,
) -> float:

    technical_keywords = {
        "technical": [
            "implementation",
            "algorithm",
            "database",
            "api",
            "performance",
            "testing",
            "debugging",
            "architecture",
            "security",
        ],
        "project": [
            "implemented",
            "developed",
            "designed",
            "tested",
            "database",
            "api",
            "technology",
            "challenge",
        ],
        "hr": [
            "experience",
            "team",
            "learning",
            "goal",
            "responsibility",
        ],
        "scenario": [
            "analyze",
            "identify",
            "solution",
            "communicate",
            "test",
            "prioritize",
        ],
    }

    keywords = technical_keywords.get(
        category.lower(),
        technical_keywords["technical"],
    )

    answer_lower = answer.lower()

    matches = sum(
        1
        for keyword in keywords
        if keyword in answer_lower
    )

    if not keywords:
        return 50.0

    score = (matches / len(keywords)) * 100

    return round(min(100.0, score), 2)


def calculate_communication_score(answer: str) -> float:

    sentences = [
        sentence.strip()
        for sentence in answer.replace("!", ".").replace("?", ".").split(".")
        if sentence.strip()
    ]

    word_count = len(answer.split())

    if word_count == 0:
        return 0.0

    score = 50.0

    if len(sentences) >= 2:
        score += 15.0

    if len(sentences) >= 4:
        score += 10.0

    if word_count >= 50:
        score += 10.0

    if word_count >= 100:
        score += 10.0

    if any(
        marker in answer.lower()
        for marker in [
            "because",
            "therefore",
            "for example",
            "for instance",
            "however",
        ]
    ):
        score += 5.0

    return round(min(100.0, score), 2)


def generate_strengths(
    answer: str,
    relevance_score: float,
    completeness_score: float,
    technical_score: float,
    communication_score: float,
) -> list[str]:

    strengths = []

    if relevance_score >= 70:
        strengths.append(
            "The answer is relevant to the interview question."
        )

    if completeness_score >= 70:
        strengths.append(
            "The answer provides a reasonable amount of supporting detail."
        )

    if technical_score >= 70:
        strengths.append(
            "The answer includes useful technical or problem-solving concepts."
        )

    if communication_score >= 70:
        strengths.append(
            "The answer is structured clearly enough to communicate the main idea."
        )

    if not strengths:
        strengths.append(
            "The answer provides a starting point that can be developed further."
        )

    return strengths


def generate_improvements(
    answer: str,
    relevance_score: float,
    completeness_score: float,
    technical_score: float,
    communication_score: float,
    expected_topics: list[str],
) -> list[str]:

    improvements = []

    if relevance_score < 70:
        improvements.append(
            "Keep the answer more directly focused on the question."
        )

    if completeness_score < 70:
        improvements.append(
            "Add specific examples, reasoning, and supporting details."
        )

    if technical_score < 70:
        improvements.append(
            "Include more relevant technical concepts, implementation details, "
            "or problem-solving steps where applicable."
        )

    if communication_score < 70:
        improvements.append(
            "Use a clearer structure with a concise beginning, explanation, "
            "and conclusion."
        )

    if expected_topics:
        missing_topics = [
            topic
            for topic in expected_topics
            if topic.lower() not in answer.lower()
        ]

        if missing_topics:
            improvements.append(
                "Consider addressing these expected topics: "
                + ", ".join(missing_topics)
                + "."
            )

    if not improvements:
        improvements.append(
            "Continue supporting your answer with specific real-world examples."
        )

    return improvements[:5]


def generate_feedback(
    overall_score: float,
    category: str,
) -> str:

    if overall_score >= 80:
        level = "strong"
    elif overall_score >= 60:
        level = "good"
    elif overall_score >= 40:
        level = "developing"
    else:
        level = "needs improvement"

    return (
        f"This {category.lower()} interview answer is at a "
        f"{level} level based on relevance, completeness, "
        "technical content, and communication."
    )