import api from "./api";

const analysisService = {
  async createAnalysis(resumeVersionId, jobId) {
    const response = await api.post("/analyses", {
      resumeVersionId,
      jobId,
    });

    return response.data;
  },

  async runAnalysis(analysisId) {
    const response = await api.post(
      `/analyses/${analysisId}/run`
    );

    return response.data;
  },

  async getAnalyses() {
    const response = await api.get("/analyses");
    return response.data;
  },

  async getAnalysis(analysisId) {
    const response = await api.get(
      `/analyses/${analysisId}`
    );

    return response.data;
  },

  async deleteAnalysis(analysisId) {
    await api.delete(`/analyses/${analysisId}`);
  },
};

export default analysisService;