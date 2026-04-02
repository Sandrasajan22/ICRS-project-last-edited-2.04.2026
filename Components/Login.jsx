import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import "../styles/login.css";
import Navbar from "../Components/Navbar.jsx";

const BASE_URL = "http://localhost:8080";

const normalizeStatus = (s) =>
  typeof s === "string" ? s.trim().toUpperCase() : null;

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (submitting) return;

    try {
      setSubmitting(true);

      const res = await fetch(`${BASE_URL}/api/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await res.json();

      if (data.success === false) {
        alert(data.message || "Login failed");
        return;
      }
     // ✅ Store full user
     localStorage.setItem(
       "user",
     JSON.stringify({
      id: data.userId,   // ✅ IMPORTANT FIX
      role: data.role,
      fname: data.fname,
      lname: data.lname
  })
);  

      // ✅ Store user data
      localStorage.setItem("userId", String(data.userId));
      localStorage.setItem("role", String(data.role));

      // ✅ Optional: clear old auth flag until approved
      localStorage.removeItem("isAuthenticated");

      // Blocked
      if (data.blocked === true) {
        alert("Your account is blocked by admin.");
        localStorage.clear();
        return;
      }

      const status = normalizeStatus(data.verificationStatus);

      // Rejected / Blocked
      if (status === "REJECTED" || status === "BLOCKED") {
        alert("Your verification was rejected. Your account is blocked.");
        localStorage.clear();
        return;
      }

      // NOT_SUBMITTED → upload page
      if (status === "NOT_SUBMITTED") {
        navigate("/verify-account", { replace: true });
        return;
      }

      // PENDING / UNDER_REVIEW → pending page
      if (status === "PENDING" || status === "UNDER_REVIEW") {
        navigate("/pending-verification", { replace: true });
        return;
      }

      // APPROVED → allow access
      if (status === "APPROVED") {
        localStorage.setItem("isAuthenticated", "true");

        switch (data.role) {
          case "student":
            navigate("/student", { replace: true });
            break;
          case "job_seeker":
            navigate("/jobseeker", { replace: true });
            break;
          case "institution":
            navigate("/training_institution", { replace: true });
            break;
          case "mentor":
            navigate("/mentor", { replace: true });
            break;
          case "employer":
            navigate("/employer", { replace: true });
            break;
          case "trainer":
            navigate("/trainer", { replace: true });
            break;
          case "admin":
            navigate("/admin", { replace: true });
            break;
          default:
            navigate("/", { replace: true });
        }
        return;
      }

      // Unknown / Missing status
      alert("Unknown account state. Please contact support.");
      localStorage.clear();
    } catch (err) {
      console.error(err);
      alert("Unable to connect to server");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="app-container">
      <Navbar />
      <div className="login-container">
        <div className="login-box">
          <h2>Login to ICRS</h2>

          <form onSubmit={handleSubmit}>
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={submitting}
            />

            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={submitting}
            />

            <button type="submit" disabled={submitting}>
              {submitting ? "Logging in..." : "Login"}
            </button>
          </form>

          <p>
            Don’t have an account? <Link to="/signup">Register here</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;
