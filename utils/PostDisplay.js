export const API_BASE = "http://localhost:8080";

export const normalizeUrl = (url) => {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  if (url.startsWith("/")) return `${API_BASE}${url}`;
  return `${API_BASE}/${url}`;
};

export const ratioClassMap = {
  "1:1": "ratio-1-1",
  "4:5": "ratio-4-5",
  "16:9": "ratio-16-9",
  "9:16": "ratio-9-16",
  "FREE": "ratio-free",
};

export const getMediaWrapClass = (post) => {
  const ratio = post?.aspectRatio || "4:5";
  return `post-media-wrap ${ratioClassMap[ratio] || "ratio-4-5"} ${
    post?.mediaType === "VIDEO" ? "video" : "image"
  }`;
};

export const getMediaStyle = (post) => {
  const zoom = Number(post?.cropZoom || 1);
  const x = Number(post?.cropX ?? 50);
  const y = Number(post?.cropY ?? 50);

  return {
    objectFit: "cover",
    objectPosition: `${x}% ${y}%`,
    transform: `scale(${zoom})`,
    transformOrigin: `${x}% ${y}%`,
  };
};