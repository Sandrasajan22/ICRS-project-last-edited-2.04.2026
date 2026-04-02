import { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/signup.css";
import Navbar from "../Components/Navbar.jsx";

function Signup() {
  const navigate = useNavigate();

  const [fname, setFname] = useState("");
  const [lname, setLname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  // ✅ Password pattern (example: strong password)
  // - min 8 chars
  // - at least 1 uppercase
  // - at least 1 lowercase
  // - at least 1 number
  // - at least 1 special char
  const passwordPattern =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&()[\]{}#^~_\-+=|\\:;'",.<>/]).{8,}$/;

  const passwordError = useMemo(() => {
    if (!password) return "";
    if (!passwordPattern.test(password)) {
      return "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.";
    }
    return "";
  }, [password]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    // ✅ pattern check (no API logic change)
    if (!passwordPattern.test(password)) {
      alert(
        "Invalid password.\n\nPassword must be at least 8 characters and include:\n- Uppercase\n- Lowercase\n- Number\n- Special character"
      );
      return;
    }

    const signupData = { fname, lname, email, password, role };

    try {
      const response = await fetch("http://localhost:8080/api/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(signupData),
      });

      if (!response.ok) {
        throw new Error(await response.text());
      }

      alert("Signup successful!");
      navigate("/login");
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <div className="app-container">
      <Navbar />

      <div className="signup-container">
        <div className="signup-box">
          <div className="signup-header">
            <h2>Create Account</h2>
            <p className="subtitle">Create your account in a few steps.</p>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="two-col">
              <div className="field">
                <label>First Name</label>
                <input
                  type="text"
                  placeholder="Enter first name"
                  value={fname}
                  onChange={(e) => setFname(e.target.value)}
                  required
                />
              </div>

              <div className="field">
                <label>Last Name</label>
                <input
                  type="text"
                  placeholder="Enter last name"
                  value={lname}
                  onChange={(e) => setLname(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="field">
              <label>Email</label>
              <input
                type="email"
                placeholder="example@gmail.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="field">
              <label>Password</label>
              <div className={`password-field ${passwordError ? "has-error" : ""}`}>
                <input
                  type={showPassword ? "text" : "password"}
                  placeholder="Create a strong password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  pattern={passwordPattern.source}
                  title="Min 8 chars, include uppercase, lowercase, number, special character."
                  aria-invalid={!!passwordError}
                />

                <button
                  type="button"
                  className="eye-btn"
                  onClick={() => setShowPassword((s) => !s)}
                  aria-label={showPassword ? "Hide password" : "Show password"}
                >
                  {showPassword ? "Hide" : "Show"}
                </button>
              </div>

              {/* ✅ live hint (does not change your logic) */}
              <p className={`helper ${passwordError ? "error" : ""}`}>
                {passwordError ||
                  "Use 8+ characters with a mix of uppercase, lowercase, number, and special character."}
              </p>
            </div>

            <div className="field">
              <label>Role</label>
              <select value={role} onChange={(e) => setRole(e.target.value)} required>
                <option value="">Select Role</option>
                <option value="student">Student</option>
                <option value="job_seeker">Job Seeker</option>
                <option value="mentor">Mentor</option>
                <option value="employer">Employer</option>
                <option value="trainer">Trainer</option>
                <option value="institution">Training Institution</option>
              </select>
            </div>

            <button type="submit" className="primary-btn" disabled={!!passwordError}>
              Sign Up
            </button>
          </form>

          <div className="divider" />

          <p className="bottom-text">
            Already have an account? <Link to="/login">Login</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Signup;
