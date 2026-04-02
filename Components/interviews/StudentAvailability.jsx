import { useEffect, useState } from "react";
import { getAvailableSlots, bookSlot } from "./interviewapi";
import { useNavigate } from "react-router-dom";
import "./interview.css";

export default function StudentAvailability() {
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const studentId = localStorage.getItem("userId");
  const role = localStorage.getItem("role"); // "student" | "job_seeker"
  const navigate = useNavigate();

  // Determine base path based on role
  const basePath = role === "job_seeker" ? "/jobseeker" : "/student";

  useEffect(() => {
    if (!studentId) {
      setError("Not logged in.");
      setLoading(false);
      return;
    }
    getAvailableSlots(studentId)
      .then(res => setSlots(res.data))
      .catch(() => setError("Failed to load slots. Please try again."))
      .finally(() => setLoading(false));
  }, [studentId]);

  const handleBook = async (slot) => {
    if (!window.confirm("Confirm booking?")) return;

    try {
      const res = await bookSlot({
        studentId,
        availabilityId: slot.availabilityId,
        time: slot.startTime
      });

      navigate(`${basePath}/booked`, {
        state: {
          bookingId: res.data.id,
          amount: slot.fee,
          mentorName: slot.mentorName,
          date: slot.date,
          time: slot.startTime
        }
      });
    } catch (err) {
      alert(err?.response?.data?.message || "Booking failed. Please try again.");
    }
  };

  return (
    <div className="itv-container">
      <div className="itv-header-flex">
        <h2>Available Sessions</h2>
      </div>

      {loading && <p className="itv-info">🔍 Finding available mentors for you...</p>}
      {error && <p className="itv-info" style={{ color: "hsl(var(--itv-danger))", background: "#fee2e2" }}>{error}</p>}

      {!loading && !error && slots.length === 0 && (
        <div className="empty-state">
          <p>No interview slots are currently available. Check back soon!</p>
        </div>
      )}

      <div className="itv-grid">
        {slots.map(s => (
          <div className={`itv-card ${s.available ? "itv-available" : "itv-full"}`} key={`${s.availabilityId}-${s.startTime}`}>
            <div className="itv-card-header">
              <span className="itv-status-badge" style={{ backgroundColor: s.available ? "hsl(var(--itv-success))" : "hsl(var(--itv-danger))" }}>
                {s.available ? "Available" : "Full"}
              </span>
              <span className="itv-date-tag">📅 {s.date}</span>
            </div>
            
            <div className="itv-card-body">
              <div className="itv-mentor-info">
                <h3>{s.mentorName || "Expert Mentor"}</h3>
              </div>
              
              <div className="itv-info-container">
                <div className="itv-info-row">⏰ {s.startTime} - {s.endTime}</div>
                <div className="itv-info-row">🏷️ {s.interviewType}</div>
              </div>
              
              <div className="itv-price">₹{s.fee}</div>
            </div>

            <div className="card-actions">
              <button
                className="btn btn-primary"
                disabled={!s.available}
                onClick={() => handleBook(s)}
              >
                {s.available ? "Book Session" : "Full"}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}