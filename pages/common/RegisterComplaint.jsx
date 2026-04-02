import { useState } from "react";
import { getUserId, API_BASE, getAuthHeaders } from "../trainer/api";
import "../../styles/complaints.css";

export default function RegisterComplaint() {
    const userId = getUserId();
    const [subject, setSubject] = useState("");
    const [description, setDescription] = useState("");
    const [screenshot, setScreenshot] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!userId) return alert("User not logged in");
        if (!subject || !description) return alert("Subject and description are required");

        setSubmitting(true);
        setMessage(null);

        const formData = new FormData();
        formData.append("userId", userId);
        formData.append("subject", subject);
        formData.append("description", description);
        if (screenshot) formData.append("screenshot", screenshot);

        try {
            const res = await fetch(`${API_BASE}/api/complaints/submit`, {
                method: "POST",
                body: formData,
                // headers: { ...getAuthHeaders() }, // Multipart doesn't need Content-Type JSON
            });

            if (res.ok) {
                setMessage({ type: "success", text: "Your complaint has been registered successfully. Admin will review it shortly." });
                setSubject("");
                setDescription("");
                setScreenshot(null);
            } else {
                setMessage({ type: "error", text: "Failed to submit complaint. Please try again." });
            }
        } catch (err) {
            console.error(err);
            setMessage({ type: "error", text: "Connection error." });
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="comp-container">
            <div className="comp-card glass">
                <h1 className="comp-title">Register a Complaint</h1>
                <p className="comp-subtitle">Report problems you face on the platform. Screenshots help us resolve issues faster.</p>

                {message && (
                    <div className={`comp-alert ${message.type}`}>
                        {message.text}
                    </div>
                )}

                <form className="comp-form" onSubmit={handleSubmit}>
                    <div className="comp-field">
                        <label>Subject</label>
                        <input
                            type="text"
                            placeholder="Brief summary of the issue"
                            value={subject}
                            onChange={(e) => setSubject(e.target.value)}
                            required
                        />
                    </div>

                    <div className="comp-field">
                        <label>Description</label>
                        <textarea
                            placeholder="Describe the issue in detail..."
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            rows={6}
                            required
                        />
                    </div>

                    <div className="comp-field">
                        <label>Attach Screenshot (Optional)</label>
                        <div className="comp-file-wrapper">
                            <input
                                type="file"
                                accept="image/*"
                                onChange={(e) => setScreenshot(e.target.files[0])}
                                id="screenshot-upload"
                            />
                            <label htmlFor="screenshot-upload" className="comp-file-btn">
                                🖼️ {screenshot ? screenshot.name : "Choose Screenshot"}
                            </label>
                        </div>
                    </div>

                    <button className="comp-submit-btn" type="submit" disabled={submitting}>
                        {submitting ? "Sending..." : "Submit Complaint"}
                    </button>
                </form>
            </div>
        </div>
    );
}
