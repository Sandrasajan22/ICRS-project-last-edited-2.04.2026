import { useState, useEffect, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { FaBell, FaHeart, FaComment, FaExclamationCircle, FaCheckCircle, FaShieldAlt, FaUser, FaTimes } from "react-icons/fa";
import "../styles/notifications.css";

const API_BASE = "http://localhost:8080";

function timeAgo(dateStr) {
  const date = new Date(dateStr);
  const now = new Date();
  const diffMs = now - date;
  const diffSecs = Math.floor(diffMs / 1000);
  const diffMins = Math.floor(diffSecs / 60);
  const diffHrs = Math.floor(diffMins / 60);
  const diffDays = Math.floor(diffHrs / 24);

  if (diffSecs < 60) return "just now";
  if (diffMins < 60) return `${diffMins}m ago`;
  if (diffHrs < 24) return `${diffHrs}h ago`;
  if (diffDays === 1) return "yesterday";
  return `${diffDays}d ago`;
}

function getIcon(type) {
  switch (type) {
    case "LIKE": return <FaHeart className="nc-icon nc-icon--like" />;
    case "COMMENT": return <FaComment className="nc-icon nc-icon--comment" />;
    case "COMPLAINT": return <FaExclamationCircle className="nc-icon nc-icon--complaint" />;
    case "COMPLAINT_RESOLVED": return <FaCheckCircle className="nc-icon nc-icon--resolved" />;
    case "VERIFICATION": return <FaShieldAlt className="nc-icon nc-icon--verify" />;
    case "FOLLOW": return <FaUser className="nc-icon nc-icon--follow" />;
    default: return <FaBell className="nc-icon nc-icon--default" />;
  }
}

export default function NotificationCenter({ userId }) {
  const [open, setOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unread, setUnread] = useState(0);
  const [loading, setLoading] = useState(false);
  const dropRef = useRef(null);
  const navigate = useNavigate();
  const location = useLocation();

  // Determine base path for "View All" (e.g. "student", "admin", "mentor")
  const basePath = location.pathname.split("/")[1] || "";


  // Fetch unread count (polling every 30s)
  useEffect(() => {
    if (!userId) return;
    const fetchCount = async () => {
      try {
        const res = await fetch(`${API_BASE}/api/notifications/unread-count?userId=${userId}`);
        if (res.ok) {
          const data = await res.json();
          setUnread(Number(data.count || 0));
        }
      } catch { /* ignore */ }
    };
    fetchCount();
    const interval = setInterval(fetchCount, 30000);
    return () => clearInterval(interval);
  }, [userId]);

  // Fetch notifications when dropdown opens
  useEffect(() => {
    if (!open || !userId) return;
    setLoading(true);
    fetch(`${API_BASE}/api/notifications?userId=${userId}`)
      .then(r => r.json())
      .then(data => setNotifications(Array.isArray(data) ? data : []))
      .catch(() => setNotifications([]))
      .finally(() => setLoading(false));
  }, [open, userId]);

  // Close on outside click
  useEffect(() => {
    const handler = (e) => {
      if (dropRef.current && !dropRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  const handleClick = async (notif) => {
    // Mark as read
    if (!notif.readStatus) {
      try {
        await fetch(`${API_BASE}/api/notifications/${notif.id}/read`, { method: "POST" });
        setNotifications(prev => prev.map(n => n.id === notif.id ? { ...n, readStatus: true } : n));
        setUnread(prev => Math.max(0, prev - 1));
      } catch { /* ignore */ }
    }
    setOpen(false);
    if (notif.redirectPath) {
      let fullPath = notif.redirectPath;
      if (basePath && !fullPath.startsWith(`/${basePath}/`) && fullPath !== `/${basePath}`) {
         fullPath = fullPath.startsWith("/") 
           ? `/${basePath}${fullPath}` 
           : `/${basePath}/${fullPath}`;
      }
      navigate(fullPath);
    }
  };

  const markAllRead = async () => {
    const unreadItems = notifications.filter(n => !n.readStatus);
    await Promise.all(
      unreadItems.map(n =>
        fetch(`${API_BASE}/api/notifications/${n.id}/read`, { method: "POST" }).catch(() => {})
      )
    );
    setNotifications(prev => prev.map(n => ({ ...n, readStatus: true })));
    setUnread(0);
  };

  return (
    <div className="nc-wrap" ref={dropRef}>
      <button
        className="nc-bell-btn"
        onClick={() => setOpen(o => !o)}
        aria-label="Notifications"
        type="button"
        id="notification-bell-btn"
      >
        <FaBell />
        {unread > 0 && (
          <span className="nc-badge">{unread > 99 ? "99+" : unread}</span>
        )}
      </button>

      {open && (
        <div className="nc-dropdown">
          <div className="nc-header">
            <span className="nc-header-title">Notifications</span>
            <div className="nc-header-actions">
              {unread > 0 && (
                <button className="nc-mark-all" onClick={markAllRead} type="button">
                  Mark all read
                </button>
              )}
              <button className="nc-close-btn" onClick={() => setOpen(false)} type="button">
                <FaTimes />
              </button>
            </div>
          </div>

          <div className="nc-body">
            {loading && (
              <div className="nc-empty">
                <div className="nc-spinner" />
                <p>Loading…</p>
              </div>
            )}

            {!loading && notifications.length === 0 && (
              <div className="nc-empty">
                <FaBell className="nc-empty-icon" />
                <p>No notifications yet</p>
              </div>
            )}

            {!loading && notifications.map(notif => (
              <div
                key={notif.id}
                className={`nc-item${notif.readStatus ? "" : " nc-item--unread"}`}
                onClick={() => handleClick(notif)}
                role="button"
                tabIndex={0}
                onKeyDown={e => e.key === "Enter" && handleClick(notif)}
                id={`notification-item-${notif.id}`}
              >
                <div className="nc-item-icon">{getIcon(notif.type)}</div>
                <div className="nc-item-body">
                  <p className="nc-item-msg">{notif.message}</p>
                  <span className="nc-item-time">{timeAgo(notif.createdAt)}</span>
                </div>
                <div className="nc-dot" />
              </div>
            ))}

            <div className="nc-footer" style={{ textAlign: "center", padding: "10px", borderTop: "1px solid #f1f5f9" }}>
              <button 
                type="button" 
                onClick={() => { setOpen(false); navigate(`/${basePath}/notifications`); }}
                style={{ background: "none", border: "none", color: "#2563eb", fontWeight: "600", cursor: "pointer", fontSize: "14px" }}
              >
                View Full History
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
