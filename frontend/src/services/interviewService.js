import api from "./api";

const interviewService = {
  async generateQuestions(data) {
    const response = await api.post(
      "/interview/generation",
      data
    );

    return response.data;
  },

  async getQuestions() {
    const response = await api.get("/interview/questions");
    return response.data;
  },

  async getQuestionsByAnalysis(analysisId) {
    const response = await api.get(
      `/interview/questions/analysis/${analysisId}`
    );

    return response.data;
  },

  async submitAnswer(questionId, answer) {
    const response = await api.post("/interview/answers", {
      questionId,
      answer,
    });

    return response.data;
  },

  async getAnswers() {
    const response = await api.get("/interview/answers");
    return response.data;
  },
};

export default interviewService;