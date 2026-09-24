import api from "./api";

const roadmapService = {
  async getRoadmaps() {
    const response = await api.get("/roadmaps");
    return response.data;
  },

  async getRoadmap(roadmapId) {
    const response = await api.get(`/roadmaps/${roadmapId}`);
    return response.data;
  },

  async getRoadmapSteps(roadmapId) {
    const response = await api.get(`/roadmaps/${roadmapId}/steps`);
    return response.data;
  },

  async generateRoadmaps() {
    const response = await api.post("/roadmaps/generate");
    return response.data;
  },

  async updateStep(roadmapId, stepId, data) {
    const response = await api.put(
      `/roadmaps/${roadmapId}/steps/${stepId}`,
      data
    );
    return response.data;
  },
};

export default roadmapService;