import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { payBooking } from "./interviewapi";
import "./interview.css";


export default function StudentBooked() {
  const { state } = useLocation();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);

  if (!state) return <div className="container">No booking found.</div>;

  const { bookingId, amount, mentorName, date, time } = state;

  const handlePay = async () => {
    if (!window.confirm("Confirm secure payment?")) return;

    setLoading(true);

    try {
      await payBooking(bookingId);
      alert("✅ Payment Successful! Your session is confirmed.");

      const role = localStorage.getItem("role");
      const basePath = role === "job_seeker" ? "/jobseeker" : "/student";
      navigate(`${basePath}/bookings`);
    } catch {
      alert("Payment failed. Please try again or contact support.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="itv-container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div className="itv-payment-card">
        <h2 style={{ fontSize: '2rem', fontWeight: 800, marginBottom: '2rem' }}>Secure Payment</h2>
        
        <div className="itv-payment-receipt">
          <p style={{ color: 'hsl(var(--itv-text-muted))', fontWeight: 600, fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
            Interview Session with
          </p>
          <h3 style={{ margin: "1rem 0", fontSize: '1.8rem', color: 'hsl(var(--itv-text-main))' }}>{mentorName}</h3>
          
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', margin: '1.5rem 0' }}>
            <span className="itv-date-tag">📅 {date}</span>
            <span className="itv-date-tag">⏰ {time}</span>
          </div>
          
          <div style={{ borderTop: '2px dashed #cbd5e1', margin: '2rem 0' }} />
          
          <p style={{ fontSize: '0.8rem', textTransform: 'uppercase', letterSpacing: '0.1em', color: 'hsl(var(--itv-text-muted))', fontWeight: 700 }}>
            Total Amount
          </p>
          <div className="itv-payment-amount">₹{amount}</div>
        </div>

        <div style={{ width: '100%', display: "flex", flexDirection: "column", gap: "1rem" }}>
          <button 
            className="itv-btn itv-btn-primary" 
            style={{ width: '100%', padding: '1.25rem', fontSize: '1.1rem' }}
            onClick={handlePay} 
            disabled={loading}
          >
            {loading ? "💳 Processing Securely..." : "Confirm & Pay Now"}
          </button>

          <button 
            className="itv-btn" 
            style={{ width: '100%', background: 'transparent', color: 'hsl(var(--itv-text-muted))', fontWeight: 600 }}
            onClick={() => navigate(-1)}
          >
            Cancel Transaction
          </button>
        </div>

        <p style={{ marginTop: '2rem', fontSize: '0.75rem', color: 'hsl(var(--text-muted))', textAlign: 'center' }}>
          🔒 Your payment information is encrypted and secure.
        </p>
      </div>
    </div>
  );
}