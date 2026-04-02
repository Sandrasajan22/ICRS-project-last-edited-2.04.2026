import { useEffect, useState } from "react";
import { getStudentBookings } from "./interviewapi";
import { getUserId } from "../../utils/auth";
import "./interview.css";

export default function StudentBookings() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const studentId = getUserId();

  useEffect(() => {
    if (!studentId) {
      setError("User ID not found. Please log in.");
      setLoading(false);
      return;
    }

    getStudentBookings(studentId)
      .then((res) => {
        setBookings(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError("Unable to load bookings. Please check your connection.");
        setLoading(false);
      });
  }, [studentId]);

  const handleJoin = (link) => {
    if (!link) {
      alert("Meeting link is not available yet.");
      return;
    }
    window.open(link, "_blank");
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "PAID": return "#27ae60";
      case "PENDING": return "#f39c12";
      case "CANCELLED": return "#e74c3c";
      default: return "#7f8c8d";
    }
  };

  if (loading) return <div className="itv-container"><p className="itv-info">Loading your bookings...</p></div>;
  if (error) return <div className="itv-container"><p className="itv-info" style={{ color: "red" }}>{error}</p></div>;

  return (
    <div className="itv-container">
      <div className="itv-header-flex">
        <h2>My Interview Bookings</h2>
      </div>

      {bookings.length === 0 ? (
        <p className="itv-info">You haven't booked any interviews yet.</p>
      ) : (
        <div className="itv-grid">
          {bookings.map((b) => (
            <div className={`itv-card ${b.paymentStatus === "PAID" ? "itv-paid" : "itv-pending"}`} key={b.id}>
              <div className="itv-card-header">
                <span className="itv-status-badge" style={{ backgroundColor: getStatusColor(b.paymentStatus) }}>
                  {b.paymentStatus}
                </span>
                <span className="itv-date-tag">📅 {b.date}</span>
              </div>
              
              <div className="itv-card-body">
                <div className="itv-mentor-info">
                  <h3>{b.mentorName || "Expert Mentor"}</h3>
                </div>

                <div className="itv-info-container">
                  <div className="itv-info-row">⏰ {b.time}</div>
                  <div className="itv-info-row">🏷️ {b.interviewType || "Mock Interview"}</div>
                </div>

                <div className="itv-price">₹{b.fee || b.paymentAmount}</div>
              </div>

              <div className="itv-card-actions">
                {b.paymentStatus === "PAID" && (() => {
                  const interviewDate = new Date(`${b.date}T${b.time}`);
                  const isPast = interviewDate < new Date();

                  if (isPast) {
                    return <span className="itv-status-badge" style={{backgroundColor: "#7f8c8d"}}>FINISHED</span>;
                  }

                  return (
                    <button className="itv-btn itv-btn-primary" onClick={() => handleJoin(b.meetingLink)}>
                      Join Meeting
                    </button>
                  );
                })()}
                
                {b.paymentStatus !== "PAID" && (
                  <button className="itv-btn itv-btn-success" disabled>
                    Pending Payment
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
