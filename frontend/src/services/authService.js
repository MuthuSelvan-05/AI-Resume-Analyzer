import api from "./api";

const authService = {
  async register(userData) {
    const response = await api.post("/auth/register", userData);

    return response.data;
  },

  async login(credentials) {
    const response = await api.post("/auth/login", credentials);

    return response.data;
  },

  async getProfile() {
    const response = await api.get("/users/profile");

    return response.data;
  },

  async changePassword(passwordData) {
    const response = await api.put("/users/password", passwordData);

    return response.data;
  },
};

export default authService;