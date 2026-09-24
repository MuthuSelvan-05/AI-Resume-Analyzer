import api from "./api";

const skillService = {
  async getSkills() {
    const response = await api.get("/skills");
    return response.data;
  },

  async getSkill(skillId) {
    const response = await api.get(`/skills/${skillId}`);
    return response.data;
  },

  async getSkillsByCategory(category) {
    const response = await api.get(
      `/skills/category/${encodeURIComponent(category)}`
    );
    return response.data;
  },
};

export default skillService;