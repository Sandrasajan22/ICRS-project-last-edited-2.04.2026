export const API_BASE = "http://localhost:8080";

// ✅ Safely parse user
function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem("user") || "{}");
  } catch {
    return {};
  }
}

// ✅ Get userId safely
export function getUserId() {
  const user = getStoredUser();

  if (user?.id) return user.id;

  const storedId = localStorage.getItem("userId");
  return storedId ? Number(storedId) : null;
}

// ✅ Get user name (clean)
export function getUserName() {
  const user = getStoredUser();

  if (user?.name?.trim()) return user.name.trim();

  const fname = user?.fname || "";
  const lname = user?.lname || "";

  const fullName = `${fname} ${lname}`.trim();

  return fullName || "User";
}

// ✅ Get avatar
export function getUserAvatar() {
  const user = getStoredUser();
  return user?.profileImage || user?.avatar || "";
}

// ✅ AUTH HEADERS (FIXED)
export function getAuthHeaders() {
  const token = localStorage.getItem("token");

  return {
    "Content-Type": "application/json",
    ...(token && token !== "null" && { Authorization: `Bearer ${token}` }),
  };
}