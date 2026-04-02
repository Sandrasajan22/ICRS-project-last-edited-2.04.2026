import { useState } from "react";
import { submitFeedback } from "./interviewapi";
import "./feedback.css";

export default function MentorFeedbackForm({ booking, onClose, onSuccess }) {
  const [form, setForm] = useState({
    communicationScore: 5,
    technicalScore: 5,
    confidenceScore: 5,
    overallRemarks: "",
    recommendedForFeed: true,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  if (!booking) {
    return (
      <div className="fb-overlay">
        <div className="fb-modal" style={{ textAlign: 'center' }}>
          <h2>No Interview Context</h2>
          <p>Please access feedback through the <b>Interview Bookings</b> list.</p>
          <button className="fb-btn primary" onClick={() => window.history.back()}>Go Back</button>
        </div>
      </div>
    );
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const payload = {
        booking: { id: booking.id },
        ...form
      };
      await submitFeedback(payload);
      if (onSuccess) onSuccess();
      if (onClose) onClose();
    } catch (err) {
      setError(err.response?.data || "Failed to submit feedback");
    } finally {
      setLoading(false);
    }
  };

  const RatingRow = ({ label, value, field }) => (
    <div className="fb-field">
      <label>{label}</label>
      <div className="fb-stars">
        {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((num) => (
          <div
            key={num}
            className={`fb-star ${value === num ? "active" : ""}`}
            onClick={() => setForm({ ...form, [field]: num })}
          >
            {num}
          </div>
        ))}
      </div>
    </div>
  );

  return (
    <div className="fb-overlay">
      <div className="fb-modal">
        <div className="fb-header">
          <h2>Interview Review</h2>
          <p>Provide detailed feedback for <b>{booking.studentName}</b></p>
        </div>

        {error && <div className="sec-toast bad">{error}</div>}

        <form onSubmit={handleSubmit} className="fb-metrics">
          <RatingRow 
            label="Communication Skills" 
            value={form.communicationScore} 
            field="communicationScore" 
          />
          <RatingRow 
            label="Technical Proficiency" 
            value={form.technicalScore} 
            field="technicalScore" 
          />
          <RatingRow 
            label="Confidence Level" 
            value={form.confidenceScore} 
            field="confidenceScore" 
          />

          <div className="fb-field">
            <label>Overall Remarks</label>
            <textarea
              className="fb-area"
              placeholder="Strengths, weaknesses, and improvement areas..."
              value={form.overallRemarks}
              onChange={(e) => setForm({ ...form, overallRemarks: e.target.value })}
              required
            />
          </div>

          <div className="fb-toggle">
            <div className="fb-toggle-text">
                <div className="fb-toggle-title">Feed Recommendation</div>
                <div className="fb-toggle-hint">Help our algorithm personalize suggestions based on this session.</div>
            </div>
            <label className="sec-switch">
              <input
                type="checkbox"
                checked={form.recommendedForFeed}
                onChange={() => setForm({ ...form, recommendedForFeed: !form.recommendedForFeed })}
              />
              <span className="sec-slider"></span>
            </label>
          </div>

          <div className="fb-actions">
            <button 
              type="button" 
              className="fb-btn secondary" 
              onClick={onClose}
              disabled={loading}
            >
              Cancel
            </button>
            <button 
              type="submit" 
              className="fb-btn primary"
              disabled={loading}
            >
              {loading ? "Submitting..." : "Submit Review"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
