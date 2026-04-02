export async function fetchJson(url, options = {}) {
  const res = await fetch(url, options);

  const text = await res.text();
  let data;

  try {
    data = text ? JSON.parse(text) : {};
  } catch {
    data = { error: text || "Invalid server response" };
  }

  if (!res.ok) {
    throw new Error(data.error || data.message || `Request failed: ${res.status}`);
  }

  return data;
}