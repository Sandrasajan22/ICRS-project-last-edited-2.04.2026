import { useState, useEffect, useRef, useCallback } from "react";
import { Outlet, NavLink, useNavigate, useLocation } from "react-router-dom";
import {
  FaBars,
  FaUserCheck,
  FaUsers,
  FaFileAlt,
  FaChartLine,
  FaExclamationCircle,
  FaSignOutAlt,
  FaFileUpload
} from "react-icons/fa";
import "../../styles/adminlay.css";
import NotificationCenter from "../../Components/NotificationCenter";

// Admin notifications use recipientId = 0 (broadcast to all admins)
const ADMIN_RECIPIENT_ID = 0;

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const [verificationCount, setVerificationCount] = useState(0);

  const navigate = useNavigate();
  const location = useLocation();

  // ===== Fetch verification count =====
  const fetchVerificationCount = useCallback(async () => {
    try {
      const res = await fetch("http://localhost:8080/api/admin/verification/count");
      const data = await res.json();
      setVerificationCount(typeof data === "number" ? data : data?.count || 0);
    } catch {
      setVerificationCount(0);
    }
  }, []);

  useEffect(() => {
    fetchVerificationCount();
    const t = setInterval(fetchVerificationCount, 30000);
    return () => clearInterval(t);
  }, [fetchVerificationCount]);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <div className="admin-layout">
      <aside className={`admin-sidebar ${collapsed ? "collapsed" : ""}`}>
        <div className="sidebar-brand">
          <span className="logo">{collapsed ? "AP" : "Admin Panel"}</span>
          <button
            type="button"
            className="toggle-btn"
            onClick={() => setCollapsed((p) => !p)}
            aria-label="Toggle sidebar"
            title="Toggle sidebar"
            style={{ background: "transparent", border: "none", color: "inherit" }}
          >
            <FaBars />
          </button>
        </div>

        <nav className="sidebar-nav">
          <NavLink
            to="/admin/verification"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <FaUserCheck />
            <span className="nav-text">
              Verifications
              {verificationCount > 0 && (
                <span className="badge">{verificationCount}</span>
              )}
            </span>
          </NavLink>

          <NavLink
            to="/admin/users"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <FaUsers />
            <span className="nav-text">Users</span>
          </NavLink>

          <NavLink
            to="/admin/notifications"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <FaFileAlt />
            <span className="nav-text">Notifications</span>
          </NavLink>

          <NavLink
            to="/admin/analytics"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <FaChartLine />
            <span className="nav-text">Analytics</span>
          </NavLink>

          <NavLink
            to="/admin/adminimport"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <FaFileUpload />
            <span className="nav-text">Import Module</span>
          </NavLink>

          <NavLink
            to="/admin/complaints"
            className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}
          >
            <FaExclamationCircle />
            <span className="nav-text">Complaints</span>
          </NavLink>
        </nav>

        <div className="sidebar-footer">
          <button className="logout-btn" onClick={handleLogout} type="button">
            <FaSignOutAlt />
            <span>Logout</span>
          </button>
        </div>
      </aside>

      <main className="admin-main">
        {/* TOP BAR */}
        <div className="admin-topbar">
          <div className="topbar-left" />
          <div className="topbar-right">
            {/* 🔔 Shared NotificationCenter — admin receives all recipientId=0 alerts */}
            <NotificationCenter userId={ADMIN_RECIPIENT_ID} />
          </div>
        </div>

        <Outlet />
      </main>
    </div>
  );
}
