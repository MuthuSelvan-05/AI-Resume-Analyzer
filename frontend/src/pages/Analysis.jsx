import { useEffect, useMemo, useState } from "react";
import {
  AlertCircle,
  BarChart3,
  CheckCircle2,
  FileSearch,
  Loader2,
  Play,
  Sparkles,
  Target,
  TrendingUp,
  XCircle,
} from "lucide-react";

import analysisService from "../services/analysisService";
import resumeService from "../services/resumeService";
import jobService from "../services/jobService";

function Analysis() {
  const [resumes, setResumes] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [analyses, setAnalyses] = useState([]);

  const [selectedResumeVersionId, setSelectedResumeVersionId] =
    useState("");
  const [selectedJobId, setSelectedJobId] = useState("");

  const [selectedAnalysis, setSelectedAnalysis] = useState(null);

  const [loading, setLoading] = useState(true);
  const [running, setRunning] = useState(false);
  const [creating, setCreating] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      setError("");

      const [resumeData, jobData, analysisData] =
        await Promise.all([
          resumeService.getResumes(),
          jobService.getJobs(),
          analysisService.getAnalyses(),
        ]);

      setResumes(Array.isArray(resumeData) ? resumeData : []);
      setJobs(Array.isArray(jobData) ? jobData : []);
      setAnalyses(Array.isArray(analysisData) ? analysisData : []);
    } catch (err) {
      console.error("Failed to load analysis data:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.detail ||
          "Failed to load analysis data.",
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateAndRun() {
    if (!selectedResumeVersionId || !selectedJobId) {
      setError("Please select a resume version and a job.");
      return;
    }

    try {
      setCreating(true);
      setError("");
      setSuccess("");
      setSelectedAnalysis(null);

      const resumeVersionId = Number(selectedResumeVersionId);
      const jobId = Number(selectedJobId);

      const existingAnalysis = analyses.find(
        (analysis) =>
          Number(analysis.resumeVersionId) === resumeVersionId &&
          Number(analysis.jobId) === jobId,
      );

      let analysis;

      if (existingAnalysis) {
        analysis = existingAnalysis;

        setSuccess(
          "Existing analysis found. Running AI analysis again...",
        );
      } else {
        analysis = await analysisService.createAnalysis(
          resumeVersionId,
          jobId,
        );

        setSuccess(
          "Analysis created. Running AI analysis...",
        );
      }

      setCreating(false);
      setRunning(true);

      const result = await analysisService.runAnalysis(
        analysis.id,
      );

      setSelectedAnalysis(result);

      setAnalyses((current) => [
        result,
        ...current.filter(
          (item) => item.id !== result.id,
        ),
      ]);

      setSuccess(
        "AI analysis completed successfully.",
      );
    } catch (err) {
      console.error("Failed to run analysis:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.detail ||
          "Failed to run resume analysis.",
      );
    } finally {
      setCreating(false);
      setRunning(false);
    }
  }

  function getResumeVersionLabel(resume) {
    if (!resume.latestVersion) {
      return `${resume.title} — No uploaded version`;
    }

    return `${resume.title} — Version ${resume.latestVersion.versionNumber} (${resume.latestVersion.fileName})`;
  }

  const resumeOptions = resumes.filter(
    (resume) => resume.latestVersion,
  );

  const latestAnalyses = useMemo(() => {
    return [...analyses]
      .sort((a, b) => {
        const first = new Date(
          a.createdAt || a.updatedAt || 0,
        ).getTime();

        const second = new Date(
          b.createdAt || b.updatedAt || 0,
        ).getTime();

        return second - first;
      })
      .slice(0, 5);
  }, [analyses]);

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
              AI Career Intelligence
            </div>

            <h1 className="mt-2 text-3xl font-bold tracking-tight text-white">
              Resume Analysis
            </h1>

            <p className="mt-2 max-w-2xl text-slate-400">
              Compare your resume with a target job and discover
              your strengths, skill gaps, and improvement
              opportunities.
            </p>
          </div>
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

      {/* Start Analysis */}
      <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-500/10">
            <FileSearch className="h-5 w-5 text-indigo-400" />
          </div>

          <div>
            <h2 className="font-semibold text-white">
              Start New Analysis
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              Select the resume and target job you want the AI
              engine to compare.
            </p>
          </div>
        </div>

        <div className="mt-6 grid gap-5 md:grid-cols-2">
          <div>
            <label className="mb-2 block text-sm font-medium text-slate-300">
              Resume
            </label>

            <select
              value={selectedResumeVersionId}
              onChange={(event) =>
                setSelectedResumeVersionId(event.target.value)
              }
              className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white outline-none transition focus:border-indigo-500"
            >
              <option value="">
                Select a resume
              </option>

              {resumeOptions.map((resume) => (
                <option
                  key={resume.latestVersion.id}
                  value={resume.latestVersion.id}
                >
                  {getResumeVersionLabel(resume)}
                </option>
              ))}
            </select>

            {resumeOptions.length === 0 && (
              <p className="mt-2 text-xs text-amber-400">
                Upload a resume version first.
              </p>
            )}
          </div>

          <div>
            <label className="mb-2 block text-sm font-medium text-slate-300">
              Target Job
            </label>

            <select
              value={selectedJobId}
              onChange={(event) =>
                setSelectedJobId(event.target.value)
              }
              className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white outline-none transition focus:border-indigo-500"
            >
              <option value="">
                Select a job
              </option>

              {jobs.map((job) => (
                <option key={job.id} value={job.id}>
                  {job.title} — {job.company}
                </option>
              ))}
            </select>

            {jobs.length === 0 && (
              <p className="mt-2 text-xs text-amber-400">
                Add a job first.
              </p>
            )}
          </div>
        </div>

        <button
          type="button"
          onClick={handleCreateAndRun}
          disabled={
            creating ||
            running ||
            !selectedResumeVersionId ||
            !selectedJobId
          }
          className="mt-6 inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {creating || running ? (
            <Loader2 className="h-4 w-4 animate-spin" />
          ) : (
            <Play className="h-4 w-4" />
          )}

          {creating
            ? "Creating Analysis..."
            : running
              ? "Running AI Analysis..."
              : "Run AI Analysis"}
        </button>
      </section>

      {/* Analysis Result */}
      {selectedAnalysis && (
        <section className="space-y-6">
          {/* Overall score */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
            <div className="flex flex-col justify-between gap-6 md:flex-row md:items-center">
              <div>
                <div className="flex items-center gap-3">
                  <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-500/10">
                    <BarChart3 className="h-5 w-5 text-indigo-400" />
                  </div>

                  <div>
                    <h2 className="text-xl font-semibold text-white">
                      Analysis Result
                    </h2>

                    <p className="mt-1 text-sm text-slate-500">
                      AI-generated resume and job compatibility
                      results.
                    </p>
                  </div>
                </div>
              </div>

              <ScoreRing
                value={selectedAnalysis.overallScore}
              />
            </div>

            {/* Score cards */}
            <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
              <ScoreCard
                label="Skills"
                value={selectedAnalysis.skillScore}
              />

              <ScoreCard
                label="Experience"
                value={selectedAnalysis.experienceScore}
              />

              <ScoreCard
                label="Education"
                value={selectedAnalysis.educationScore}
              />

              <ScoreCard
                label="Keywords"
                value={selectedAnalysis.keywordScore}
              />

              <ScoreCard
                label="Semantic"
                value={selectedAnalysis.semanticScore}
              />
            </div>
          </div>

          {/* Skills */}
          <div className="grid gap-6 lg:grid-cols-3">
            <SkillList
              title="Matched Skills"
              skills={selectedAnalysis.matchedSkills}
              icon={
                <CheckCircle2 className="h-5 w-5 text-emerald-400" />
              }
              tone="success"
            />

            <SkillList
              title="Missing Skills"
              skills={selectedAnalysis.missingSkills}
              icon={
                <XCircle className="h-5 w-5 text-red-400" />
              }
              tone="danger"
            />

            <SkillList
              title="Related Skills"
              skills={selectedAnalysis.relatedSkills}
              icon={
                <Target className="h-5 w-5 text-amber-400" />
              }
              tone="warning"
            />
          </div>

          {/* Suggestions */}
          {selectedAnalysis.suggestions?.length > 0 && (
            <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
              <div className="flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-500/10">
                  <Sparkles className="h-5 w-5 text-indigo-400" />
                </div>

                <div>
                  <h2 className="font-semibold text-white">
                    AI Improvement Suggestions
                  </h2>

                  <p className="mt-1 text-sm text-slate-500">
                    Recommended actions based on your analysis.
                  </p>
                </div>
              </div>

              <div className="mt-6 space-y-3">
                {selectedAnalysis.suggestions.map(
                  (suggestion, index) => (
                    <div
                      key={suggestion.id || index}
                      className="rounded-xl border border-slate-800 bg-slate-950/70 p-5"
                    >
                      <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-start">
                        <div>
                          <p className="text-xs font-medium uppercase tracking-wide text-indigo-400">
                            Recommendation {index + 1}
                          </p>

                          <h3 className="mt-1 font-medium text-white">
                            {suggestion.title}
                          </h3>
                        </div>

                        {suggestion.priority && (
                          <span className="rounded-full bg-indigo-500/10 px-3 py-1 text-xs text-indigo-300">
                            {suggestion.priority}
                          </span>
                        )}
                      </div>

                      <p className="mt-3 text-sm leading-6 text-slate-400">
                        {suggestion.description}
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
      {!selectedAnalysis && analyses.length === 0 && (
        <section className="rounded-2xl border border-dashed border-slate-700 bg-slate-900/40 p-10 text-center">
          <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-indigo-500/10">
            <Sparkles className="h-7 w-7 text-indigo-400" />
          </div>

          <h2 className="mt-5 text-lg font-semibold text-white">
            Your AI analysis will appear here
          </h2>

          <p className="mx-auto mt-2 max-w-lg text-sm leading-6 text-slate-500">
            Choose a resume and target job above to generate your
            first compatibility analysis.
          </p>
        </section>
      )}

      {/* Analysis history */}
      {latestAnalyses.length > 0 && (
        <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-slate-800">
              <TrendingUp className="h-5 w-5 text-slate-300" />
            </div>

            <div>
              <h2 className="font-semibold text-white">
                Analysis History
              </h2>

              <p className="mt-1 text-sm text-slate-500">
                Your recent resume-job compatibility analyses.
              </p>
            </div>
          </div>

          <div className="mt-6 space-y-3">
            {latestAnalyses.map((analysis) => (
              <button
                key={analysis.id}
                type="button"
                onClick={() => setSelectedAnalysis(analysis)}
                className="flex w-full flex-col gap-4 rounded-xl border border-slate-800 bg-slate-950/60 p-4 text-left transition hover:border-slate-700 hover:bg-slate-950 sm:flex-row sm:items-center sm:justify-between"
              >
                <div>
                  <p className="font-medium text-white">
                    Analysis #{analysis.id}
                  </p>

                  <p className="mt-1 text-xs text-slate-500">
                    Resume version {analysis.resumeVersionId}{" "}
                    · Job {analysis.jobId}
                  </p>
                </div>

                <div className="flex items-center gap-4">
                  <div className="text-right">
                    <p className="text-xs text-slate-500">
                      Overall Score
                    </p>

                    <p className="mt-1 text-lg font-bold text-indigo-400">
                      {Math.round(
                        analysis.overallScore || 0,
                      )}
                      /100
                    </p>
                  </div>

                  <span className="text-xs text-slate-600">
                    View
                  </span>
                </div>
              </button>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}

function ScoreRing({ value }) {
  const score = Math.max(
    0,
    Math.min(100, Number(value || 0)),
  );

  const circumference = 2 * Math.PI * 42;
  const offset =
    circumference - (score / 100) * circumference;

  return (
    <div className="relative h-36 w-36 shrink-0">
      <svg
        className="h-full w-full -rotate-90"
        viewBox="0 0 100 100"
      >
        <circle
          cx="50"
          cy="50"
          r="42"
          fill="none"
          stroke="currentColor"
          strokeWidth="8"
          className="text-slate-800"
        />

        <circle
          cx="50"
          cy="50"
          r="42"
          fill="none"
          stroke="currentColor"
          strokeWidth="8"
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          className="text-indigo-500 transition-all duration-700"
        />
      </svg>

      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-3xl font-bold text-white">
          {Math.round(score)}
        </span>

        <span className="text-xs text-slate-500">
          / 100
        </span>
      </div>
    </div>
  );
}

function ScoreCard({ label, value }) {
  const score = Number(value || 0);

  return (
    <div className="rounded-xl border border-slate-800 bg-slate-950/70 p-4">
      <div className="flex items-center justify-between">
        <p className="text-xs text-slate-500">
          {label}
        </p>

        <BarChart3
          size={14}
          className="text-slate-600"
        />
      </div>

      <div className="mt-3 flex items-end gap-1">
        <span className="text-2xl font-bold text-white">
          {Math.round(score)}
        </span>

        <span className="mb-1 text-xs text-slate-500">
          / 100
        </span>
      </div>

      <div className="mt-3 h-1.5 overflow-hidden rounded-full bg-slate-800">
        <div
          className="h-full rounded-full bg-indigo-500 transition-all duration-500"
          style={{
            width: `${Math.max(
              0,
              Math.min(100, score),
            )}%`,
          }}
        />
      </div>
    </div>
  );
}

function SkillList({
  title,
  skills,
  icon,
  tone,
}) {
  const toneClasses = {
    success:
      "border-emerald-500/10 bg-emerald-500/[0.03]",
    danger:
      "border-red-500/10 bg-red-500/[0.03]",
    warning:
      "border-amber-500/10 bg-amber-500/[0.03]",
  };

  return (
    <div
      className={`rounded-2xl border p-6 ${
        toneClasses[tone] || "border-slate-800 bg-slate-900"
      }`}
    >
      <div className="mb-5 flex items-center gap-3">
        {icon}

        <div>
          <h2 className="font-semibold text-white">
            {title}
          </h2>

          <p className="text-xs text-slate-500">
            {skills?.length || 0} identified
          </p>
        </div>
      </div>

      {skills?.length > 0 ? (
        <div className="flex flex-wrap gap-2">
          {skills.map((skill) => (
            <span
              key={skill}
              className="rounded-full border border-slate-700 bg-slate-950 px-3 py-1.5 text-xs text-slate-300"
            >
              {skill}
            </span>
          ))}
        </div>
      ) : (
        <p className="text-sm text-slate-500">
          No skills found.
        </p>
      )}
    </div>
  );
}

export default Analysis;