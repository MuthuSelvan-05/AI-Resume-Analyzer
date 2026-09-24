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

  async updateResume(resumeId, data) {
    const response = await api.put(`/resumes/${resumeId}`, data);
    return response.data;
  },

  async deleteResume(resumeId) {
    const response = await api.delete(`/resumes/${resumeId}`);
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

  async getVersions(resumeId) {
    const response = await api.get(`/resumes/${resumeId}/versions`);
    return response.data;
  },

  getDownloadUrl(resumeId, versionNumber) {
    return `http://localhost:8080/api/resumes/${resumeId}/versions/${versionNumber}/download`;
  },
};

export default resumeService;