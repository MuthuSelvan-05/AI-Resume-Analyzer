import { useEffect, useMemo, useState } from "react";
import {
  AlertCircle,
  BarChart3,
  CheckCircle2,
  ChevronRight,
  Loader2,
  MessageSquareText,
  Play,
  RotateCcw,
  Sparkles,
  Target,
  Trophy,
} from "lucide-react";

import analysisService from "../services/analysisService";
import jobService from "../services/jobService";
import interviewService from "../services/interviewService";

function Interview() {
  const [analyses, setAnalyses] = useState([]);
  const [jobs, setJobs] = useState([]);

  const [selectedAnalysisId, setSelectedAnalysisId] =
    useState("");

  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answer, setAnswer] = useState("");
  const [evaluation, setEvaluation] = useState(null);
  const [interviewCompleted, setInterviewCompleted] = useState(false);

  const [questionCount, setQuestionCount] = useState(10);

  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      setError("");

      const [analysisData, jobData] = await Promise.all([
        analysisService.getAnalyses(),
        jobService.getJobs(),
      ]);

      setAnalyses(
        Array.isArray(analysisData) ? analysisData : [],
      );

      setJobs(Array.isArray(jobData) ? jobData : []);
    } catch (err) {
      console.error("Failed to load interview data:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.detail ||
          "Failed to load interview data.",
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleGenerate() {
    if (!selectedAnalysisId) {
      setError("Please select an analysis first.");
      return;
    }

    try {
      setGenerating(true);
      setError("");
      setSuccess("");
      setQuestions([]);
      setEvaluation(null);
      setInterviewCompleted(false);
      setAnswer("");
      setCurrentIndex(0);

      const selectedAnalysis = analyses.find(
        (analysis) =>
          Number(analysis.id) === Number(selectedAnalysisId),
      );

      if (!selectedAnalysis) {
        throw new Error("Selected analysis was not found.");
      }

      const job = jobs.find(
        (item) =>
          Number(item.id) ===
          Number(selectedAnalysis.jobId),
      );

      await interviewService.generateQuestions({
        analysisId: Number(selectedAnalysis.id),
        resumeText: null,
        jobTitle: job?.title || null,
        jobDescription: job?.description || null,
        matchedSkills:
          selectedAnalysis.matchedSkills || [],
        missingSkills:
          selectedAnalysis.missingSkills || [],
        questionCount: Number(questionCount),
      });

      const savedQuestions =
    await interviewService.getQuestionsByAnalysis(
        Number(selectedAnalysis.id)
    );

const generatedQuestions = (savedQuestions || []).slice(
    -Number(questionCount)
);

setQuestions(generatedQuestions);

setSuccess(
    `${generatedQuestions.length} interview questions generated.`
);
    } catch (err) {
      console.error(
        "Failed to generate interview questions:",
        err,
      );

      setError(
        err.response?.data?.message ||
          err.response?.data?.detail ||
          err.message ||
          "Failed to generate interview questions.",
      );
    } finally {
      setGenerating(false);
    }
  }

  async function handleSubmitAnswer() {
    const currentQuestion = questions[currentIndex];

    if (!currentQuestion) {
      return;
    }

    if (!answer.trim()) {
      setError(
        "Please enter your answer before submitting.",
      );
      return;
    }

    try {
      setSubmitting(true);
      setError("");
      setSuccess("");
      setEvaluation(null);

      const result =
        await interviewService.submitAnswer(
          currentQuestion.id,
          answer,
        );

      setEvaluation(result);

      setSuccess("Answer evaluated successfully.");
    } catch (err) {
      console.error(
        "Failed to evaluate answer:",
        err,
      );

      setError(
        err.response?.data?.message ||
          err.response?.data?.detail ||
          "Failed to evaluate answer.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  function handleNextQuestion() {
    if (currentIndex >= questions.length - 1) {
      setSuccess(
        "Interview practice completed. Great work!",
      );
      return;
    }

    setCurrentIndex((current) => current + 1);
    setAnswer("");
    setEvaluation(null);
    setSuccess("");
    setError("");
  }

  function handleFinishInterview() {
    setInterviewCompleted(true);
    setAnswer("");
    setEvaluation(null);
    setSuccess("Interview completed successfully. Great work!");
    setError("");
  }

  function handleRestart() {
    setInterviewCompleted(false);
    setQuestions([]);
    setCurrentIndex(0);
    setAnswer("");
    setEvaluation(null);
    setSuccess("");
    setError("");
  }

  const currentQuestion = questions[currentIndex];

  const progress = questions.length
    ? Math.round(
        ((currentIndex + 1) / questions.length) * 100,
      )
    : 0;

  const completedQuestions = Math.min(
    currentIndex + (evaluation ? 1 : 0),
    questions.length,
  );

  const selectedAnalysis = useMemo(
    () =>
      analyses.find(
        (analysis) =>
          Number(analysis.id) ===
          Number(selectedAnalysisId),
      ),
    [analyses, selectedAnalysisId],
  );

  const selectedJob = useMemo(
    () =>
      jobs.find(
        (job) =>
          Number(job.id) ===
          Number(selectedAnalysis?.jobId),
      ),
    [jobs, selectedAnalysis],
  );

  if (loading) {
    return (
      <div className="flex min-h-[500px] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-indigo-400" />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-7xl space-y-8">
      {/* Header */}
      <section>
        <div className="flex flex-col justify-between gap-5 md:flex-row md:items-end">
          <div>
            <div className="flex items-center gap-2 text-sm font-medium text-indigo-400">
              <Sparkles size={16} />
              AI Interview Simulator
            </div>

            <h1 className="mt-2 text-3xl font-bold tracking-tight text-white">
              Interview Prep
            </h1>

            <p className="mt-2 max-w-2xl text-slate-400">
              Practice personalized interview questions based
              on your resume analysis and target job, then get
              AI-powered feedback on every answer.
            </p>
          </div>

          {questions.length > 0 && (
            <button
              type="button"
              onClick={handleRestart}
              className="inline-flex items-center justify-center gap-2 rounded-lg border border-slate-700 px-4 py-2.5 text-sm font-medium text-slate-300 transition hover:bg-slate-800"
            >
              <RotateCcw size={16} />
              New Practice
            </button>
          )}
        </div>
      </section>

      {/* Alerts */}
      {error && (
        <div className="flex items-start gap-3 rounded-xl border border-red-500/20 bg-red-500/10 p-4 text-sm text-red-300">
          <AlertCircle className="mt-0.5 h-5 w-5 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {success && (
        <div className="flex items-start gap-3 rounded-xl border border-emerald-500/20 bg-emerald-500/10 p-4 text-sm text-emerald-300">
          <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0" />
          <span>{success}</span>
        </div>
      )}

      {/* Setup */}
      {questions.length === 0 && (
        <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-500/10">
              <MessageSquareText className="h-5 w-5 text-indigo-400" />
            </div>

            <div>
              <h2 className="font-semibold text-white">
                Start Interview Practice
              </h2>

              <p className="mt-1 text-sm text-slate-500">
                Choose one of your completed AI analyses.
              </p>
            </div>
          </div>

          <div className="mt-6 grid gap-5 md:grid-cols-[1fr_180px_auto] md:items-end">
            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300">
                Resume Analysis
              </label>

              <select
                value={selectedAnalysisId}
                onChange={(event) =>
                  setSelectedAnalysisId(
                    event.target.value,
                  )
                }
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white outline-none transition focus:border-indigo-500"
              >
                <option value="">
                  Select an analysis
                </option>

                {analyses.map((analysis) => (
                  <option
                    key={analysis.id}
                    value={analysis.id}
                  >
                    Analysis #{analysis.id} — Score{" "}
                    {Math.round(
                      analysis.overallScore || 0,
                    )}
                  </option>
                ))}
              </select>

              {analyses.length === 0 && (
                <p className="mt-2 text-xs text-amber-400">
                  Complete a resume analysis first.
                </p>
              )}
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300">
                Questions
              </label>

              <select
                value={questionCount}
                onChange={(event) =>
                  setQuestionCount(
                    Number(event.target.value),
                  )
                }
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white outline-none transition focus:border-indigo-500"
              >
                <option value={5}>5 Questions</option>
                <option value={10}>10 Questions</option>
                <option value={15}>15 Questions</option>
                <option value={20}>20 Questions</option>
              </select>
            </div>

            <button
              type="button"
              onClick={handleGenerate}
              disabled={
                generating || !selectedAnalysisId
              }
              className="inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {generating ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Play className="h-4 w-4" />
              )}

              {generating
                ? "Generating..."
                : "Generate Questions"}
            </button>
          </div>

          {selectedAnalysis && (
            <div className="mt-6 grid gap-3 sm:grid-cols-3">
              <InfoCard
                label="Analysis Score"
                value={`${Math.round(
                  selectedAnalysis.overallScore || 0,
                )}/100`}
              />

              <InfoCard
                label="Target Job"
                value={
                  selectedJob
                    ? selectedJob.title
                    : "Job unavailable"
                }
              />

              <InfoCard
                label="Missing Skills"
                value={
                  selectedAnalysis.missingSkills
                    ?.length || 0
                }
              />
            </div>
          )}
        </section>
      )}

      {/* Interview completed */}
      {interviewCompleted && (
        <section className="rounded-2xl border border-emerald-500/20 bg-slate-900/70 p-10 text-center">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-emerald-500/10">
            <Trophy className="h-8 w-8 text-emerald-400" />
          </div>

          <h2 className="mt-6 text-2xl font-bold text-white">
            Interview Completed
          </h2>

          <p className="mx-auto mt-2 max-w-lg text-sm leading-6 text-slate-400">
            You completed all {questions.length} interview questions.
            Review your feedback above or start a new practice session.
          </p>

          <div className="mx-auto mt-6 flex max-w-md items-center justify-center gap-3 rounded-xl bg-slate-800/60 p-4">
            <CheckCircle2 className="h-5 w-5 text-emerald-400" />
            <span className="text-sm font-medium text-slate-300">
              {questions.length} / {questions.length} Questions Completed
            </span>
          </div>

          <button
            type="button"
            onClick={handleRestart}
            className="mt-6 inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-500"
          >
            <RotateCcw size={16} />
            Practice Again
          </button>
        </section>
      )}

      {/* Interview progress */}
      {currentQuestion && !interviewCompleted && (
        <>
          <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
            <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
              <div>
                <p className="text-xs font-medium uppercase tracking-wider text-indigo-400">
                  Interview Progress
                </p>

                <p className="mt-1 text-sm text-slate-400">
                  Question {currentIndex + 1} of{" "}
                  {questions.length}
                </p>
              </div>

              <div className="flex items-center gap-3">
                <span className="text-sm font-medium text-slate-300">
                  {progress}%
                </span>

                <div className="h-2 w-32 overflow-hidden rounded-full bg-slate-800">
                  <div
                    className="h-full rounded-full bg-indigo-500 transition-all duration-500"
                    style={{
                      width: `${progress}%`,
                    }}
                  />
                </div>
              </div>
            </div>

            <div className="mt-5 grid grid-cols-5 gap-2 sm:grid-cols-10">
              {questions.map((question, index) => (
                <div
                  key={question.id || index}
                  className={`h-1.5 rounded-full ${
                    index < completedQuestions
                      ? "bg-indigo-500"
                      : index === currentIndex
                        ? "bg-indigo-400"
                        : "bg-slate-800"
                  }`}
                />
              ))}
            </div>
          </section>

          {/* Question */}
          <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
            <div className="flex flex-col justify-between gap-5 md:flex-row md:items-start">
              <div className="max-w-3xl">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="rounded-full bg-indigo-500/10 px-3 py-1 text-xs font-medium text-indigo-300">
                    Question {currentIndex + 1}
                  </span>

                  {currentQuestion.category && (
                    <span className="rounded-full bg-slate-800 px-3 py-1 text-xs text-slate-300">
                      {currentQuestion.category}
                    </span>
                  )}

                  {currentQuestion.difficulty && (
                    <span className="rounded-full bg-slate-800 px-3 py-1 text-xs text-slate-300">
                      {currentQuestion.difficulty}
                    </span>
                  )}
                </div>

                <h2 className="mt-5 text-2xl font-semibold leading-9 text-white">
                  {currentQuestion.question}
                </h2>
              </div>

              <div className="hidden h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-indigo-500/10 md:flex">
                <Target className="h-6 w-6 text-indigo-400" />
              </div>
            </div>

            <div className="mt-7">
              <label className="mb-2 block text-sm font-medium text-slate-300">
                Your Answer
              </label>

              <textarea
                value={answer}
                onChange={(event) =>
                  setAnswer(event.target.value)
                }
                placeholder="Explain your answer clearly. Include examples, technical details, or real-world experience where relevant..."
                rows={9}
                className="w-full resize-none rounded-xl border border-slate-700 bg-slate-950 px-4 py-4 text-sm leading-7 text-white outline-none placeholder:text-slate-600 transition focus:border-indigo-500"
              />

              <div className="mt-2 flex justify-between text-xs text-slate-600">
                <span>
                  Try to provide a structured and specific
                  answer.
                </span>

                <span>
                  {answer.trim().split(/\s+/).filter(Boolean).length}{" "}
                  words
                </span>
              </div>
            </div>

            <div className="mt-5 flex flex-wrap gap-3">
              <button
                type="button"
                onClick={handleSubmitAnswer}
                disabled={
                  submitting || !answer.trim()
                }
                className="inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {submitting && (
                  <Loader2 className="h-4 w-4 animate-spin" />
                )}

                {submitting
                  ? "Evaluating..."
                  : "Submit Answer"}
              </button>

              {evaluation && (
                <button
                  type="button"
                  onClick={
                    currentIndex === questions.length - 1
                      ? handleFinishInterview
                      : handleNextQuestion
                  }
                  className="inline-flex items-center gap-2 rounded-xl border border-slate-700 px-5 py-3 text-sm font-semibold text-slate-200 transition hover:bg-slate-800"
                >
                  {currentIndex === questions.length - 1
                    ? "Finish Interview"
                    : "Next Question"}

                  <ChevronRight size={16} />
                </button>
              )}
            </div>
          </section>
        </>
      )}

      {/* Evaluation */}
      {evaluation && !interviewCompleted && (
        <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
          <div className="flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
            <div className="flex items-center gap-3">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-emerald-500/10">
                <BarChart3 className="h-5 w-5 text-emerald-400" />
              </div>

              <div>
                <h2 className="text-lg font-semibold text-white">
                  AI Answer Evaluation
                </h2>

                <p className="mt-1 text-sm text-slate-500">
                  Feedback generated from your submitted answer.
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <Trophy
                size={18}
                className="text-amber-400"
              />

              <div className="text-right">
                <p className="text-xs text-slate-500">
                  Answer Score
                </p>

                <p className="text-3xl font-bold text-indigo-400">
                  {Math.round(evaluation.score || 0)}
                  <span className="ml-1 text-sm font-normal text-slate-500">
                    / 100
                  </span>
                </p>
              </div>
            </div>
          </div>

          {evaluation.feedback && (
            <div className="mt-6 rounded-xl border border-slate-800 bg-slate-950 p-5">
              <p className="mb-2 text-xs font-medium uppercase tracking-wide text-indigo-400">
                Feedback
              </p>

              <p className="whitespace-pre-line text-sm leading-7 text-slate-300">
                {evaluation.feedback}
              </p>
            </div>
          )}

          {evaluation.strengths?.length > 0 && (
            <div className="mt-5">
              <p className="mb-3 text-sm font-semibold text-white">
                Strengths
              </p>

              <div className="grid gap-3 md:grid-cols-2">
                {evaluation.strengths.map(
                  (strength, index) => (
                    <div
                      key={index}
                      className="flex gap-3 rounded-xl border border-emerald-500/10 bg-emerald-500/[0.03] p-4"
                    >
                      <CheckCircle2
                        size={17}
                        className="mt-0.5 shrink-0 text-emerald-400"
                      />

                      <p className="text-sm leading-6 text-slate-300">
                        {strength}
                      </p>
                    </div>
                  ),
                )}
              </div>
            </div>
          )}

          {evaluation.improvements?.length > 0 && (
            <div className="mt-5">
              <p className="mb-3 text-sm font-semibold text-white">
                Areas to Improve
              </p>

              <div className="grid gap-3 md:grid-cols-2">
                {evaluation.improvements.map(
                  (improvement, index) => (
                    <div
                      key={index}
                      className="rounded-xl border border-amber-500/10 bg-amber-500/[0.03] p-4"
                    >
                      <p className="text-sm leading-6 text-slate-300">
                        {improvement}
                      </p>
                    </div>
                  ),
                )}
              </div>
            </div>
          )}
        </section>
      )}

      {/* Empty state */}
      {questions.length === 0 &&
        !generating &&
        analyses.length === 0 && (
          <section className="rounded-2xl border border-dashed border-slate-700 bg-slate-900/40 p-10 text-center">
            <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-indigo-500/10">
              <MessageSquareText className="h-7 w-7 text-indigo-400" />
            </div>

            <h2 className="mt-5 text-lg font-semibold text-white">
              Complete an analysis first
            </h2>

            <p className="mx-auto mt-2 max-w-lg text-sm leading-6 text-slate-500">
              Your interview questions are generated from your
              resume analysis, matched skills, missing skills,
              and target job.
            </p>
          </section>
        )}
    </div>
  );
}

function InfoCard({ label, value }) {
  return (
    <div className="rounded-xl bg-slate-800/50 p-4">
      <p className="text-xs text-slate-500">
        {label}
      </p>

      <p className="mt-1 truncate text-sm font-medium text-slate-300">
        {value}
      </p>
    </div>
  );
}

export default Interview;