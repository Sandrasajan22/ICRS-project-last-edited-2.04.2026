import { useEffect, useMemo, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/adminnotif.css";

// Admin notifications use recipientId = 0
const ADMIN_RECIPIENT_ID = 0;
const API_BASE = "http://localhost:8080";

export default function AdminNotification() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [markingAll, setMarkingAll] = useState(false);
  const navigate = useNavigate();

  const fetchNotifications = useCallback(async () => {
    try {
      setLoading(true);
      // ✅ Use the new centralized notification endpoint for admin (recipientId = 0)
      const res = await fetch(`${API_BASE}/api/notifications?userId=${ADMIN_RECIPIENT_ID}`);

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
  }, []);

  useEffect(() => {
    fetchNotifications();
    const t = setInterval(fetchNotifications, 20000); // reduced frequency
    return () => clearInterval(t);
  }, [fetchNotifications]);

  // Unread count
  const unreadCount = notifications.filter(n => !n.readStatus).length;

  const summaryText = useMemo(() => {
    if (unreadCount === 0) return "No unread notifications";
    if (unreadCount === 1) return "1 unread notification";
    return `${unreadCount} unread notifications`;
  }, [unreadCount]);

  const handleOpen = async (n) => {
    if (!n.readStatus) {
      // ✅ Optimistic UI
      setNotifications(prev => prev.map(x => x.id === n.id ? { ...x, readStatus: true } : x));
      try {
        await fetch(`${API_BASE}/api/notifications/${n.id}/read`, { method: "POST" });
      } catch (err) {
        /* ignore */
      }
    }
    navigate(n.redirectPath || "/admin/verification");
  };

  const markAllRead = async () => {
    const unreadItems = notifications.filter(n => !n.readStatus);
    if (unreadItems.length === 0) return;

    setMarkingAll(true);
    setNotifications(prev => prev.map(n => ({ ...n, readStatus: true })));

    try {
      await Promise.allSettled(
        unreadItems.map(n =>
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
    if (!type) return "General";
    const t = String(type).toLowerCase();

    if (t.includes("verification")) return "Verification";
    if (t.includes("complaint")) return "Complaint";
    if (t.includes("user")) return "User";

    return String(type)
      .replace(/[_-]/g, " ")
      .replace(/\b\w/g, c => c.toUpperCase());
  };

  return (
    <div className="notif-page">
      <div className="notif-head">
        <div>
          <h2>Notifications</h2>
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
              <th>Type</th>
              <th>Time</th>
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
                  No notifications
                </td>
              </tr>
            ) : (
              notifications.map((n, index) => (
                <tr key={n.id} className="notif-row" style={{ opacity: n.readStatus ? 0.6 : 1 }}>
                  <td>{index + 1}</td>

                  <td>
                    <div className="notif-msg">
                      <strong>{n.message}</strong>
                      <div className="notif-sub">Click open to view details</div>
                    </div>
                  </td>

                  <td>
                    <span className="notif-type">{formatType(n.type)}</span>
                  </td>

                  <td>
                    <span className="notif-time">
                      {n.createdAt ? String(n.createdAt).replace("T", " ").slice(0, 16) : "-"}
                    </span>
                  </td>

                  <td>
                     <span style={{ fontWeight: 600, color: n.readStatus ? '#6b7280' : '#2563eb' }}>
                      {n.readStatus ? 'Read' : 'New'}
                     </span>
                  </td>

                  <td>
                    <button className="btn-open" type="button" onClick={() => handleOpen(n)}>
                      Open
                    </button>
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
