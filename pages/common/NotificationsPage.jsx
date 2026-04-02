import { useEffect, useMemo, useState, useCallback } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "../../styles/adminnotif.css"; // Reusing the premium admin notifications styling

const API_BASE = "http://localhost:8080";

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [markingAll, setMarkingAll] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // Determine base path for proper deep linking (e.g. "student", "mentor")
  const basePath = location.pathname.split("/")[1] || "";


  // Get current user ID from local storage
  const userId = localStorage.getItem("userId");

  const fetchNotifications = useCallback(async () => {
    if (!userId) return;

    try {
      setLoading(true);
      const res = await fetch(`${API_BASE}/api/notifications?userId=${userId}`);

      if (!res.ok) {
        setNotifications([]);
        return;
      }

      const data = await res.json();
      setNotifications(Array.isArray(data) ? data : []);
    } catch (err) {
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchNotifications();
    const t = setInterval(fetchNotifications, 20000); // poll every 20s
    return () => clearInterval(t);
  }, [fetchNotifications]);

  const unreadCount = notifications.filter((n) => !n.readStatus).length;

  const summaryText = useMemo(() => {
    if (unreadCount === 0) return "No unread notifications";
    if (unreadCount === 1) return "1 unread notification";
    return `${unreadCount} unread notifications`;
  }, [unreadCount]);

  const handleOpen = async (n) => {
    if (!n.readStatus) {
      // Optimistic UI update
      setNotifications((prev) =>
        prev.map((x) => (x.id === n.id ? { ...x, readStatus: true } : x))
      );
      try {
        await fetch(`${API_BASE}/api/notifications/${n.id}/read`, {
          method: "POST",
        });
      } catch (err) {
        /* ignore */
      }
    }
    
    if (n.redirectPath) {
      let fullPath = n.redirectPath;
      if (basePath && !fullPath.startsWith(`/${basePath}/`) && fullPath !== `/${basePath}`) {
         fullPath = fullPath.startsWith("/") 
           ? `/${basePath}${fullPath}` 
           : `/${basePath}/${fullPath}`;
      }
      navigate(fullPath);
    }
  };


  const markAllRead = async () => {
    const unreadItems = notifications.filter((n) => !n.readStatus);
    if (unreadItems.length === 0) return;

    setMarkingAll(true);
    // Optimistic UI clear
    setNotifications((prev) => prev.map((n) => ({ ...n, readStatus: true })));

    try {
      await Promise.allSettled(
        unreadItems.map((n) =>
          fetch(`${API_BASE}/api/notifications/${n.id}/read`, { method: "POST" })
        )
      );
    } catch (err) {
      console.error(err);
    } finally {
      setMarkingAll(false);
    }
  };

  const formatType = (type) => {
    if (!type) return "Alert";
    const t = String(type).toUpperCase();
    if (t === "LIKE") return "New Like";
    if (t === "COMMENT") return "New Comment";
    if (t === "COMPLAINT_RESOLVED") return "Issue Resolved";
    if (t === "VERIFICATION") return "Verification Alert";

    return t.replace(/[_-]/g, " ").replace(/\b\w/g, (c) => c.toUpperCase());
  };

  return (
    <div className="notif-page">
      <div className="notif-head">
        <div>
          <h2>Your Notifications</h2>
          <p className="notif-summary">{summaryText}</p>
        </div>

        <div className="notif-actions">
          <button className="btn-refresh" onClick={fetchNotifications} type="button">
            Refresh
          </button>

          <button
            className="btn-markall"
            onClick={markAllRead}
            type="button"
            disabled={unreadCount === 0 || markingAll}
            title={unreadCount === 0 ? "No unread notifications" : "Mark all as read"}
          >
            {markingAll ? "Marking..." : "Mark all read"}
          </button>
        </div>
      </div>

      <div className="notif-card">
        <table className="notif-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Message</th>
              <th>Category</th>
              <th>Date</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {loading && notifications.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: "center" }}>
                  Loading...
                </td>
              </tr>
            ) : notifications.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: "center" }}>
                  No notifications found
                </td>
              </tr>
            ) : (
              notifications.map((n, index) => (
                <tr
                  key={n.id}
                  className="notif-row"
                  style={{ opacity: n.readStatus ? 0.6 : 1 }}
                >
                  <td>{index + 1}</td>

                  <td>
                    <div className="notif-msg">
                      <strong>{n.message}</strong>
                      {n.redirectPath && (
                        <div className="notif-sub">Click open to view details</div>
                      )}
                    </div>
                  </td>

                  <td>
                    <span className="notif-type">{formatType(n.type)}</span>
                  </td>

                  <td>
                    <span className="notif-time">
                      {n.createdAt
                        ? String(n.createdAt).replace("T", " ").slice(0, 16)
                        : "-"}
                    </span>
                  </td>

                  <td>
                    <span
                      style={{
                        fontWeight: 600,
                        color: n.readStatus ? "#6b7280" : "#2563eb",
                      }}
                    >
                      {n.readStatus ? "Read" : "New"}
                    </span>
                  </td>

                  <td>
                    {n.redirectPath ? (
                      <button
                        className="btn-open"
                        type="button"
                        onClick={() => handleOpen(n)}
                      >
                        Open
                      </button>
                    ) : (
                      <span style={{ color: "#9ca3af", fontSize: "14px" }}>-</span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
