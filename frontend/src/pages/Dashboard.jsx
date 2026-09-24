import { useEffect, useMemo, useState } from "react";
import {
  ArrowUpRight,
  BarChart3,
  BriefcaseBusiness,
  CheckCircle2,
  ChevronRight,
  FileText,
  GraduationCap,
  Loader2,
  MessageSquareText,
  Sparkles,
  Target,
  TrendingUp,
  Upload,
  XCircle,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

import dashboardService from "../services/dashboardService";
import useAuth from "../hooks/useAuth";

function Dashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [data, setData] = useState({
    resumes: [],
    jobs: [],
    analyses: [],
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDashboard() {
      try {
        setLoading(true);
        setError("");

        const dashboardData =
          await dashboardService.getDashboardData();

        setData({
          resumes: Array.isArray(dashboardData?.resumes)
            ? dashboardData.resumes
            : [],
          jobs: Array.isArray(dashboardData?.jobs)
            ? dashboardData.jobs
            : [],
          analyses: Array.isArray(dashboardData?.analyses)
            ? dashboardData.analyses
            : [],
        });
      } catch (requestError) {
        console.error("Dashboard loading failed:", requestError);

        setError(
          requestError.response?.data?.message ||
            "Unable to load dashboard data.",
        );
      } finally {
        setLoading(false);
      }
    }

    loadDashboard();
  }, []);

  const latestAnalysis = useMemo(() => {
    if (!data.analyses.length) {
      return null;
    }

    return [...data.analyses].sort((a, b) => {
      const firstDate = new Date(
        a.createdAt || a.updatedAt || 0,
      ).getTime();

      const secondDate = new Date(
        b.createdAt || b.updatedAt || 0,
      ).getTime();

      return secondDate - firstDate;
    })[0];
  }, [data.analyses]);

  const score = Number(
    latestAnalysis?.overallScore ??
      latestAnalysis?.score ??
      0,
  );

  const matchedSkills = Array.isArray(
    latestAnalysis?.matchedSkills,
  )
    ? latestAnalysis.matchedSkills
    : [];

  const missingSkills = Array.isArray(
    latestAnalysis?.missingSkills,
  )
    ? latestAnalysis.missingSkills
    : [];

  const scoreLabel =
    score >= 80
      ? "Strong match"
      : score >= 60
        ? "Good progress"
        : score > 0
          ? "Needs improvement"
          : "Not analyzed";

  const scoreMessage =
    score >= 80
      ? "Your resume aligns well with the selected job."
      : score >= 60
        ? "Your resume has a solid foundation with some areas to improve."
        : score > 0
          ? "Review your missing skills and suggestions to improve your match."
          : "Run an analysis to see your resume readiness.";

  const stats = [
    {
      label: "Resumes",
      value: data.resumes.length,
      icon: FileText,
      description:
        data.resumes.length > 0
          ? "Resume profiles available"
          : "Upload your first resume",
      action: () => navigate("/resumes"),
    },
    {
      label: "Jobs Tracked",
      value: data.jobs.length,
      icon: BriefcaseBusiness,
      description:
        data.jobs.length > 0
          ? "Target roles being tracked"
          : "Add a target job",
      action: () => navigate("/jobs"),
    },
    {
      label: "Analyses",
      value: data.analyses.length,
      icon: Target,
      description:
        data.analyses.length > 0
          ? "Resume-job comparisons"
          : "No analysis yet",
      action: () => navigate("/analysis"),
    },
  ];

  return (
    <div className="mx-auto max-w-7xl space-y-8">
      {/* Header */}
      <section>
        <div className="flex flex-col justify-between gap-6 md:flex-row md:items-end">
          <div>
            <div className="flex items-center gap-2 text-sm font-medium text-blue-400">
              <Sparkles size={16} />
              Career Intelligence
            </div>

            <h2 className="mt-2 text-3xl font-bold tracking-tight">
              Welcome back
              {user?.name ? `, ${user.name}` : ""}
            </h2>

            <p className="mt-2 max-w-2xl text-slate-400">
              Track your resume readiness, analyze job matches,
              close skill gaps, and prepare for your next interview.
            </p>
          </div>

          <button
            type="button"
            onClick={() => navigate("/analysis")}
            className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium transition hover:bg-blue-500"
          >
            Analyze Resume
            <ArrowUpRight size={17} />
          </button>
        </div>
      </section>

      {/* Error */}
      {error && (
        <div className="rounded-xl border border-red-500/20 bg-red-500/10 px-4 py-3 text-sm text-red-400">
          {error}
        </div>
      )}

      {/* Statistics */}
      <section className="grid gap-4 md:grid-cols-3">
        {stats.map((stat) => {
          const Icon = stat.icon;

          return (
            <button
              key={stat.label}
              type="button"
              onClick={stat.action}
              className="group rounded-xl border border-slate-800 bg-slate-900/60 p-5 text-left transition hover:border-slate-700 hover:bg-slate-900"
            >
              <div className="flex items-center justify-between">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600/10">
                  <Icon
                    size={19}
                    className="text-blue-400"
                  />
                </div>

                <ChevronRight
                  size={17}
                  className="text-slate-600 transition group-hover:translate-x-1 group-hover:text-slate-400"
                />
              </div>

              <div className="mt-5 flex items-center gap-2">
                {loading ? (
                  <Loader2
                    size={25}
                    className="animate-spin text-slate-500"
                  />
                ) : (
                  <p className="text-3xl font-bold">
                    {stat.value}
                  </p>
                )}
              </div>

              <p className="mt-1 text-sm font-medium text-slate-300">
                {stat.label}
              </p>

              <p className="mt-1 text-xs text-slate-500">
                {stat.description}
              </p>
            </button>
          );
        })}
      </section>

      {/* Main intelligence section */}
      <section className="grid gap-6 lg:grid-cols-3">
        {/* Resume score */}
        <div className="rounded-xl border border-slate-800 bg-slate-900/60 p-6 lg:col-span-2">
          <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-start">
            <div>
              <div className="flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600/10">
                  <BarChart3
                    size={20}
                    className="text-blue-400"
                  />
                </div>

                <div>
                  <h3 className="font-semibold">
                    Resume Match Score
                  </h3>

                  <p className="text-sm text-slate-500">
                    Based on your latest resume analysis
                  </p>
                </div>
              </div>
            </div>

            {latestAnalysis && (
              <span className="rounded-full bg-slate-800 px-3 py-1 text-xs text-slate-400">
                Latest analysis
              </span>
            )}
          </div>

          {loading ? (
            <div className="mt-8 flex min-h-40 items-center justify-center">
              <Loader2
                size={30}
                className="animate-spin text-slate-500"
              />
            </div>
          ) : latestAnalysis ? (
            <div className="mt-8 grid gap-8 md:grid-cols-[180px_1fr] md:items-center">
              <div className="flex justify-center">
                <div className="relative flex h-40 w-40 items-center justify-center rounded-full border-[12px] border-slate-800">
                  <div
                    className="absolute inset-[-12px] rounded-full border-[12px] border-transparent border-t-blue-500 border-r-blue-500"
                    style={{
                      transform: `rotate(${Math.min(score, 100) * 3.6}deg)`,
                    }}
                  />

                  <div className="text-center">
                    <p className="text-4xl font-bold">
                      {Math.round(score)}
                    </p>

                    <p className="text-xs text-slate-500">
                      out of 100
                    </p>
                  </div>
                </div>
              </div>

              <div>
                <div className="flex items-center gap-2">
                  {score >= 60 ? (
                    <CheckCircle2
                      size={18}
                      className="text-emerald-400"
                    />
                  ) : (
                    <TrendingUp
                      size={18}
                      className="text-amber-400"
                    />
                  )}

                  <p className="font-semibold">
                    {scoreLabel}
                  </p>
                </div>

                <p className="mt-2 max-w-xl text-sm leading-6 text-slate-400">
                  {scoreMessage}
                </p>

                <div className="mt-6 grid gap-3 sm:grid-cols-2">
                  <div className="rounded-lg bg-slate-800/60 p-4">
                    <p className="text-xs text-slate-500">
                      Matched skills
                    </p>

                    <p className="mt-1 text-2xl font-bold text-emerald-400">
                      {matchedSkills.length}
                    </p>
                  </div>

                  <div className="rounded-lg bg-slate-800/60 p-4">
                    <p className="text-xs text-slate-500">
                      Missing skills
                    </p>

                    <p className="mt-1 text-2xl font-bold text-amber-400">
                      {missingSkills.length}
                    </p>
                  </div>
                </div>

                <button
                  type="button"
                  onClick={() => navigate("/analysis")}
                  className="mt-5 inline-flex items-center gap-2 text-sm font-medium text-blue-400 transition hover:text-blue-300"
                >
                  View detailed analysis
                  <ArrowUpRight size={16} />
                </button>
              </div>
            </div>
          ) : (
            <div className="mt-8 rounded-xl border border-dashed border-slate-700 p-8 text-center">
              <FileText
                className="mx-auto text-slate-600"
                size={34}
              />

              <h4 className="mt-4 font-medium">
                No resume analysis yet
              </h4>

              <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-slate-500">
                Upload your resume, add a target job, and run your
                first AI analysis to see your match score here.
              </p>

              <button
                type="button"
                onClick={() => navigate("/analysis")}
                className="mt-5 inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium transition hover:bg-blue-500"
              >
                Start Analysis
                <ArrowUpRight size={16} />
              </button>
            </div>
          )}
        </div>

        {/* Career readiness */}
        <div className="rounded-xl border border-slate-800 bg-slate-900/60 p-6">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600/10">
              <TrendingUp
                size={20}
                className="text-blue-400"
              />
            </div>

            <div>
              <h3 className="font-semibold">
                Career Readiness
              </h3>

              <p className="text-sm text-slate-500">
                Your current progress
              </p>
            </div>
          </div>

          <div className="mt-6 space-y-4">
            <div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">
                  Resume setup
                </span>

                <span className="text-slate-300">
                  {data.resumes.length > 0 ? "Ready" : "Pending"}
                </span>
              </div>

              <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-800">
                <div
                  className="h-full rounded-full bg-blue-500 transition-all"
                  style={{
                    width:
                      data.resumes.length > 0
                        ? "100%"
                        : "15%",
                  }}
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">
                  Target jobs
                </span>

                <span className="text-slate-300">
                  {data.jobs.length}
                </span>
              </div>

              <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-800">
                <div
                  className="h-full rounded-full bg-blue-500 transition-all"
                  style={{
                    width: `${Math.min(
                      data.jobs.length * 20,
                      100,
                    )}%`,
                  }}
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">
                  AI analyses
                </span>

                <span className="text-slate-300">
                  {data.analyses.length}
                </span>
              </div>

              <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-800">
                <div
                  className="h-full rounded-full bg-blue-500 transition-all"
                  style={{
                    width: `${Math.min(
                      data.analyses.length * 25,
                      100,
                    )}%`,
                  }}
                />
              </div>
            </div>
          </div>

          <div className="mt-6 rounded-lg bg-slate-800/50 p-4">
            <p className="text-xs text-slate-500">
              Next recommended action
            </p>

            <p className="mt-1 text-sm leading-5 text-slate-300">
              {data.resumes.length === 0
                ? "Upload your resume"
                : data.jobs.length === 0
                  ? "Add a target job"
                  : data.analyses.length === 0
                    ? "Run your first analysis"
                    : "Review your skill roadmap"}
            </p>
          </div>
        </div>
      </section>

      {/* Skill summary */}
      <section className="grid gap-6 lg:grid-cols-2">
        <div className="rounded-xl border border-slate-800 bg-slate-900/60 p-6">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="font-semibold">
                Skill Match Overview
              </h3>

              <p className="mt-1 text-sm text-slate-500">
                Skills identified in your latest analysis
              </p>
            </div>

            <Target
              size={20}
              className="text-blue-400"
            />
          </div>

          {!latestAnalysis ? (
            <div className="mt-6 rounded-lg bg-slate-800/40 p-5 text-center">
              <p className="text-sm text-slate-500">
                Complete an analysis to see your skill match.
              </p>
            </div>
          ) : (
            <div className="mt-6 grid gap-4 sm:grid-cols-2">
              <div className="rounded-lg border border-emerald-500/10 bg-emerald-500/5 p-4">
                <div className="flex items-center gap-2">
                  <CheckCircle2
                    size={17}
                    className="text-emerald-400"
                  />

                  <p className="text-sm font-medium text-emerald-300">
                    Matched Skills
                  </p>
                </div>

                {matchedSkills.length > 0 ? (
                  <div className="mt-4 flex flex-wrap gap-2">
                    {matchedSkills.slice(0, 6).map((skill) => (
                      <span
                        key={skill}
                        className="rounded-full bg-emerald-500/10 px-2.5 py-1 text-xs text-emerald-300"
                      >
                        {skill}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="mt-4 text-sm text-slate-500">
                    No matched skills recorded.
                  </p>
                )}
              </div>

              <div className="rounded-lg border border-amber-500/10 bg-amber-500/5 p-4">
                <div className="flex items-center gap-2">
                  <XCircle
                    size={17}
                    className="text-amber-400"
                  />

                  <p className="text-sm font-medium text-amber-300">
                    Missing Skills
                  </p>
                </div>

                {missingSkills.length > 0 ? (
                  <div className="mt-4 flex flex-wrap gap-2">
                    {missingSkills.slice(0, 6).map((skill) => (
                      <span
                        key={skill}
                        className="rounded-full bg-amber-500/10 px-2.5 py-1 text-xs text-amber-300"
                      >
                        {skill}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="mt-4 text-sm text-slate-500">
                    No missing skills recorded.
                  </p>
                )}
              </div>
            </div>
          )}
        </div>

        {/* Quick actions */}
        <div className="rounded-xl border border-slate-800 bg-slate-900/60 p-6">
          <div>
            <h3 className="font-semibold">
              Continue Your Career Journey
            </h3>

            <p className="mt-1 text-sm text-slate-500">
              Jump directly into your next step.
            </p>
          </div>

          <div className="mt-6 grid gap-3 sm:grid-cols-2">
            <button
              type="button"
              onClick={() => navigate("/resumes")}
              className="group flex items-center gap-3 rounded-lg border border-slate-800 bg-slate-800/40 p-4 text-left transition hover:border-slate-700 hover:bg-slate-800"
            >
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-600/10">
                <Upload
                  size={17}
                  className="text-blue-400"
                />
              </div>

              <div className="min-w-0">
                <p className="text-sm font-medium">
                  Manage Resumes
                </p>
                <p className="mt-1 text-xs text-slate-500">
                  Upload or update
                </p>
              </div>

              <ChevronRight
                size={16}
                className="ml-auto text-slate-600 transition group-hover:translate-x-1"
              />
            </button>

            <button
              type="button"
              onClick={() => navigate("/jobs")}
              className="group flex items-center gap-3 rounded-lg border border-slate-800 bg-slate-800/40 p-4 text-left transition hover:border-slate-700 hover:bg-slate-800"
            >
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-600/10">
                <BriefcaseBusiness
                  size={17}
                  className="text-blue-400"
                />
              </div>

              <div className="min-w-0">
                <p className="text-sm font-medium">
                  Track Jobs
                </p>
                <p className="mt-1 text-xs text-slate-500">
                  Manage target roles
                </p>
              </div>

              <ChevronRight
                size={16}
                className="ml-auto text-slate-600 transition group-hover:translate-x-1"
              />
            </button>

            <button
              type="button"
              onClick={() => navigate("/roadmap")}
              className="group flex items-center gap-3 rounded-lg border border-slate-800 bg-slate-800/40 p-4 text-left transition hover:border-slate-700 hover:bg-slate-800"
            >
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-600/10">
                <GraduationCap
                  size={17}
                  className="text-blue-400"
                />
              </div>

              <div className="min-w-0">
                <p className="text-sm font-medium">
                  Skill Roadmap
                </p>
                <p className="mt-1 text-xs text-slate-500">
                  Close skill gaps
                </p>
              </div>

              <ChevronRight
                size={16}
                className="ml-auto text-slate-600 transition group-hover:translate-x-1"
              />
            </button>

            <button
              type="button"
              onClick={() => navigate("/interview")}
              className="group flex items-center gap-3 rounded-lg border border-slate-800 bg-slate-800/40 p-4 text-left transition hover:border-slate-700 hover:bg-slate-800"
            >
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-600/10">
                <MessageSquareText
                  size={17}
                  className="text-blue-400"
                />
              </div>

              <div className="min-w-0">
                <p className="text-sm font-medium">
                  Interview Prep
                </p>
                <p className="mt-1 text-xs text-slate-500">
                  Practice questions
                </p>
              </div>

              <ChevronRight
                size={16}
                className="ml-auto text-slate-600 transition group-hover:translate-x-1"
              />
            </button>
          </div>
        </div>
      </section>

      {/* Latest analysis */}
      <section className="rounded-xl border border-slate-800 bg-slate-900/60 p-6">
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <h3 className="font-semibold">
              Latest Analysis
            </h3>

            <p className="mt-1 text-sm text-slate-500">
              Your most recent resume-job comparison
            </p>
          </div>

          {latestAnalysis && (
            <button
              type="button"
              onClick={() => navigate("/analysis")}
              className="inline-flex items-center gap-2 text-sm text-blue-400 transition hover:text-blue-300"
            >
              View all analyses
              <ArrowUpRight size={16} />
            </button>
          )}
        </div>

        {latestAnalysis ? (
          <div className="mt-6 grid gap-4 md:grid-cols-3">
            <div className="rounded-lg bg-slate-800/50 p-4">
              <p className="text-xs text-slate-500">
                Overall score
              </p>

              <p className="mt-2 text-2xl font-bold">
                {Math.round(score)}
                <span className="ml-1 text-sm font-normal text-slate-500">
                  / 100
                </span>
              </p>
            </div>

            <div className="rounded-lg bg-slate-800/50 p-4">
              <p className="text-xs text-slate-500">
                Matched skills
              </p>

              <p className="mt-2 text-2xl font-bold text-emerald-400">
                {matchedSkills.length}
              </p>
            </div>

            <div className="rounded-lg bg-slate-800/50 p-4">
              <p className="text-xs text-slate-500">
                Skills to improve
              </p>

              <p className="mt-2 text-2xl font-bold text-amber-400">
                {missingSkills.length}
              </p>
            </div>
          </div>
        ) : (
          <div className="mt-6 rounded-lg border border-dashed border-slate-700 p-8 text-center">
            <Sparkles
              className="mx-auto text-slate-600"
              size={30}
            />

            <p className="mt-3 text-sm text-slate-400">
              Your latest analysis will appear here.
            </p>

            <button
              type="button"
              onClick={() => navigate("/analysis")}
              className="mt-4 inline-flex items-center gap-2 rounded-lg border border-slate-700 px-4 py-2 text-sm text-slate-300 transition hover:border-slate-600 hover:bg-slate-800"
            >
              Create Analysis
              <ArrowUpRight size={16} />
            </button>
          </div>
        )}
      </section>
    </div>
  );
}

export default Dashboard;