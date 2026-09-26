import api from "./api";

const resumeService = {
  async getResumes() {
    const response = await api.get("/resumes");
    return response.data;
  },

  async getResume(resumeId) {
    const response = await api.get(`/resumes/${resumeId}`);
    return response.data;
  },

  async createResume(data) {
    const response = await api.post("/resumes", data);
    return response.data;
  },

  async uploadVersion(resumeId, file) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await api.post(
      `/resumes/${resumeId}/versions`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      },
    );

    return response.data;
  },

  async deleteResume(resumeId) {
    const response = await api.delete(`/resumes/${resumeId}`);
    return response.data;
  },

  async downloadVersion(resumeId, versionNumber) {
    const response = await api.get(
      `/resumes/${resumeId}/versions/${versionNumber}/download`,
      {
        responseType: "blob",
      },
    );

    const contentDisposition =
      response.headers["content-disposition"];

    let fileName = `resume-v${versionNumber}`;

    if (contentDisposition) {
      const match = contentDisposition.match(
        /filename="?([^"]+)"?/i,
      );

      if (match?.[1]) {
        fileName = match[1];
      }
    }

    const blob = new Blob([response.data], {
      type:
        response.headers["content-type"] ||
        "application/octet-stream",
    });

    const url = window.URL.createObjectURL(blob);

    const link = document.createElement("a");
    link.href = url;
    link.download = fileName;

    document.body.appendChild(link);
    link.click();
    link.remove();

    window.URL.revokeObjectURL(url);
  },
};

export default resumeService;