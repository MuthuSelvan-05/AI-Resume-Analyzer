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

  const safeScore = Math.min(Math.max(score, 0), 100);

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
      delay: "animation-delay-100",
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
      delay: "animation-delay-200",
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
      delay: "animation-delay-300",
    },
  ];

  const quickActions = [
    {
      title: "Manage Resumes",
      description: "Upload or update",
      icon: Upload,
      path: "/resumes",
    },
    {
      title: "Track Jobs",
      description: "Manage target roles",
      icon: BriefcaseBusiness,
      path: "/jobs",
    },
    {
      title: "Skill Roadmap",
      description: "Close skill gaps",
      icon: GraduationCap,
      path: "/roadmap",
    },
    {
      title: "Interview Prep",
      description: "Practice questions",
      icon: MessageSquareText,
      path: "/interview",
    },
  ];

  return (
    <div className="page-enter animated-glow mx-auto max-w-7xl space-y-8">

      {/* Hero */}
      <section className="relative overflow-hidden rounded-3xl border border-slate-800/80 bg-slate-900/50 p-6 shadow-2xl shadow-blue-950/10 backdrop-blur-xl md:p-8">
        <div className="absolute -right-24 -top-24 h-64 w-64 rounded-full bg-blue-500/10 blur-3xl" />
        <div className="absolute -bottom-32 left-1/3 h-72 w-72 rounded-full bg-violet-500/10 blur-3xl" />

        <div className="relative flex flex-col justify-between gap-7 md:flex-row md:items-end">
          <div className="max-w-3xl">
            <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-blue-500/20 bg-blue-500/10 px-3 py-1.5 text-sm font-medium text-blue-300">
              <Sparkles size={15} className="soft-pulse" />
              Career Intelligence
            </div>

            <h2 className="text-3xl font-bold tracking-tight md:text-4xl">
              Welcome back
              {user?.name ? `, ${user.name}` : ""}
              <span className="gradient-text">.</span>
            </h2>

            <p className="mt-3 max-w-2xl text-sm leading-7 text-slate-400 md:text-base">
              Track your resume readiness, analyze job matches,
              close skill gaps, and prepare for your next interview.
            </p>
          </div>

          <button
            type="button"
            onClick={() => navigate("/analysis")}
            className="glow-button group inline-flex items-center justify-center gap-2 rounded-xl bg-blue-600 px-5 py-3 text-sm font-semibold text-white"
          >
            <Sparkles size={17} />
            Analyze Resume
            <ArrowUpRight
              size={17}
              className="transition-transform duration-200 group-hover:-translate-y-0.5 group-hover:translate-x-0.5"
            />
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
              className={`card-hover group relative overflow-hidden rounded-2xl border border-slate-800/80 bg-slate-900/60 p-5 text-left backdrop-blur-xl ${stat.delay}`}
            >
              <div className="absolute -right-10 -top-10 h-28 w-28 rounded-full bg-blue-500/5 blur-2xl transition-all duration-500 group-hover:bg-blue-500/10" />

              <div className="relative flex items-center justify-between">
                <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-blue-500/10 ring-1 ring-blue-500/10 transition duration-300 group-hover:scale-110 group-hover:bg-blue-500/15">
                  <Icon
                    size={20}
                    className="text-blue-400"
                  />
                </div>

                <ChevronRight
                  size={18}
                  className="text-slate-600 transition duration-300 group-hover:translate-x-1 group-hover:text-blue-400"
                />
              </div>

              <div className="relative mt-6 flex items-center gap-2">
                {loading ? (
                  <Loader2
                    size={26}
                    className="animate-spin text-slate-500"
                  />
                ) : (
                  <p className="text-4xl font-bold tracking-tight">
                    {stat.value}
                  </p>
                )}
              </div>

              <p className="mt-1 text-sm font-semibold text-slate-200">
                {stat.label}
              </p>

              <p className="mt-1 text-xs text-slate-500">
                {stat.description}
              </p>
            </button>
          );
        })}
      </section>

      {/* Main intelligence */}
      <section className="grid gap-6 lg:grid-cols-3">

        {/* Resume score */}
        <div className="card-hover rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl lg:col-span-2">
          <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-start">
            <div className="flex items-center gap-3">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-blue-500/10 ring-1 ring-blue-500/10">
                <BarChart3
                  size={21}
                  className="text-blue-400"
                />
              </div>

              <div>
                <h3 className="font-semibold text-slate-100">
                  Resume Match Score
                </h3>

                <p className="mt-1 text-sm text-slate-500">
                  Based on your latest resume analysis
                </p>
              </div>
            </div>

            {latestAnalysis && (
              <span className="rounded-full border border-blue-500/10 bg-blue-500/5 px-3 py-1 text-xs text-blue-300">
                Latest analysis
              </span>
            )}
          </div>

          {loading ? (
            <div className="mt-8 flex min-h-40 items-center justify-center">
              <Loader2
                size={32}
                className="animate-spin text-blue-400"
              />
            </div>
          ) : latestAnalysis ? (
            <div className="mt-8 grid gap-8 md:grid-cols-[190px_1fr] md:items-center">

              {/* Score */}
              <div className="flex justify-center">
                <div className="score-ring relative flex h-44 w-44 items-center justify-center rounded-full">
                  <div
                    className="absolute inset-0 rounded-full"
                    style={{
                      background: `conic-gradient(
                        #3b82f6 ${safeScore * 3.6}deg,
                        rgba(30, 41, 59, 0.8) ${safeScore * 3.6}deg
                      )`,
                    }}
                  />

                  <div className="absolute inset-[9px] rounded-full bg-slate-950" />

                  <div className="relative text-center">
                    <p className="text-5xl font-bold tracking-tight">
                      {Math.round(score)}
                    </p>

                    <p className="mt-1 text-xs text-slate-500">
                      out of 100
                    </p>
                  </div>
                </div>
              </div>

              {/* Score details */}
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

                  <p className="font-semibold text-slate-100">
                    {scoreLabel}
                  </p>
                </div>

                <p className="mt-2 max-w-xl text-sm leading-6 text-slate-400">
                  {scoreMessage}
                </p>

                <div className="mt-6 grid gap-3 sm:grid-cols-2">
                  <div className="card-hover rounded-xl border border-emerald-500/10 bg-emerald-500/5 p-4">
                    <p className="text-xs text-slate-500">
                      Matched skills
                    </p>

                    <p className="mt-1 text-2xl font-bold text-emerald-400">
                      {matchedSkills.length}
                    </p>
                  </div>

                  <div className="card-hover rounded-xl border border-amber-500/10 bg-amber-500/5 p-4">
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
                  className="mt-5 inline-flex items-center gap-2 text-sm font-medium text-blue-400 hover:text-blue-300"
                >
                  View detailed analysis
                  <ArrowUpRight size={16} />
                </button>
              </div>
            </div>
          ) : (
            <div className="mt-8 rounded-2xl border border-dashed border-slate-700 bg-slate-950/30 p-8 text-center">
              <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-500/10">
                <FileText
                  className="text-blue-400"
                  size={28}
                />
              </div>

              <h4 className="mt-4 font-semibold">
                No resume analysis yet
              </h4>

              <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-slate-500">
                Upload your resume, add a target job, and run
                your first AI analysis to see your match score here.
              </p>

              <button
                type="button"
                onClick={() => navigate("/analysis")}
                className="glow-button mt-5 inline-flex items-center gap-2 rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-medium text-white"
              >
                Start Analysis
                <ArrowUpRight size={16} />
              </button>
            </div>
          )}
        </div>

        {/* Career readiness */}
        <div className="card-hover rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-violet-500/10">
              <TrendingUp
                size={20}
                className="text-violet-400"
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

          <div className="mt-7 space-y-5">

            {/* Resume */}
            <div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">
                  Resume setup
                </span>

                <span className="text-slate-300">
                  {data.resumes.length > 0
                    ? "Ready"
                    : "Pending"}
                </span>
              </div>

              <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-800">
                <div
                  className="h-full rounded-full bg-gradient-to-r from-blue-500 to-cyan-400 transition-all duration-700"
                  style={{
                    width:
                      data.resumes.length > 0
                        ? "100%"
                        : "15%",
                  }}
                />
              </div>
            </div>

            {/* Jobs */}
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
                  className="h-full rounded-full bg-gradient-to-r from-violet-500 to-fuchsia-400 transition-all duration-700"
                  style={{
                    width: `${Math.min(
                      data.jobs.length * 20,
                      100,
                    )}%`,
                  }}
                />
              </div>
            </div>

            {/* Analyses */}
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
                  className="h-full rounded-full bg-gradient-to-r from-cyan-500 to-blue-500 transition-all duration-700"
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

          <div className="mt-7 rounded-xl border border-slate-800 bg-slate-950/40 p-4">
            <p className="text-xs font-medium uppercase tracking-wider text-slate-500">
              Next recommended action
            </p>

            <p className="mt-2 text-sm leading-5 text-slate-300">
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

      {/* Skills + quick actions */}
      <section className="grid gap-6 lg:grid-cols-2">

        {/* Skill match */}
        <div className="card-hover rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="font-semibold">
                Skill Match Overview
              </h3>

              <p className="mt-1 text-sm text-slate-500">
                Skills identified in your latest analysis
              </p>
            </div>

            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-500/10">
              <Target
                size={20}
                className="text-blue-400"
              />
            </div>
          </div>

          {!latestAnalysis ? (
            <div className="mt-6 rounded-xl border border-dashed border-slate-800 bg-slate-950/30 p-6 text-center">
              <Target
                size={28}
                className="mx-auto text-slate-600"
              />

              <p className="mt-3 text-sm text-slate-500">
                Complete an analysis to see your skill match.
              </p>
            </div>
          ) : (
            <div className="mt-6 grid gap-4 sm:grid-cols-2">

              {/* Matched */}
              <div className="card-hover rounded-xl border border-emerald-500/10 bg-emerald-500/5 p-4">
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
                        className="rounded-full border border-emerald-500/10 bg-emerald-500/10 px-2.5 py-1 text-xs text-emerald-300 transition hover:scale-105"
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

              {/* Missing */}
              <div className="card-hover rounded-xl border border-amber-500/10 bg-amber-500/5 p-4">
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
                        className="rounded-full border border-amber-500/10 bg-amber-500/10 px-2.5 py-1 text-xs text-amber-300 transition hover:scale-105"
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
        <div className="card-hover rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl">
          <div>
            <h3 className="font-semibold">
              Continue Your Career Journey
            </h3>

            <p className="mt-1 text-sm text-slate-500">
              Jump directly into your next step.
            </p>
          </div>

          <div className="mt-6 grid gap-3 sm:grid-cols-2">
            {quickActions.map((action) => {
              const Icon = action.icon;

              return (
                <button
                  key={action.title}
                  type="button"
                  onClick={() => navigate(action.path)}
                  className="group flex items-center gap-3 rounded-xl border border-slate-800 bg-slate-950/30 p-4 text-left transition duration-300 hover:-translate-y-1 hover:border-blue-500/20 hover:bg-slate-800/60 hover:shadow-lg hover:shadow-blue-950/20"
                >
                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-blue-500/10 transition duration-300 group-hover:scale-110 group-hover:bg-blue-500/15">
                    <Icon
                      size={17}
                      className="text-blue-400"
                    />
                  </div>

                  <div className="min-w-0">
                    <p className="text-sm font-medium text-slate-200">
                      {action.title}
                    </p>

                    <p className="mt-1 text-xs text-slate-500">
                      {action.description}
                    </p>
                  </div>

                  <ChevronRight
                    size={16}
                    className="ml-auto text-slate-600 transition duration-300 group-hover:translate-x-1 group-hover:text-blue-400"
                  />
                </button>
              );
            })}
          </div>
        </div>
      </section>

      {/* Latest analysis */}
      <section className="card-hover rounded-2xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur-xl">
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <div className="flex items-center gap-2">
              <Sparkles
                size={17}
                className="text-blue-400"
              />

              <h3 className="font-semibold">
                Latest Analysis
              </h3>
            </div>

            <p className="mt-1 text-sm text-slate-500">
              Your most recent resume-job comparison
            </p>
          </div>

          {latestAnalysis && (
            <button
              type="button"
              onClick={() => navigate("/analysis")}
              className="inline-flex items-center gap-2 text-sm text-blue-400 hover:text-blue-300"
            >
              View all analyses
              <ArrowUpRight size={16} />
            </button>
          )}
        </div>

        {latestAnalysis ? (
          <div className="mt-6 grid gap-4 md:grid-cols-3">

            <div className="card-hover rounded-xl border border-slate-800 bg-slate-950/30 p-5">
              <p className="text-xs uppercase tracking-wider text-slate-500">
                Overall score
              </p>

              <p className="mt-2 text-3xl font-bold">
                {Math.round(score)}
                <span className="ml-1 text-sm font-normal text-slate-500">
                  / 100
                </span>
              </p>
            </div>

            <div className="card-hover rounded-xl border border-emerald-500/10 bg-emerald-500/5 p-5">
              <p className="text-xs uppercase tracking-wider text-slate-500">
                Matched skills
              </p>

              <p className="mt-2 text-3xl font-bold text-emerald-400">
                {matchedSkills.length}
              </p>
            </div>

            <div className="card-hover rounded-xl border border-amber-500/10 bg-amber-500/5 p-5">
              <p className="text-xs uppercase tracking-wider text-slate-500">
                Skills to improve
              </p>

              <p className="mt-2 text-3xl font-bold text-amber-400">
                {missingSkills.length}
              </p>
            </div>

          </div>
        ) : (
          <div className="mt-6 rounded-xl border border-dashed border-slate-700 bg-slate-950/30 p-8 text-center">
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
              className="mt-4 inline-flex items-center gap-2 rounded-xl border border-slate-700 px-4 py-2 text-sm text-slate-300 hover:border-blue-500/30 hover:bg-slate-800 hover:text-white"
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