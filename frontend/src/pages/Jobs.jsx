import { useEffect, useState } from "react";
import {
  BriefcaseBusiness,
  Building2,
  MapPin,
  Plus,
  Trash2,
  Pencil,
  X,
  Loader2,
  Settings2,
  CheckCircle2,
  Star,
} from "lucide-react";

import jobService from "../services/jobService";
import jobSkillService from "../services/jobSkillService";
import skillService from "../services/skillService";

function Jobs() {
  const [jobs, setJobs] = useState([]);
  const [skills, setSkills] = useState([]);

  const [jobSkills, setJobSkills] = useState({});

  const [loading, setLoading] = useState(true);
  const [skillsLoading, setSkillsLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [addingSkill, setAddingSkill] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editingJobId, setEditingJobId] = useState(null);

  const [expandedSkillJobId, setExpandedSkillJobId] = useState(null);

  const [formData, setFormData] = useState({
    title: "",
    company: "",
    location: "",
    description: "",
  });

  const [skillForm, setSkillForm] = useState({
    skillId: "",
    required: true,
    importance: 3,
  });

  useEffect(() => {
    loadJobs();
    loadSkills();
  }, []);

  const loadJobs = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await jobService.getJobs();
      setJobs(data);

      await loadAllJobSkills(data);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to load your jobs."
      );
    } finally {
      setLoading(false);
    }
  };

  const loadSkills = async () => {
    try {
      const data = await skillService.getSkills();
      setSkills(data);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to load available skills."
      );
    }
  };

  const loadAllJobSkills = async (jobList) => {
    if (!jobList || jobList.length === 0) {
      setJobSkills({});
      return;
    }

    try {
      setSkillsLoading(true);

      const results = await Promise.all(
        jobList.map(async (job) => {
          const data = await jobSkillService.getJobSkills(job.id);

          return {
            jobId: job.id,
            skills: data,
          };
        })
      );

      const mappedSkills = {};

      results.forEach((result) => {
        mappedSkills[result.jobId] = result.skills;
      });

      setJobSkills(mappedSkills);
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to load job skills."
      );
    } finally {
      setSkillsLoading(false);
    }
  };

  const loadJobSkills = async (jobId) => {
    try {
      const data = await jobSkillService.getJobSkills(jobId);

      setJobSkills((current) => ({
        ...current,
        [jobId]: data,
      }));
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to load job skills."
      );
    }
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const resetForm = () => {
    setFormData({
      title: "",
      company: "",
      location: "",
      description: "",
    });

    setEditingJobId(null);
    setShowForm(false);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");
    setSubmitting(true);

    try {
      if (editingJobId) {
        await jobService.updateJob(editingJobId, formData);
        setSuccess("Job updated successfully.");
      } else {
        await jobService.createJob(formData);
        setSuccess("Job created successfully.");
      }

      resetForm();
      await loadJobs();
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to save the job."
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (job) => {
    setFormData({
      title: job.title || "",
      company: job.company || "",
      location: job.location || "",
      description: job.description || "",
    });

    setEditingJobId(job.id);
    setShowForm(true);
    setError("");
    setSuccess("");
  };

  const handleDelete = async (jobId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this job?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccess("");

      await jobService.deleteJob(jobId);

      setJobs((current) =>
        current.filter((job) => job.id !== jobId)
      );

      setJobSkills((current) => {
        const updated = { ...current };
        delete updated[jobId];
        return updated;
      });

      setSuccess("Job deleted successfully.");
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to delete the job."
      );
    }
  };

  const handleSkillFormChange = (event) => {
    const { name, value } = event.target;

    setSkillForm((current) => ({
      ...current,
      [name]:
        name === "importance"
          ? Number(value)
          : value,
    }));
  };

  const handleRequiredChange = (event) => {
    setSkillForm((current) => ({
      ...current,
      required: event.target.value === "true",
    }));
  };

  const resetSkillForm = () => {
    setSkillForm({
      skillId: "",
      required: true,
      importance: 3,
    });
  };

  const handleAddSkill = async (jobId) => {
    if (!skillForm.skillId) {
      setError("Please select a skill.");
      return;
    }

    try {
      setAddingSkill(true);
      setError("");
      setSuccess("");

      await jobSkillService.addJobSkill(jobId, {
        skillId: Number(skillForm.skillId),
        required: skillForm.required,
        importance: Number(skillForm.importance),
      });

      await loadJobSkills(jobId);

      resetSkillForm();

      setSuccess("Skill added to the job successfully.");
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to add this skill."
      );
    } finally {
      setAddingSkill(false);
    }
  };

  const handleRemoveSkill = async (jobId, skillId) => {
    const confirmed = window.confirm(
      "Remove this skill from the job?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccess("");

      await jobSkillService.removeJobSkill(jobId, skillId);

      await loadJobSkills(jobId);

      setSuccess("Skill removed successfully.");
    } catch (requestError) {
      setError(
        requestError.response?.data?.message ||
          "Unable to remove this skill."
      );
    }
  };

  const toggleSkillPanel = (jobId) => {
    setError("");
    setSuccess("");

    if (expandedSkillJobId === jobId) {
      setExpandedSkillJobId(null);
      return;
    }

    setExpandedSkillJobId(jobId);

    if (!jobSkills[jobId]) {
      loadJobSkills(jobId);
    }
  };

  const getJobSkills = (jobId) => {
    return jobSkills[jobId] || [];
  };

  const getRequiredSkills = (jobId) => {
    return getJobSkills(jobId).filter(
      (skill) => skill.required
    );
  };

  const getPreferredSkills = (jobId) => {
    return getJobSkills(jobId).filter(
      (skill) => !skill.required
    );
  };

  const isSkillAlreadyAdded = (jobId, skillId) => {
    return getJobSkills(jobId).some(
      (skill) => skill.skillId === Number(skillId)
    );
  };

  return (
    <div className="space-y-8">

      {/* Page Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-sm font-medium text-blue-400">
            Job Management
          </p>

          <h1 className="mt-2 text-3xl font-bold tracking-tight text-white">
            Your jobs
          </h1>

          <p className="mt-2 max-w-2xl text-sm text-slate-400">
            Save job descriptions and manage the opportunities you want
            to analyze against your resumes.
          </p>
        </div>

        <button
          type="button"
          onClick={() => {
            setEditingJobId(null);

            setFormData({
              title: "",
              company: "",
              location: "",
              description: "",
            });

            setShowForm(true);
            setError("");
            setSuccess("");
          }}
          className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-500"
        >
          <Plus size={18} />
          New Job
        </button>
      </div>

      {/* Messages */}
      {error && (
        <div className="rounded-lg border border-red-500/20 bg-red-500/10 px-4 py-3 text-sm text-red-400">
          {error}
        </div>
      )}

      {success && (
        <div className="rounded-lg border border-emerald-500/20 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-400">
          {success}
        </div>
      )}

      {/* Job Form */}
      {showForm && (
        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
          <div className="mb-6 flex items-center justify-between">
            <div>
              <h2 className="text-lg font-semibold text-white">
                {editingJobId ? "Edit job" : "Add a new job"}
              </h2>

              <p className="mt-1 text-sm text-slate-400">
                Add the job details that will be used for resume matching.
              </p>
            </div>

            <button
              type="button"
              onClick={resetForm}
              className="rounded-lg p-2 text-slate-400 transition hover:bg-slate-800 hover:text-white"
            >
              <X size={20} />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">

            <div className="grid gap-5 md:grid-cols-2">

              <div>
                <label className="mb-2 block text-sm font-medium text-slate-300">
                  Job title
                </label>

                <input
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  placeholder="e.g. Java Backend Developer"
                  required
                  className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2.5 text-sm text-white outline-none transition placeholder:text-slate-600 focus:border-blue-500"
                />
              </div>

              <div>
                <label className="mb-2 block text-sm font-medium text-slate-300">
                  Company
                </label>

                <input
                  name="company"
                  value={formData.company}
                  onChange={handleChange}
                  placeholder="e.g. Acme Technologies"
                  required
                  className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2.5 text-sm text-white outline-none transition placeholder:text-slate-600 focus:border-blue-500"
                />
              </div>

            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300">
                Location
              </label>

              <input
                name="location"
                value={formData.location}
                onChange={handleChange}
                placeholder="e.g. Chennai, Tamil Nadu / Remote"
                className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2.5 text-sm text-white outline-none transition placeholder:text-slate-600 focus:border-blue-500"
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-slate-300">
                Job description
              </label>

              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Paste the complete job description here..."
                required
                rows={8}
                className="w-full resize-y rounded-lg border border-slate-700 bg-slate-950 px-3 py-2.5 text-sm leading-6 text-white outline-none transition placeholder:text-slate-600 focus:border-blue-500"
              />
            </div>

            <div className="flex justify-end gap-3">
              <button
                type="button"
                onClick={resetForm}
                className="rounded-lg border border-slate-700 px-4 py-2.5 text-sm font-medium text-slate-300 transition hover:bg-slate-800"
              >
                Cancel
              </button>

              <button
                type="submit"
                disabled={submitting}
                className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {submitting && (
                  <Loader2 size={17} className="animate-spin" />
                )}

                {submitting
                  ? "Saving..."
                  : editingJobId
                    ? "Update Job"
                    : "Save Job"}
              </button>
            </div>

          </form>
        </div>
      )}

      {/* Jobs */}
      {loading ? (
        <div className="flex min-h-48 items-center justify-center rounded-2xl border border-slate-800 bg-slate-900">
          <Loader2
            size={28}
            className="animate-spin text-blue-500"
          />
        </div>
      ) : jobs.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-700 bg-slate-900/50 px-6 py-16 text-center">

          <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-xl bg-slate-800">
            <BriefcaseBusiness
              size={26}
              className="text-slate-400"
            />
          </div>

          <h2 className="mt-5 text-lg font-semibold text-white">
            No jobs yet
          </h2>

          <p className="mx-auto mt-2 max-w-md text-sm text-slate-400">
            Add a job description to start comparing your resume with
            real job requirements.
          </p>

          <button
            type="button"
            onClick={() => setShowForm(true)}
            className="mt-6 inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-500"
          >
            <Plus size={18} />
            Add your first job
          </button>

        </div>
      ) : (
        <div className="grid gap-5 lg:grid-cols-2">

          {jobs.map((job) => {
            const requiredSkills = getRequiredSkills(job.id);
            const preferredSkills = getPreferredSkills(job.id);
            const skillPanelOpen =
              expandedSkillJobId === job.id;

            return (
              <div
                key={job.id}
                className="rounded-2xl border border-slate-800 bg-slate-900 p-6 transition hover:border-slate-700"
              >

                {/* Job Header */}
                <div className="flex items-start justify-between gap-4">

                  <div className="flex min-w-0 items-start gap-4">

                    <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-blue-500/10 text-blue-400">
                      <BriefcaseBusiness size={22} />
                    </div>

                    <div className="min-w-0">

                      <h2 className="truncate text-lg font-semibold text-white">
                        {job.title}
                      </h2>

                      <div className="mt-1 flex items-center gap-2 text-sm text-slate-400">
                        <Building2 size={15} />
                        <span>{job.company}</span>
                      </div>

                      {job.location && (
                        <div className="mt-1 flex items-center gap-2 text-sm text-slate-500">
                          <MapPin size={15} />
                          <span>{job.location}</span>
                        </div>
                      )}

                    </div>
                  </div>

                  <div className="flex shrink-0 items-center gap-1">

                    <button
                      type="button"
                      onClick={() => handleEdit(job)}
                      className="rounded-lg p-2 text-slate-400 transition hover:bg-slate-800 hover:text-blue-400"
                      title="Edit job"
                    >
                      <Pencil size={17} />
                    </button>

                    <button
                      type="button"
                      onClick={() => handleDelete(job.id)}
                      className="rounded-lg p-2 text-slate-400 transition hover:bg-red-500/10 hover:text-red-400"
                      title="Delete job"
                    >
                      <Trash2 size={17} />
                    </button>

                  </div>

                </div>

                {/* Job Description */}
                <div className="mt-5 border-t border-slate-800 pt-5">

                  <p className="line-clamp-4 whitespace-pre-line text-sm leading-6 text-slate-400">
                    {job.description}
                  </p>

                </div>

                {/* Skills Summary */}
                <div className="mt-5 space-y-4">

                  <div>
                    <div className="mb-2 flex items-center justify-between">

                      <div className="flex items-center gap-2">
                        <CheckCircle2
                          size={16}
                          className="text-emerald-400"
                        />

                        <span className="text-xs font-semibold uppercase tracking-wide text-slate-400">
                          Required Skills
                        </span>
                      </div>

                      <span className="text-xs text-slate-500">
                        {requiredSkills.length}
                      </span>

                    </div>

                    {requiredSkills.length > 0 ? (
                      <div className="flex flex-wrap gap-2">
                        {requiredSkills.map((skill) => (
                          <span
                            key={skill.id}
                            className="rounded-full border border-blue-500/20 bg-blue-500/10 px-2.5 py-1 text-xs text-blue-300"
                          >
                            {skill.skillName}
                          </span>
                        ))}
                      </div>
                    ) : (
                      <p className="text-xs text-slate-600">
                        No required skills added yet.
                      </p>
                    )}
                  </div>

                  <div>
                    <div className="mb-2 flex items-center justify-between">

                      <div className="flex items-center gap-2">
                        <Star
                          size={16}
                          className="text-amber-400"
                        />

                        <span className="text-xs font-semibold uppercase tracking-wide text-slate-400">
                          Preferred Skills
                        </span>
                      </div>

                      <span className="text-xs text-slate-500">
                        {preferredSkills.length}
                      </span>

                    </div>

                    {preferredSkills.length > 0 ? (
                      <div className="flex flex-wrap gap-2">
                        {preferredSkills.map((skill) => (
                          <span
                            key={skill.id}
                            className="rounded-full border border-amber-500/20 bg-amber-500/10 px-2.5 py-1 text-xs text-amber-300"
                          >
                            {skill.skillName}
                          </span>
                        ))}
                      </div>
                    ) : (
                      <p className="text-xs text-slate-600">
                        No preferred skills added yet.
                      </p>
                    )}
                  </div>

                </div>

                {/* Skill Management Toggle */}
                <button
                  type="button"
                  onClick={() => toggleSkillPanel(job.id)}
                  className="mt-5 inline-flex w-full items-center justify-center gap-2 rounded-lg border border-slate-700 px-4 py-2.5 text-sm font-medium text-slate-300 transition hover:bg-slate-800 hover:text-white"
                >
                  <Settings2 size={17} />

                  {skillPanelOpen
                    ? "Hide Skill Management"
                    : "Manage Job Skills"}
                </button>

                {/* Skill Management Panel */}
                {skillPanelOpen && (
                  <div className="mt-4 rounded-xl border border-slate-800 bg-slate-950 p-4">

                    <div className="mb-4">
                      <h3 className="text-sm font-semibold text-white">
                        Manage required and preferred skills
                      </h3>

                      <p className="mt-1 text-xs leading-5 text-slate-500">
                        Add skills that the job requires or prefers.
                        Importance helps the matching engine prioritize
                        requirements.
                      </p>
                    </div>

                    {/* Add Skill */}
                    <div className="space-y-4">

                      <div>
                        <label className="mb-2 block text-xs font-medium text-slate-400">
                          Skill
                        </label>

                        <select
                          name="skillId"
                          value={skillForm.skillId}
                          onChange={handleSkillFormChange}
                          className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2.5 text-sm text-white outline-none focus:border-blue-500"
                        >
                          <option value="">
                            Select a skill
                          </option>

                          {skills.map((skill) => (
                            <option
                              key={skill.id}
                              value={skill.id}
                              disabled={isSkillAlreadyAdded(
                                job.id,
                                skill.id
                              )}
                            >
                              {skill.name} — {skill.category}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div className="grid gap-4 sm:grid-cols-2">

                        <div>
                          <label className="mb-2 block text-xs font-medium text-slate-400">
                            Skill type
                          </label>

                          <select
                            value={String(skillForm.required)}
                            onChange={handleRequiredChange}
                            className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2.5 text-sm text-white outline-none focus:border-blue-500"
                          >
                            <option value="true">
                              Required
                            </option>

                            <option value="false">
                              Preferred
                            </option>
                          </select>
                        </div>

                        <div>
                          <label className="mb-2 block text-xs font-medium text-slate-400">
                            Importance
                          </label>

                          <select
                            name="importance"
                            value={skillForm.importance}
                            onChange={handleSkillFormChange}
                            className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2.5 text-sm text-white outline-none focus:border-blue-500"
                          >
                            <option value={1}>
                              1 — Low
                            </option>

                            <option value={2}>
                              2 — Low
                            </option>

                            <option value={3}>
                              3 — Medium
                            </option>

                            <option value={4}>
                              4 — High
                            </option>

                            <option value={5}>
                              5 — Critical
                            </option>
                          </select>
                        </div>

                      </div>

                      <button
                        type="button"
                        onClick={() => handleAddSkill(job.id)}
                        disabled={
                          addingSkill ||
                          !skillForm.skillId
                        }
                        className="inline-flex w-full items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        {addingSkill ? (
                          <>
                            <Loader2
                              size={16}
                              className="animate-spin"
                            />
                            Adding...
                          </>
                        ) : (
                          <>
                            <Plus size={16} />
                            Add Skill
                          </>
                        )}
                      </button>

                    </div>

                    {/* Existing Skills */}
                    <div className="mt-6 border-t border-slate-800 pt-5">

                      <h3 className="text-sm font-semibold text-white">
                        Current job skills
                      </h3>

                      {skillsLoading ? (
                        <div className="flex items-center justify-center py-6">
                          <Loader2
                            size={22}
                            className="animate-spin text-blue-500"
                          />
                        </div>
                      ) : getJobSkills(job.id).length === 0 ? (
                        <p className="py-5 text-sm text-slate-500">
                          No skills have been added to this job.
                        </p>
                      ) : (
                        <div className="mt-4 space-y-2">

                          {getJobSkills(job.id)
                            .sort(
                              (a, b) =>
                                b.importance - a.importance
                            )
                            .map((skill) => (
                              <div
                                key={skill.id}
                                className="flex items-center justify-between gap-3 rounded-lg border border-slate-800 bg-slate-900 px-3 py-3"
                              >

                                <div className="min-w-0">
                                  <p className="text-sm font-medium text-white">
                                    {skill.skillName}
                                  </p>

                                  <div className="mt-1 flex flex-wrap items-center gap-2">

                                    <span
                                      className={`rounded-full px-2 py-0.5 text-[11px] ${
                                        skill.required
                                          ? "bg-blue-500/10 text-blue-300"
                                          : "bg-amber-500/10 text-amber-300"
                                      }`}
                                    >
                                      {skill.required
                                        ? "Required"
                                        : "Preferred"}
                                    </span>

                                    <span className="text-[11px] text-slate-500">
                                      Importance{" "}
                                      {skill.importance}/5
                                    </span>

                                  </div>
                                </div>

                                <button
                                  type="button"
                                  onClick={() =>
                                    handleRemoveSkill(
                                      job.id,
                                      skill.skillId
                                    )
                                  }
                                  className="shrink-0 rounded-lg p-2 text-slate-500 transition hover:bg-red-500/10 hover:text-red-400"
                                  title={`Remove ${skill.skillName}`}
                                >
                                  <Trash2 size={16} />
                                </button>

                              </div>
                            ))}

                        </div>
                      )}

                    </div>

                  </div>
                )}

                {/* Footer */}
                <div className="mt-5 flex items-center justify-between text-xs text-slate-500">

                  <span>
                    Added{" "}
                    {job.createdAt
                      ? new Date(
                          job.createdAt
                        ).toLocaleDateString()
                      : "Recently"}
                  </span>

                  <span className="rounded-full bg-slate-800 px-2.5 py-1 text-slate-400">
                    Ready for analysis
                  </span>

                </div>

              </div>
            );
          })}

        </div>
      )}

    </div>
  );
}

export default Jobs;