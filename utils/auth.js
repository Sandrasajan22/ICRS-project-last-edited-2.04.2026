// src/utils/auth.js
import axios from "axios";

export const API_BASE = "http://localhost:8080";

/**
 * Save authentication info to localStorage.
 * Accepts an object that may contain: token, user, role, userId.
 */
export function saveAuth(payload = {}) {
  try {
    const { token, user, role, userId } = payload;

    if (token) {
      localStorage.setItem("token", token);
    }

    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
      if (user.id) localStorage.setItem("userId", String(user.id));
    } else if (userId) {
      localStorage.setItem("userId", String(userId));
    }

    if (role) {
      localStorage.setItem("role", role);
    }
  } catch (e) {
    // eslint-disable-next-line no-console
    console.error("saveAuth error:", e);
  }
}

/** Clear auth from storage */
export function clearAuth() {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  localStorage.removeItem("userId");
  localStorage.removeItem("role");
}

/** Return stored user id or null */
export function getUserId() {
  try {
    const user = JSON.parse(localStorage.getItem("user") || "null");
    if (user && (user.id || user.userId)) return user.id || user.userId;
    const idFromKey = localStorage.getItem("userId");
    return idFromKey ? Number(idFromKey) : null;
  } catch (e) {
    return null;
  }
}

/** Return headers for authenticated requests */
export function getAuthHeaders() {
  const token = localStorage.getItem("token");
  const headers = {
    Accept: "application/json",
  };
  if (token) headers["Authorization"] = `Bearer ${token}`;
  return headers;
}

/** Axios helper to fetch profile by id */
export const fetchProfile = (id) => {
  return axios.get(`${API_BASE}/api/mentor-profile/users/${id}`, {
    headers: getAuthHeaders(),
  });
};

/**
 * Update profile using multipart/form-data.
 * endpoint should be like "/api/mentor-profile" (no trailing slash).
 */
export const updateProfile = async (endpoint, userId, data, fileField = "profileImage", file = null) => {
  const formData = new FormData();
  formData.append("data", JSON.stringify(data));
  if (file) formData.append(fileField, file);

  const res = await fetch(`${API_BASE}${endpoint}/${userId}`, {
    method: "PUT",
    headers: {
      // Do not set Content-Type for multipart; browser will set boundary
      ...getAuthHeaders(),
    },
    body: formData,
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(msg || "Update failed");
  }

  return res.json();
};

// Explicit named exports (helps some bundlers/ESM resolvers)
