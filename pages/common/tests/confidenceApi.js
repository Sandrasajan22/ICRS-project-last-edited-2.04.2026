import axios from "axios";

const API_BASE = "http://localhost:8080/api/confidence-tasks";

export const getConfidenceTasks = async (userId, level) => {
  const res = await axios.get(`${API_BASE}/random`, {
    params: { userId, level },
  });
  return res.data;
};

export const submitConfidenceTasks = async (payload) => {
  try {
    const res = await axios.post(`${API_BASE}/submit`, payload);
    return res.data;
  } catch (error) {
    console.error("Confidence submit error:", error?.response?.data || error.message);
    throw error;
  }
};

export const getConfidenceLevelStatus = async (userId) => {
  const res = await axios.get(`${API_BASE}/level-status`, {
    params: { userId },
  });
  return res.data;
};