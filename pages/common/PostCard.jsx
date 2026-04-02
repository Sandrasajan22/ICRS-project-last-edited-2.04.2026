import { useEffect, useRef, useState } from "react";
import {
  FaEllipsisH,
  FaHeart,
  FaRegCommentDots,
  FaRegHeart,
} from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { fetchJson } from "../../utils/fetchJson";
import {
  API_BASE,
  getMediaStyle,
  getMediaWrapClass,
  normalizeUrl,
} from "../../utils/PostDisplay";

function formatTags(tags) {
  if (!tags) return "";
  return tags
    .split(",")
    .map((tag) => tag.trim())
    .filter(Boolean)
    .map((tag) => `#${tag}`)
    .join(" ");
}

export default function PostCard({
  post,
  viewerId,
  onLike,
  onVisible,
  onCommentAdded,
  onCommentRemoved,
  refProp,
}) {
  const localRef = useRef(null);
  const navigate = useNavigate();

  const [comments, setComments] = useState([]);
  const [showComments, setShowComments] = useState(false);
  const [commentText, setCommentText] = useState("");
  const [commentLoading, setCommentLoading] = useState(false);
  const [commentMenuId, setCommentMenuId] = useState(null);
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingCommentText, setEditingCommentText] = useState("");

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          onVisible(post.id);
        }
      },
      { threshold: 0.6 }
    );

    if (localRef.current) observer.observe(localRef.current);
    return () => observer.disconnect();
  }, [post.id, onVisible]);

  const goToProfile = () => {
    if (!post.userId) return;
    navigate(`/profile/${post.userId}`);
  };

  const loadComments = async () => {
    try {
      const data = await fetchJson(
        `${API_BASE}/api/feed/${post.id}/comments?viewerId=${viewerId}`
      );

      const list = Array.isArray(data) ? data : [];
      setComments(
        list.map((c) => ({
          ...c,
          profileImage: normalizeUrl(c.profileImage || ""),
        }))
      );
    } catch (err) {
      console.error(err);
    }
  };

  const toggleComments = async () => {
    const next = !showComments;
    setShowComments(next);
    if (next) await loadComments();
  };

  const submitComment = async () => {
    if (!commentText.trim()) return;

    try {
      setCommentLoading(true);

      const created = await fetchJson(`${API_BASE}/api/feed/${post.id}/comments`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          userId: viewerId,
          content: commentText.trim(),
        }),
      });

      setComments((prev) => [
        ...prev,
        {
          ...created,
          profileImage: normalizeUrl(created.profileImage || ""),
        },
      ]);

      setCommentText("");
      onCommentAdded?.(post.id);
    } catch (err) {
      alert(err.message || "Failed to add comment");
    } finally {
      setCommentLoading(false);
    }
  };

  const updateComment = async (commentId) => {
    if (!editingCommentText.trim()) return;

    try {
      const updated = await fetchJson(`${API_BASE}/api/feed/comments/${commentId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          userId: viewerId,
          content: editingCommentText.trim(),
        }),
      });

      setComments((prev) =>
        prev.map((c) =>
          c.id === commentId
            ? { ...updated, profileImage: normalizeUrl(updated.profileImage || "") }
            : c
        )
      );

      setEditingCommentId(null);
      setEditingCommentText("");
      setCommentMenuId(null);
    } catch (err) {
      alert(err.message || "Failed to update comment");
    }
  };

  const deleteComment = async (commentId) => {
    const ok = window.confirm("Delete this comment?");
    if (!ok) return;

    try {
      await fetchJson(`${API_BASE}/api/feed/comments/${commentId}?userId=${viewerId}`, {
        method: "DELETE",
      });

      setComments((prev) => prev.filter((c) => c.id !== commentId));
      setCommentMenuId(null);
      onCommentRemoved?.(post.id);
    } catch (err) {
      alert(err.message || "Failed to delete comment");
    }
  };

  return (
    <div
      className="post-card"
      ref={(node) => {
        localRef.current = node;
        if (refProp) refProp(node);
      }}
    >
      <div className="post-header">
        <div className="post-user" onClick={goToProfile} role="button" tabIndex={0}>
          {post.profileImage && (
            <img
              src={post.profileImage}
              alt={post.userName || "User"}
              className="avatar"
            />
          )}

          <div className="post-user-text">
            <h4>{post.userName || post.firstName || "User"}</h4>
            <small>
              {post.createdAt ? new Date(post.createdAt).toLocaleString() : "Just now"}
            </small>
          </div>
        </div>
      </div>

      {post.mediaUrl && (
        <div className={getMediaWrapClass(post)}>
          {post.mediaType === "VIDEO" ? (
            <video controls className="post-media" src={post.mediaUrl} />
          ) : (
            <img
              src={post.mediaUrl}
              alt="post media"
              className="post-media"
              style={getMediaStyle(post)}
            />
          )}
        </div>
      )}

      <div className="post-meta-area">
        <div className="post-actions">
          <button
            className={`icon-action-btn ${post.likedByCurrentUser ? "liked" : ""}`}
            onClick={() => onLike(post.id)}
            type="button"
          >
            {post.likedByCurrentUser ? <FaHeart /> : <FaRegHeart />}
          </button>

          <button
            className="icon-action-btn comment-toggle-btn"
            onClick={toggleComments}
            type="button"
          >
            <FaRegCommentDots />
          </button>
        </div>

        <div className="post-meta">
          <span>❤️ {Number(post.likeCount || 0)}</span>
          <span>🗨️ {Number(post.commentCount || 0)}</span>
          <span>👁️ {Number(post.viewCount || 0)}</span>
        </div>

        <div className="post-content">
          {post.caption && (
            <div className="post-caption">
              <strong>{post.userName || "User"}</strong> {post.caption}
            </div>
          )}
          {!!post.tags && <div className="post-tags">{formatTags(post.tags)}</div>}
        </div>
      </div>


      {showComments && (
        <div className="post-comments">
          <div className="comment-list">
            {comments.length === 0 ? (
              <div className="comment-empty">No comments yet.</div>
            ) : (
              comments.map((comment) => (
                <div className="comment-item" key={comment.id}>
                  {comment.profileImage && (
                    <img
                      className="comment-avatar"
                      src={comment.profileImage}
                      alt={comment.userName || "User"}
                      onClick={() => navigate(`/profile/${comment.userId}`)}
                    />
                  )}

                  <div className="comment-body">
                    <div className="comment-top">
                      <div className="comment-name">{comment.userName || "User"}</div>

                      {comment.owner && (
                        <div className="comment-menu-wrap">
                          <button
                            className="comment-menu-btn"
                            type="button"
                            onClick={() =>
                              setCommentMenuId(commentMenuId === comment.id ? null : comment.id)
                            }
                          >
                            <FaEllipsisH />
                          </button>

                          {commentMenuId === comment.id && (
                            <div className="comment-menu">
                              <button
                                type="button"
                                onClick={() => {
                                  setEditingCommentId(comment.id);
                                  setEditingCommentText(comment.content || "");
                                  setCommentMenuId(null);
                                }}
                              >
                                Edit
                              </button>
                              <button
                                type="button"
                                className="danger"
                                onClick={() => deleteComment(comment.id)}
                              >
                                Delete
                              </button>
                            </div>
                          )}
                        </div>
                      )}
                    </div>

                    {editingCommentId === comment.id ? (
                      <div className="comment-edit-box">
                        <input
                          value={editingCommentText}
                          onChange={(e) => setEditingCommentText(e.target.value)}
                        />
                        <div className="comment-edit-actions">
                          <button type="button" onClick={() => updateComment(comment.id)}>
                            Save
                          </button>
                          <button
                            type="button"
                            className="light"
                            onClick={() => {
                              setEditingCommentId(null);
                              setEditingCommentText("");
                            }}
                          >
                            Cancel
                          </button>
                        </div>
                      </div>
                    ) : (
                      <div className="comment-text">{comment.content}</div>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>

          <div className="comment-compose">
            <input
              type="text"
              placeholder="Write a comment..."
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
            />
            <button type="button" onClick={submitComment} disabled={commentLoading}>
              {commentLoading ? "Posting..." : "Send"}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}