import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/pendingverification.css";

const BASE_URL = "http://localhost:8080";

const normalizeStatus = (s) =>
  typeof s === "string" ? s.trim().toUpperCase() : null;

export default function PendingVerification() {
  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");

  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState(null);
  const [error, setError] = useState("");

  const fetchStatus = async (signal) => {
    const res = await fetch(`${BASE_URL}/api/verification/status/${userId}`, { signal });
    if (!res.ok) throw new Error(`Server returned ${res.status}`);
    const data = await res.json();
    const st = normalizeStatus(data?.status);
    if (!st) throw new Error("Status missing in response");
    return st;
  };

  useEffect(() => {
    if (!userId) {
      navigate("/login", { replace: true });
      return;
    }

    let isMounted = true;
    let inFlight = false;

    const loadStatus = async () => {
      if (inFlight) return;
      inFlight = true;

      const controller = new AbortController();

      try {
        setLoading(true);
        setError("");

        const currentStatus = await fetchStatus(controller.signal);
        if (!isMounted) return;

        setStatus(currentStatus);

        // ✅ IMPORTANT: redirect if NOT_SUBMITTED
        if (currentStatus === "NOT_SUBMITTED") {
          navigate("/verify-account", { replace: true });
          return;
        }

        if (currentStatus === "APPROVED") {
          navigate("/dashboard", { replace: true });
          return;
        }

        if (currentStatus === "REJECTED" || currentStatus === "BLOCKED") {
          alert("Your verification was rejected. Your account is blocked.");
          localStorage.clear();
          navigate("/login", { replace: true });
          return;
        }
      } catch (err) {
        if (err.name !== "AbortError" && isMounted) {
          setError(err.message || "Failed to load status");
        }
      } finally {
        if (isMounted) setLoading(false);
        inFlight = false;
      }
    };

    loadStatus();
    const interval = setInterval(loadStatus, 10000);

    return () => {
      isMounted = false;
      clearInterval(interval);
    };
  }, [userId, navigate]);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login", { replace: true });
  };

  const showStatus = useMemo(() => status ?? "UNKNOWN", [status]);

  const isSubmitted = showStatus !== "NOT_SUBMITTED" && showStatus !== "UNKNOWN";
  const isUnderReview = showStatus === "PENDING" || showStatus === "UNDER_REVIEW";

  if (loading) {
    return (
      <div className="pv-page">
        <div className="pv-card">
          <div className="pv-loading">
            <div className="pv-spinner" />
            <div>
              <div className="pv-title">Checking status</div>
              <div className="pv-subtitle">Please wait a moment…</div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="pv-page">
        <div className="pv-card">
          <div className="pv-header">
            <div>
              <div className="pv-title">Unable to load status</div>
              <div className="pv-subtitle">We couldn’t reach the server.</div>
            </div>
            <span className="pv-pill pv-pill-error">Error</span>
          </div>

          <div className="pv-alert pv-alert-error">{error}</div>

          <div className="pv-actions">
            <button className="pv-btn pv-btn-secondary" onClick={() => window.location.reload()}>
              Retry
            </button>
            <button className="pv-btn pv-btn-primary" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </div>
      </div>
    );
  }

  // ✅ If status unknown, show safe info
  if (showStatus === "UNKNOWN") {
    return (
      <div className="pv-page">
        <div className="pv-card">
          <div className="pv-header">
            <div>
              <div className="pv-title">Verification status unknown</div>
              <div className="pv-subtitle">Please refresh or try again.</div>
            </div>
            <span className="pv-pill pv-pill-pending">UNKNOWN</span>
          </div>

          <div className="pv-actions">
            <button className="pv-btn pv-btn-secondary" onClick={() => window.location.reload()}>
              Refresh
            </button>
            <button className="pv-btn pv-btn-primary" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="pv-page">
      <div className="pv-card">
        <div className="pv-header">
          <div className="pv-headerLeft">
            <div className="pv-badge">Verification</div>
            <div>
              <div className="pv-title">Verification in progress</div>
              <div className="pv-subtitle">
                Your request is under admin review. You will get access once approved!
              </div>
            </div>
          </div>

          <span className="pv-pill pv-pill-pending">{showStatus}</span>
        </div>

        <div className="pv-stepper">
          <div className={`pv-step ${isSubmitted ? "active" : ""}`}>
            <div className="pv-dot" />
            <div className="pv-stepText">
              <div className="pv-stepTitle">Submitted</div>
              <div className="pv-stepSub">
                {isSubmitted ? "Documents uploaded" : "Upload required"}
              </div>
            </div>
          </div>

          <div className="pv-line" />

          <div className={`pv-step ${isUnderReview ? "active" : ""}`}>
            <div className="pv-dot" />
            <div className="pv-stepText">
              <div className="pv-stepTitle">Under review</div>
              <div className="pv-stepSub">Admin verification</div>
            </div>
          </div>

          <div className="pv-line" />

          <div className="pv-step">
            <div className="pv-dot" />
            <div className="pv-stepText">
              <div className="pv-stepTitle">Approved</div>
              <div className="pv-stepSub">Access granted</div>
            </div>
          </div>
        </div>

        <div className="pv-info">
          <div className="pv-infoTitle">What happens next?</div>
          <ul className="pv-infoList">
            <li>We review your ID and certificates.</li>
            <li>If approved, you will be redirected automatically.</li>
            <li>If rejected, your account may be blocked for security.</li>
          </ul>
        </div>

        <div className="pv-actions">
          <button className="pv-btn pv-btn-secondary" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>
    </div>
  );
}
