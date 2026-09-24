import { useEffect, useState } from "react";
import {
  BookOpen,
  BriefcaseBusiness,
  CheckCircle2,
  ChevronDown,
  Circle,
  ExternalLink,
  FileText,
  GraduationCap,
  Loader2,
  PlayCircle,
  RefreshCw,
  Target,
  Wrench,
} from "lucide-react";

import roadmapService from "../services/roadmapService";
import learningResourceService from "../services/learningResourceService";

function Roadmap() {
  const [roadmaps, setRoadmaps] = useState([]);
  const [selectedRoadmap, setSelectedRoadmap] = useState(null);
  const [steps, setSteps] = useState([]);
  const [resources, setResources] = useState([]);

  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [loadingSteps, setLoadingSteps] = useState(false);
  const [loadingResources, setLoadingResources] = useState(false);
  const [updatingStep, setUpdatingStep] = useState(null);

  const [error, setError] = useState("");
  const [resourceError, setResourceError] = useState("");

  useEffect(() => {
    loadRoadmaps();
  }, []);

  const loadRoadmaps = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await roadmapService.getRoadmaps();

      setRoadmaps(Array.isArray(data) ? data : []);

      if (Array.isArray(data) && data.length > 0) {
        await loadSteps(data[0]);
      }
    } catch (requestError) {
      console.error("Failed to load roadmaps:", requestError);

      setError(
        requestError.response?.data?.message ||
          "Unable to load your learning roadmaps."
      );
    } finally {
      setLoading(false);
    }
  };

  const loadSteps = async (roadmap) => {
    try {
      setSelectedRoadmap(roadmap);
      setLoadingSteps(true);
      setError("");

      const data = await roadmapService.getRoadmapSteps(roadmap.id);

      setSteps(Array.isArray(data) ? data : []);

      await loadResources(roadmap);
    } catch (requestError) {
      console.error("Failed to load roadmap steps:", requestError);

      setError(
        requestError.response?.data?.message ||
          "Unable to load roadmap steps."
      );

      setSteps([]);
    } finally {
      setLoadingSteps(false);
    }
  };

  const loadResources = async (roadmap) => {
    try {
      setLoadingResources(true);
      setResourceError("");

      const skillId =
        roadmap?.skillId ||
        roadmap?.skill?.id;

      if (!skillId) {
        setResources([]);
        return;
      }

      const data =
        await learningResourceService.getResourcesBySkill(skillId);

      setResources(Array.isArray(data) ? data : []);
    } catch (requestError) {
      console.error("Failed to load learning resources:", requestError);

      setResources([]);

      setResourceError(
        requestError.response?.data?.message ||
          "Unable to load learning resources."
      );
    } finally {
      setLoadingResources(false);
    }
  };

  const handleGenerate = async () => {
    try {
      setGenerating(true);
      setError("");

      await roadmapService.generateRoadmaps();

      await loadRoadmaps();
    } catch (requestError) {
      console.error("Failed to generate roadmaps:", requestError);

      setError(
        requestError.response?.data?.message ||
          "Unable to generate your roadmap."
      );
    } finally {
      setGenerating(false);
    }
  };

  const getStepStatus = (step) => {
    const status = String(step.status || "").toUpperCase();

    if (
      status === "COMPLETED" ||
      status === "DONE" ||
      step.completed === true
    ) {
      return "COMPLETED";
    }

    if (status === "IN_PROGRESS") {
      return "IN_PROGRESS";
    }

    return "NOT_STARTED";
  };

  const handleStepUpdate = async (step) => {
    if (!selectedRoadmap || updatingStep === step.id) {
      return;
    }

    const currentStatus = getStepStatus(step);

    const nextStatus =
      currentStatus === "COMPLETED"
        ? "NOT_STARTED"
        : "COMPLETED";

    try {
      setUpdatingStep(step.id);
      setError("");

      const updatedStep = await roadmapService.updateStep(
        selectedRoadmap.id,
        step.id,
        {
          status: nextStatus,
        }
      );

      setSteps((current) =>
        current.map((item) =>
          item.id === step.id
            ? { ...item, ...updatedStep }
            : item
        )
      );
    } catch (requestError) {
      console.error(
        "Failed to update roadmap step:",
        requestError
      );

      setError(
        requestError.response?.data?.message ||
          "Unable to update the roadmap step."
      );
    } finally {
      setUpdatingStep(null);
    }
  };

  const getSkillName = (roadmap) => {
    return (
      roadmap.skillName ||
      roadmap.skill?.name ||
      roadmap.name ||
      "Learning Roadmap"
    );
  };

  const getSkillCategory = (roadmap) => {
    return (
      roadmap.skillCategory ||
      roadmap.skill?.category ||
      "Skill Development"
    );
  };

  const getStepTitle = (step) => {
    return (
      step.title ||
      step.name ||
      `Step ${step.stepOrder || ""}`
    );
  };

  const getStepDescription = (step) => {
    return (
      step.description ||
      step.details ||
      "Complete this learning step to continue your roadmap."
    );
  };

  const getResourceTypeLabel = (resourceType) => {
    const labels = {
      DOCUMENTATION: "Documentation",
      TUTORIAL: "Tutorial",
      VIDEO: "Video",
      COURSE: "Course",
      PRACTICE: "Practice",
      PROJECT: "Project",
      INTERVIEW: "Interview",
    };

    return (
      labels[String(resourceType || "").toUpperCase()] ||
      "Learning Resource"
    );
  };

  const getResourceIcon = (resourceType) => {
    const type = String(resourceType || "").toUpperCase();

    switch (type) {
      case "VIDEO":
        return PlayCircle;

      case "PRACTICE":
      case "PROJECT":
        return Wrench;

      case "COURSE":
        return GraduationCap;

      case "INTERVIEW":
        return BriefcaseBusiness;

      case "DOCUMENTATION":
      case "TUTORIAL":
      default:
        return FileText;
    }
  };

  const getResourceLevelLabel = (level) => {
    const value = String(level || "")
      .toLowerCase();

    if (!value) {
      return "All Levels";
    }

    return (
      value.charAt(0).toUpperCase() +
      value.slice(1)
    );
  };

  const completedCount = steps.filter(
    (step) => getStepStatus(step) === "COMPLETED"
  ).length;

  const progress =
    steps.length > 0
      ? Math.round(
          (completedCount / steps.length) * 100
        )
      : 0;

  if (loading) {
    return (
      <div className="flex min-h-[500px] items-center justify-center">
        <Loader2
          className="animate-spin text-blue-400"
          size={32}
        />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-7xl space-y-8">
      {/* Header */}
      <section>
        <p className="text-sm font-medium text-blue-400">
          Learning Center
        </p>

        <div className="mt-2 flex flex-col justify-between gap-4 md:flex-row md:items-end">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">
              Skill Roadmap
            </h2>

            <p className="mt-2 max-w-2xl text-slate-400">
              Turn your skill gaps into a structured learning
              plan and track your progress step by step.
            </p>
          </div>

          <button
            onClick={handleGenerate}
            disabled={generating}
            className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {generating ? (
              <Loader2
                size={17}
                className="animate-spin"
              />
            ) : (
              <RefreshCw size={17} />
            )}

            {generating
              ? "Generating..."
              : "Generate Roadmap"}
          </button>
        </div>
      </section>

      {/* Error */}
      {error && (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
          {error}
        </div>
      )}

      {/* Empty state */}
      {roadmaps.length === 0 ? (
        <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-10 text-center">
          <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-600/10 text-blue-400">
            <GraduationCap size={28} />
          </div>

          <h3 className="mt-5 text-xl font-semibold">
            No learning roadmaps yet
          </h3>

          <p className="mx-auto mt-2 max-w-lg text-sm text-slate-400">
            Generate a roadmap to create structured learning
            steps for your skill gaps.
          </p>

          <button
            onClick={handleGenerate}
            disabled={generating}
            className="mt-6 inline-flex items-center gap-2 rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-medium hover:bg-blue-500 disabled:opacity-60"
          >
            <Target size={17} />
            Generate My Roadmap
          </button>
        </section>
      ) : (
        <>
          {/* Roadmap selector */}
          <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
            <div className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
              <div className="flex items-center gap-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-600/10 text-blue-400">
                  <BookOpen size={24} />
                </div>

                <div>
                  <p className="text-xs uppercase tracking-wide text-slate-500">
                    Current roadmap
                  </p>

                  <h3 className="mt-1 text-xl font-semibold">
                    {selectedRoadmap
                      ? getSkillName(selectedRoadmap)
                      : "Select a roadmap"}
                  </h3>

                  {selectedRoadmap && (
                    <p className="mt-1 text-sm text-slate-400">
                      {getSkillCategory(selectedRoadmap)}
                    </p>
                  )}
                </div>
              </div>

              <div className="relative min-w-[230px]">
                <select
                  value={selectedRoadmap?.id || ""}
                  onChange={(event) => {
                    const roadmap = roadmaps.find(
                      (item) =>
                        String(item.id) ===
                        event.target.value
                    );

                    if (roadmap) {
                      loadSteps(roadmap);
                    }
                  }}
                  className="w-full appearance-none rounded-lg border border-slate-700 bg-slate-950 px-4 py-2.5 pr-10 text-sm text-slate-200 outline-none transition focus:border-blue-500"
                >
                  {roadmaps.map((roadmap) => (
                    <option
                      key={roadmap.id}
                      value={roadmap.id}
                    >
                      {getSkillName(roadmap)}
                    </option>
                  ))}
                </select>

                <ChevronDown
                  size={17}
                  className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-slate-500"
                />
              </div>
            </div>
          </section>

          {/* Progress */}
          <section className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6">
            <div className="flex items-end justify-between">
              <div>
                <p className="text-sm text-slate-400">
                  Roadmap Progress
                </p>

                <p className="mt-1 text-3xl font-bold">
                  {progress}%
                </p>
              </div>

              <p className="text-sm text-slate-500">
                {completedCount} of {steps.length} steps
                completed
              </p>
            </div>

            <div className="mt-5 h-2 overflow-hidden rounded-full bg-slate-800">
              <div
                className="h-full rounded-full bg-blue-500 transition-all duration-500"
                style={{ width: `${progress}%` }}
              />
            </div>
          </section>

          {/* Learning Path */}
          <section>
            <div className="mb-4">
              <h3 className="text-lg font-semibold">
                Learning Path
              </h3>

              <p className="mt-1 text-sm text-slate-500">
                Complete each step to progress through your
                roadmap.
              </p>
            </div>

            {loadingSteps ? (
              <div className="flex min-h-[250px] items-center justify-center rounded-2xl border border-slate-800 bg-slate-900/70">
                <Loader2
                  className="animate-spin text-blue-400"
                  size={28}
                />
              </div>
            ) : steps.length === 0 ? (
              <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-8 text-center text-sm text-slate-400">
                No roadmap steps are available.
              </div>
            ) : (
              <div className="space-y-4">
                {steps.map((step, index) => {
                  const status = getStepStatus(step);
                  const completed =
                    status === "COMPLETED";
                  const inProgress =
                    status === "IN_PROGRESS";

                  return (
                    <div
                      key={step.id}
                      className={`rounded-2xl border p-5 transition ${
                        completed
                          ? "border-emerald-500/20 bg-emerald-500/5"
                          : "border-slate-800 bg-slate-900/70"
                      }`}
                    >
                      <div className="flex gap-4">
                        <div className="flex flex-col items-center">
                          <button
                            onClick={() =>
                              handleStepUpdate(step)
                            }
                            disabled={
                              updatingStep === step.id
                            }
                            className="shrink-0 text-slate-500 transition hover:text-blue-400 disabled:opacity-50"
                            title={
                              completed
                                ? "Mark as incomplete"
                                : "Mark as completed"
                            }
                          >
                            {updatingStep === step.id ? (
                              <Loader2
                                size={25}
                                className="animate-spin"
                              />
                            ) : completed ? (
                              <CheckCircle2
                                size={25}
                                className="text-emerald-400"
                              />
                            ) : inProgress ? (
                              <Target
                                size={25}
                                className="text-blue-400"
                              />
                            ) : (
                              <Circle size={25} />
                            )}
                          </button>

                          {index < steps.length - 1 && (
                            <div className="mt-2 h-full min-h-8 w-px bg-slate-800" />
                          )}
                        </div>

                        <div className="min-w-0 flex-1">
                          <div className="flex flex-col justify-between gap-2 sm:flex-row">
                            <div>
                              <p className="text-xs font-medium uppercase tracking-wide text-slate-500">
                                Step{" "}
                                {step.stepOrder ||
                                  index + 1}
                              </p>

                              <h4
                                className={`mt-1 text-base font-semibold ${
                                  completed
                                    ? "text-emerald-300"
                                    : "text-slate-100"
                                }`}
                              >
                                {getStepTitle(step)}
                              </h4>
                            </div>

                            <span
                              className={`h-fit rounded-full px-2.5 py-1 text-xs font-medium ${
                                completed
                                  ? "bg-emerald-500/10 text-emerald-400"
                                  : inProgress
                                  ? "bg-blue-500/10 text-blue-400"
                                  : "bg-slate-800 text-slate-400"
                              }`}
                            >
                              {completed
                                ? "Completed"
                                : inProgress
                                ? "In Progress"
                                : "Not Started"}
                            </span>
                          </div>

                          <p className="mt-3 text-sm leading-6 text-slate-400">
                            {getStepDescription(step)}
                          </p>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </section>

          {/* Learning Resources */}
          <section>
            <div className="mb-4">
              <h3 className="text-lg font-semibold">
                Recommended Learning Resources
              </h3>

              <p className="mt-1 text-sm text-slate-500">
                Curated resources to help you build your{" "}
                {selectedRoadmap
                  ? getSkillName(selectedRoadmap)
                  : "skill"}{" "}
                knowledge.
              </p>
            </div>

            {loadingResources ? (
              <div className="flex min-h-[180px] items-center justify-center rounded-2xl border border-slate-800 bg-slate-900/70">
                <Loader2
                  className="animate-spin text-blue-400"
                  size={28}
                />
              </div>
            ) : resourceError ? (
              <div className="rounded-2xl border border-red-500/20 bg-red-500/5 p-6 text-sm text-red-300">
                {resourceError}
              </div>
            ) : resources.length === 0 ? (
              <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-8 text-center">
                <BookOpen
                  size={28}
                  className="mx-auto text-slate-600"
                />

                <p className="mt-3 text-sm text-slate-400">
                  No learning resources are available for
                  this skill yet.
                </p>
              </div>
            ) : (
              <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                {resources.map((resource) => {
                  const ResourceIcon =
                    getResourceIcon(
                      resource.resourceType
                    );

                  return (
                    <div
                      key={resource.id}
                      className="group rounded-2xl border border-slate-800 bg-slate-900/70 p-5 transition hover:-translate-y-0.5 hover:border-slate-700 hover:bg-slate-900"
                    >
                      <div className="flex items-start justify-between gap-4">
                        <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-blue-600/10 text-blue-400">
                          <ResourceIcon size={21} />
                        </div>

                        <span className="rounded-full bg-slate-800 px-2.5 py-1 text-xs font-medium text-slate-400">
                          {getResourceLevelLabel(
                            resource.level
                          )}
                        </span>
                      </div>

                      <h4 className="mt-4 line-clamp-2 text-base font-semibold text-slate-100">
                        {resource.title}
                      </h4>

                      {resource.description && (
                        <p className="mt-2 line-clamp-3 text-sm leading-6 text-slate-400">
                          {resource.description}
                        </p>
                      )}

                      <div className="mt-4 flex items-center justify-between gap-3">
                        <span className="text-xs font-medium text-blue-400">
                          {getResourceTypeLabel(
                            resource.resourceType
                          )}
                        </span>

                        <a
                          href={resource.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-1.5 rounded-lg border border-slate-700 px-3 py-2 text-xs font-medium text-slate-200 transition hover:border-blue-500 hover:bg-blue-500/10 hover:text-blue-300"
                        >
                          Open Resource
                          <ExternalLink size={14} />
                        </a>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
}

export default Roadmap;