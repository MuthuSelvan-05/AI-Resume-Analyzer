import api from "./api";

const dashboardService = {
  async getDashboardData() {
    const [resumesResponse, jobsResponse, analysesResponse] =
      await Promise.all([
        api.get("/resumes"),
        api.get("/jobs"),
        api.get("/analyses"),
      ]);

    return {
      resumes: resumesResponse.data,
      jobs: jobsResponse.data,
      analyses: analysesResponse.data,
    };
  },
};

export default dashboardService;