import api from "./api";

const jobService = {
  async getJobs() {
    const response = await api.get("/jobs");
    return response.data;
  },

  async getJob(jobId) {
    const response = await api.get(`/jobs/${jobId}`);
    return response.data;
  },

  async createJob(data) {
    const response = await api.post("/jobs", data);
    return response.data;
  },

  async updateJob(jobId, data) {
    const response = await api.put(`/jobs/${jobId}`, data);
    return response.data;
  },

  async deleteJob(jobId) {
    const response = await api.delete(`/jobs/${jobId}`);
    return response.data;
  },
};

export default jobService;