const BASE = "http://localhost:8080/api/follow";

export const followUser = (userId, targetId) =>
  fetch(`${BASE}/${targetId}?userId=${userId}`, {
    method: "POST",
  });

export const unfollowUser = (userId, targetId) =>
  fetch(`${BASE}/${targetId}?userId=${userId}`, {
    method: "DELETE",
  });

// 🔥 ADD THIS
export const checkFollow = async (userId, targetId) => {
  const res = await fetch(
    `${BASE}/status?userId=${userId}&targetId=${targetId}`
  );
  return res.json(); // returns true / false
};