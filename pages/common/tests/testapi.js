const BASE_URL = "http://localhost:8080/api";

export const getCommunicationTest = async (stream, level) => {
  const userId = localStorage.getItem("userId");

  if (!userId) {
    throw new Error("User not logged in.");
  }

  const res = await fetch(
    `${BASE_URL}/communication-test/random?userId=${userId}&stream=${encodeURIComponent(
      stream
    )}&level=${encodeURIComponent(level)}`
  );

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Failed to load communication test");
  }

  return res.json();
};