import { useEffect, useRef, useState } from "react";
import {
  AlertCircle,
  CheckCircle2,
  Download,
  FileText,
  Loader2,
  Plus,
  Trash2,
  Upload,
} from "lucide-react";

import resumeService from "../services/resumeService";

function Resumes() {
  const [resumes, setResumes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [title, setTitle] = useState("");

  const [creating, setCreating] = useState(false);
  const [uploadingId, setUploadingId] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  const [successMessage, setSuccessMessage] = useState("");

  const fileInputs = useRef({});

  useEffect(() => {
    loadResumes();
  }, []);

  const loadResumes = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await resumeService.getResumes();

      setResumes(data);
    } catch (requestError) {
      console.error("Failed to load resumes:", requestError);

      setError(
        requestError.response?.data?.message ||
          "Unable to load your resumes.",
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCreateResume = async (event) => {
    event.preventDefault();

    if (!title.trim()) {
      return;
    }

    try {
      setCreating(true);
      setError("");
      setSuccessMessage("");

      await resumeService.createResume({
        title: title.trim(),
      });

      setTitle("");
      setShowCreateForm(false);
      setSuccessMessage("Resume created successfully.");

      await loadResumes();
    } catch (requestError) {
      console.error("Failed to create resume:", requestError);

      setError(
        requestError.response?.data?.message ||
          "Unable to create the resume.",
      );
    } finally {
      setCreating(false);
    }
  };

  const handleFileSelect = async (resumeId, event) => {
    const file = event.target.files?.[0];

    if (!file) {
      return;
    }

    const allowedTypes = [
      "application/pdf",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];

    if (!allowedTypes.includes(file.type)) {
      setError("Only PDF and DOCX files are supported.");
      event.target.value = "";
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      setError("The maximum file size is 10 MB.");
      event.target.value = "";
      return;
    }

    try {
      setUploadingId(resumeId);
      setError("");
      setSuccessMessage("");

      await resumeService.uploadVersion(resumeId, file);

      setSuccessMessage("Resume version uploaded successfully.");

      await loadResumes();
    } catch (requestError) {
      console.error("Resume upload failed:", requestError);

      setError(
        requestError.response?.data?.message ||
          "Unable to upload the resume.",
      );
    } finally {
      setUploadingId(null);
      event.target.value = "";
    }
  };

  const handleDelete = async (resumeId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this resume and its versions?",
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(resumeId);
      setError("");
      setSuccessMessage("");

      await resumeService.deleteResume(resumeId);

      setSuccessMessage("Resume deleted successfully.");

      await loadResumes();
    } catch (requestError) {
      console.error("Resume deletion failed:", requestError);

      setError(
        requestError.response?.data?.message ||
          "Unable to delete the resume.",
      );
    } finally {
      setDeletingId(null);
    }
  };

  const formatDate = (dateValue) => {
    if (!dateValue) {
      return "—";
    }

    return new Date(dateValue).toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  };

  return (
    <div className="mx-auto max-w-7xl space-y-8">
      <section className="flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <p className="text-sm font-medium text-blue-400">Resume Management</p>

          <h2 className="mt-2 text-3xl font-bold tracking-tight">
            Your resumes
          </h2>

          <p className="mt-2 max-w-2xl text-slate-400">
            Upload and manage resume versions that you can use for job
            matching and AI analysis.
          </p>
        </div>

        <button
          onClick={() => setShowCreateForm((current) => !current)}
          className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium transition hover:bg-blue-500"
        >
          <Plus size={17} />
          New Resume
        </button>
      </section>

      {error && (
        <div className="flex items-start gap-3 rounded-lg border border-red-500/20 bg-red-500/10 p-4 text-sm text-red-400">
          <AlertCircle size={18} className="mt-0.5 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {successMessage && (
        <div className="flex items-start gap-3 rounded-lg border border-emerald-500/20 bg-emerald-500/10 p-4 text-sm text-emerald-400">
          <CheckCircle2 size={18} className="mt-0.5 shrink-0" />
          <span>{successMessage}</span>
        </div>
      )}

      {showCreateForm && (
        <section className="rounded-xl border border-slate-800 bg-slate-900/60 p-6">
          <h3 className="font-semibold">Create a resume profile</h3>

          <p className="mt-1 text-sm text-slate-500">
            Give this resume a name such as "Software Developer Resume".
          </p>

          <form
            onSubmit={handleCreateResume}
            className="mt-5 flex flex-col gap-3 sm:flex-row"
          >
            <input
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="Resume title"
              required
              className="flex-1 rounded-lg border border-slate-700 bg-slate-950 px-3 py-2.5 text-sm outline-none transition placeholder:text-slate-600 focus:border-blue-500"
            />

            <button
              type="submit"
              disabled={creating}
              className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-medium transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {creating && (
                <Loader2 size={17} className="animate-spin" />
              )}
              {creating ? "Creating..." : "Create Resume"}
            </button>
          </form>
        </section>
      )}

      {loading ? (
        <div className="flex min-h-60 items-center justify-center rounded-xl border border-slate-800 bg-slate-900/40">
          <div className="flex items-center gap-3 text-sm text-slate-400">
            <Loader2 size={20} className="animate-spin" />
            Loading resumes...
          </div>
        </div>
      ) : resumes.length === 0 ? (
        <div className="rounded-xl border border-dashed border-slate-700 bg-slate-900/30 p-12 text-center">
          <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-xl bg-slate-800">
            <FileText size={25} className="text-slate-500" />
          </div>

          <h3 className="mt-5 font-semibold">No resumes yet</h3>

          <p className="mx-auto mt-2 max-w-md text-sm text-slate-500">
            Create a resume profile and upload your PDF or DOCX file to start
            analyzing it.
          </p>

          <button
            onClick={() => setShowCreateForm(true)}
            className="mt-6 inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium transition hover:bg-blue-500"
          >
            <Plus size={17} />
            Create your first resume
          </button>
        </div>
      ) : (
        <section className="grid gap-5 lg:grid-cols-2">
          {resumes.map((resume) => (
            <div
              key={resume.id}
              className="rounded-xl border border-slate-800 bg-slate-900/60 p-6 transition hover:border-slate-700"
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex min-w-0 items-center gap-4">
                  <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-blue-600/10">
                    <FileText size={21} className="text-blue-400" />
                  </div>

                  <div className="min-w-0">
                    <h3 className="truncate font-semibold">
                      {resume.title}
                    </h3>

                    <p className="mt-1 text-xs text-slate-500">
                      Created {formatDate(resume.createdAt)}
                    </p>
                  </div>
                </div>

                <button
                  onClick={() => handleDelete(resume.id)}
                  disabled={deletingId === resume.id}
                  className="rounded-lg p-2 text-slate-500 transition hover:bg-red-500/10 hover:text-red-400 disabled:opacity-50"
                  title="Delete resume"
                >
                  {deletingId === resume.id ? (
                    <Loader2 size={17} className="animate-spin" />
                  ) : (
                    <Trash2 size={17} />
                  )}
                </button>
              </div>

              <div className="mt-6 rounded-lg border border-slate-800 bg-slate-950/60 p-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-xs text-slate-500">
                      Current version
                    </p>

                    <p className="mt-1 text-sm font-medium text-slate-300">
                      {resume.latestVersion
                        ? `Version ${resume.latestVersion.versionNumber}`
                        : "No file uploaded"}
                    </p>
                  </div>

                  {resume.latestVersion && (
                    <span className="rounded-full bg-emerald-500/10 px-2.5 py-1 text-xs text-emerald-400">
                      Uploaded
                    </span>
                  )}
                </div>

                {resume.latestVersion && (
                  <p className="mt-3 truncate text-xs text-slate-500">
                    {resume.latestVersion.fileName}
                  </p>
                )}
              </div>

              <div className="mt-5 flex flex-wrap gap-2">
                <input
                  ref={(element) => {
                    fileInputs.current[resume.id] = element;
                  }}
                  type="file"
                  accept=".pdf,.docx"
                  className="hidden"
                  onChange={(event) =>
                    handleFileSelect(resume.id, event)
                  }
                />

                <button
                  onClick={() =>
                    fileInputs.current[resume.id]?.click()
                  }
                  disabled={uploadingId === resume.id}
                  className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-3.5 py-2 text-sm font-medium transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {uploadingId === resume.id ? (
                    <Loader2 size={16} className="animate-spin" />
                  ) : (
                    <Upload size={16} />
                  )}

                  {uploadingId === resume.id
                    ? "Uploading..."
                    : "Upload Version"}
                </button>

                {resume.latestVersion && (
                  <a
                    href={resumeService.getDownloadUrl(
                      resume.id,
                      resume.latestVersion.versionNumber,
                    )}
                    target="_blank"
                    rel="noreferrer"
                    className="inline-flex items-center gap-2 rounded-lg border border-slate-700 px-3.5 py-2 text-sm text-slate-300 transition hover:bg-slate-800"
                  >
                    <Download size={16} />
                    Download
                  </a>
                )}
              </div>
            </div>
          ))}
        </section>
      )}
    </div>
  );
}

export default Resumes;