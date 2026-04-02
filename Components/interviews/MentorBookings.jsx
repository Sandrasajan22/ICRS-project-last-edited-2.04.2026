import { useEffect, useState } from "react";
import { getMentorBookings, shareLink } from "./interviewapi";
import MentorFeedbackForm from "./MentorFeedbackForm";
import "./interview.css";

export default function MentorBookings() {
  const [booked, setBooked] = useState([]);
  const [reviewBooking, setReviewBooking] = useState(null);
  const mentorId = localStorage.getItem("userId");

  const fetchBookings = () => {
    getMentorBookings(mentorId).then(res => {
      setBooked(res.data);
    });
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  const handleShare = async (id) => {
    const res = await shareLink(id);
    navigator.clipboard.writeText(res.data);
    alert("Link copied!");
  };

  return (
    <div className="container">
  <h2>Interview Bookings</h2>

  <div className="grid">
    {booked.map((b) => (
      <div className="card" key={b.id}>

        {/* 👤 STUDENT */}
        <div className="title">{b.studentName}</div>

        {/* 🎯 TYPE */}
        <div className="info">🧠 {b.interviewType}</div>

        {/* 📅 DATE */}
        <div className="info">📅 {b.date}</div>

        {/* ⏰ TIME */}
        <div className="info">⏰ {b.time}</div>

        {/* 💰 AMOUNT */}
        <div className="price">₹{b.paymentAmount}</div>

        {/* 📊 STATUS */}
        <div className="info">
          Status: {b.paymentStatus}
        </div>

        {/* 🔗 ACTIONS */}
        {b.paymentStatus === "PAID" && (() => {
          const interviewDate = new Date(`${b.date}T${b.time}`);
          const isPast = interviewDate < new Date();

          return (
            <div className="card-actions">
              {!isPast && (
                <a href={b.meetingLink} target="_blank">
                  <button className="btn btn-success">Join</button>
                </a>
              )}

              {b.status === "BOOKED" ? (
                <button 
                  className="btn btn-primary"
                  onClick={() => setReviewBooking(b)}
                >
                  Review Student
                </button>
              ) : (
                <span className="badge badge-completed">FINISHED</span>
              )}
            </div>
          );
        })()}
      </div>
    ))}
  </div>

  {reviewBooking && (
    <MentorFeedbackForm 
      booking={reviewBooking}
      onClose={() => setReviewBooking(null)}
      onSuccess={fetchBookings}
    />
  )}
</div>
  );
}