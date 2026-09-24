
import api from "./api";

const learningResourceService = {
  async getResourcesBySkill(skillId) {
    const response = await api.get(
      `/learning-resources/skill/${skillId}`
    );

    return response.data;
  },

  async getResourcesByRoadmapStep(roadmapStepId) {
    const response = await api.get(
      `/learning-resources/roadmap-step/${roadmapStepId}`
    );

    return response.data;
  },
};

export default learningResourceService;