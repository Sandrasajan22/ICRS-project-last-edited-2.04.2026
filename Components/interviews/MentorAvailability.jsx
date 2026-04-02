import { useEffect, useState } from "react";
import { createSlot, getMySlots } from "./interviewapi";
import "./interview.css";

export default function MentorAvailability() {
  const mentorId = localStorage.getItem("userId");

  const [slots, setSlots] = useState([]);

  const [form, setForm] = useState({
    date: "",
    startTime: "",
    endTime: "",
    duration: 30,
    interviewType: "TECHNICAL",
    fee: 100,
    maxSlots: 1
  });

  useEffect(() => {
    fetchSlots();
  }, []);

  const fetchSlots = () => {
    getMySlots(mentorId).then(res => setSlots(res.data));
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleCreate = async () => {
    if (!form.date || !form.startTime || !form.endTime) {
      return alert("Please fill all required fields");
    }

    try {
      await createSlot({
        ...form,
        mentorId,
        fee: Number(form.fee),
        duration: Number(form.duration),
        maxSlots: Number(form.maxSlots)
      });

      alert("✅ Slot created");

      setForm({
        date: "",
        startTime: "",
        endTime: "",
        duration: 30,
        interviewType: "TECHNICAL",
        fee: 100,
        maxSlots: 1
      });

      fetchSlots();
    } catch (err) {
      alert("Error creating slot");
    }
  };

  return (
    <div className="container">
      <div className="header-flex">
        <h2>Schedule Management</h2>
      </div>

      {/* FORM */}
      <div className="form-container">
        <h3>Create New Session</h3>
        <div className="form">
          <div className="form-group">
            <label>📅 Date</label>
            <input type="date" name="date" value={form.date} onChange={handleChange} />
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1.5rem" }}>
            <div className="form-group">
              <label>⏰ Start Time</label>
              <input type="time" name="startTime" value={form.startTime} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label>⌛ End Time</label>
              <input type="time" name="endTime" value={form.endTime} onChange={handleChange} />
            </div>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1.5rem" }}>
            <div className="form-group">
              <label>⏱️ Duration (min)</label>
              <input type="number" name="duration" value={form.duration} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label>💳 Fee (₹)</label>
              <input type="number" name="fee" value={form.fee} onChange={handleChange} />
            </div>
          </div>

          <div className="form-group">
            <label>🏷️ Interview Type</label>
            <select name="interviewType" value={form.interviewType} onChange={handleChange}>
              <option value="TECHNICAL">Technical Interview</option>
              <option value="HR">HR / Behavioral</option>
            </select>
          </div>

          <button className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} onClick={handleCreate}>
            🚀 Publish Interview Slot
          </button>
        </div>
      </div>

      {/* EXISTING SLOTS */}
      <h3>Published Slots</h3>

      <div className="grid">
        {slots.length === 0 ? (
          <div className="empty-state" style={{ width: "100%", gridColumn: "1/-1" }}>
            <p>You haven't created any slots yet.</p>
          </div>
        ) : (
          slots.map((s) => (
            <div className="card" key={s.id}>
              <div className="card-header">
                <span className="status-badge" style={{ backgroundColor: "var(--success-color)" }}>Active</span>
                <span className="date-tag">📅 {s.date}</span>
              </div>

              <div className="card-body">
                <h3>{s.interviewType} Interview</h3>
                <div className="info-row">⏰ {s.startTime} - {s.endTime}</div>
                <div className="info-row">⏱️ {s.duration} mins</div>
                <div className="price">₹{s.fee}</div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}