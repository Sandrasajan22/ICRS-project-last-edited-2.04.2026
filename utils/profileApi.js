// src/utils/profileApi.js
import axios from "axios";

export const API_BASE = "http://localhost:8080";

/**
 * Return stored user id or null.
 */
export function getCurrentUserId() {
  try {
    const user = JSON.parse(localStorage.getItem("user") || "null");
    if (user && (user.id || user.userId)) return user.id || user.userId;
    const idFromKey = localStorage.getItem("userId");
    return idFromKey ? Number(idFromKey) : null;
  } catch {
    return null;
  }
}

/**
 * Normalize an image/path to a usable URL.
 * - absolute URLs are returned as-is
 * - paths starting with "/" are prefixed with API_BASE
 * - otherwise returned as-is
 */
export function normalizeUrl(path) {
  if (!path) return "";
  if (path.startsWith("http://") || path.startsWith("https://")) return path;
  if (path.startsWith("/")) return `${API_BASE}${path}`;
  return path;
}

/**
 * Fetch profile object for a user.
 * endpoint: base path like "/api/mentor-profile" (no trailing slash required)
 * returns the response data (not axios response)
 */
export async function fetchProfile(endpoint, userId) {
  if (!endpoint) throw new Error("Missing endpoint");
  if (!userId) throw new Error("Missing userId");

  const url = `${API_BASE}${endpoint.replace(/\/$/, "")}/users/${userId}`;

  try {
    const res = await axios.get(url, { headers: { Accept: "application/json" } });
    return res.data;
  } catch (err) {
    if (err.response) {
      const msg = err.response.data?.error || err.response.data || err.response.statusText;
      throw new Error(`Server error: ${msg}`);
    } else if (err.request) {
      throw new Error("No response from server");
    } else {
      throw new Error(err.message || "Request failed");
    }
  }
}

/**
 * Update profile using multipart/form-data.
 * endpoint: "/api/mentor-profile"
 * userId: numeric id
 * data: plain object
 * fileField: form field name for file (default "profileImage")
 * file: File object or null
 *
 * Throws on non-OK responses and returns parsed JSON on success.
 */
export async function updateProfile(endpoint, userId, data, fileField = "profileImage", file = null) {
  if (!endpoint) throw new Error("Missing endpoint");
  if (!userId) throw new Error("Missing userId");

  const formData = new FormData();
  formData.append("data", JSON.stringify(data));
  if (file) formData.append(fileField, file);

  const url = `${API_BASE}${endpoint.replace(/\/$/, "")}/${userId}`;

  const res = await fetch(url, {
    method: "PUT",
    headers: {
      // Do not set Content-Type for multipart; browser will set boundary
      Accept: "application/json",
    },
    body: formData,
  });

  if (!res.ok) {
    const txt = await res.text().catch(() => "");
    throw new Error(txt || `Update failed: ${res.status}`);
  }

  return res.json();
}
