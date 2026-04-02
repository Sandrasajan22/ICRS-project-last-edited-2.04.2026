import { useCallback, useEffect, useRef, useState } from "react";
import PostCard from "./PostCard";
import { fetchJson } from "../../utils/fetchJson";
import { API_BASE, normalizeUrl } from "../../utils/PostDisplay";
import "../../styles/feed.css";

export default function Feed() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const observerRef = useRef(null);
  const seenPostsRef = useRef(new Set());

  const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
  const userId = storedUser?.id || Number(localStorage.getItem("userId")) || 0;

  const normalizePost = useCallback((post) => {
    return {
      ...post,
      userName: post.userName?.trim() ? post.userName : post.firstName || "User",
      profileImage: normalizeUrl(post.profileImage || ""),
      mediaUrl: normalizeUrl(post.mediaUrl || ""),
    };
  }, []);

  const loadFeed = useCallback(async () => {
    if (loading || !hasMore || !userId) return;

    setLoading(true);
    setError("");

    try {
      const data = await fetchJson(
        `${API_BASE}/api/feed?userId=${userId}&page=${page}&size=5`
      );

      const incomingPosts = Array.isArray(data.posts) ? data.posts : [];
      const normalizedPosts = incomingPosts.map(normalizePost);

      setPosts((prev) => {
        const merged = [...prev, ...normalizedPosts];
        return merged.filter(
          (post, index, arr) => index === arr.findIndex((p) => p.id === post.id)
        );
      });

      setHasMore(Boolean(data.hasMore));
      setPage((prev) => prev + 1);
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to load feed");
    } finally {
      setLoading(false);
    }
  }, [userId, page, loading, hasMore, normalizePost]);

  useEffect(() => {
    loadFeed();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const lastPostRef = useCallback(
    (node) => {
      if (loading) return;
      if (observerRef.current) observerRef.current.disconnect();

      observerRef.current = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && hasMore) {
          loadFeed();
        }
      });

      if (node) observerRef.current.observe(node);
    },
    [loading, hasMore, loadFeed]
  );

  const handleLike = async (postId) => {
    const existingPost = posts.find((p) => p.id === postId);
    if (!existingPost) return;

    const alreadyLiked = !!existingPost.likedByCurrentUser;

    setPosts((prev) =>
      prev.map((p) =>
        p.id === postId
          ? {
              ...p,
              likedByCurrentUser: !alreadyLiked,
              likeCount: alreadyLiked
                ? Math.max(0, Number(p.likeCount || 0) - 1)
                : Number(p.likeCount || 0) + 1,
            }
          : p
      )
    );

    try {
      const updatedPost = await fetchJson(
        `${API_BASE}/api/feed/${postId}/like?userId=${userId}`,
        { method: "POST" }
      );

      setPosts((prev) =>
        prev.map((p) => (p.id === postId ? normalizePost(updatedPost) : p))
      );
    } catch (err) {
      console.error(err);
      setPosts((prev) =>
        prev.map((p) =>
          p.id === postId
            ? {
                ...p,
                likedByCurrentUser: alreadyLiked,
                likeCount: existingPost.likeCount,
              }
            : p
        )
      );
    }
  };

  const handleVisible = async (postId) => {
    if (seenPostsRef.current.has(postId)) return;
    seenPostsRef.current.add(postId);

    try {
      await fetchJson(`${API_BASE}/api/feed/${postId}/view?userId=${userId}`, {
        method: "POST",
      });

      setPosts((prev) =>
        prev.map((p) =>
          p.id === postId ? { ...p, viewCount: Number(p.viewCount || 0) + 1 } : p
        )
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleCommentAdded = (postId) => {
    setPosts((prev) =>
      prev.map((p) =>
        p.id === postId ? { ...p, commentCount: Number(p.commentCount || 0) + 1 } : p
      )
    );
  };

  const handleCommentRemoved = (postId) => {
    setPosts((prev) =>
      prev.map((p) =>
        p.id === postId
          ? { ...p, commentCount: Math.max(0, Number(p.commentCount || 0) - 1) }
          : p
      )
    );
  };

  return (
    <div className="feed-container">
      {error && <div className="feed-status error">{error}</div>}

      {!loading && posts.length === 0 && !error && (
        <div className="feed-status">No posts found</div>
      )}

      {posts.map((post, index) => (
        <PostCard
          key={post.id}
          post={post}
          viewerId={userId}
          onLike={handleLike}
          onVisible={handleVisible}
          onCommentAdded={handleCommentAdded}
          onCommentRemoved={handleCommentRemoved}
          refProp={index === posts.length - 1 ? lastPostRef : null}
        />
      ))}

      {loading && <div className="feed-status">Loading...</div>}
      {!hasMore && posts.length > 0 && <div className="feed-status">No more posts</div>}
    </div>
  );
}