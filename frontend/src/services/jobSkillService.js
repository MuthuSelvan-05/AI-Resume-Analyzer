import api from "./api";

const jobSkillService = {
  async getJobSkills(jobId) {
    const response = await api.get(`/jobs/${jobId}/skills`);
    return response.data;
  },

  async addJobSkill(jobId, data) {
    const response = await api.post(`/jobs/${jobId}/skills`, data);
    return response.data;
  },

  async updateJobSkill(jobId, skillId, data) {
    const response = await api.put(
      `/jobs/${jobId}/skills/${skillId}`,
      data
    );
    return response.data;
  },

  async removeJobSkill(jobId, skillId) {
    const response = await api.delete(
      `/jobs/${jobId}/skills/${skillId}`
    );
    return response.data;
  },
};

export default jobSkillService;