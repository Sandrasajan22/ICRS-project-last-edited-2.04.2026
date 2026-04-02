import { useEffect, useState } from "react";
import { API_BASE, getAuthHeaders } from "../trainer/api"; 
import "../../styles/admincomplaints.css";

export default function AdminComplaints() {
    const [pending, setPending] = useState([]);
    const [selected, setSelected] = useState(null);
    const [resolutionNote, setResolutionNote] = useState("");
    const [resolving, setResolving] = useState(false);
    const [error, setError] = useState(null);

    const fetchPending = async () => {
        try {
            const res = await fetch(`${API_BASE}/api/complaints/pending`, { headers: { ...getAuthHeaders() } });
            if (!res.ok) throw new Error("Fetch failed");
            const data = await res.json();
            setPending(data);
        } catch (err) {
            console.error(err);
            setError("Failed to load complaints.");
        }
    };

    const handleResolve = async (e) => {
        e.preventDefault();
        if (!selected || !resolutionNote) return alert("Note is required");

        setResolving(true);
        try {
            const res = await fetch(`${API_BASE}/api/complaints/resolve/${selected.id}`, {
                method: "PUT",
                headers: { ...getAuthHeaders(), "Content-Type": "application/json" },
                body: JSON.stringify({ resolutionNote }),
            });

            if (res.ok) {
                alert("Complaint resolved successfully!");
                setSelected(null);
                setResolutionNote("");
                fetchPending();
            } else {
                alert("Failed to resolve complaint.");
            }
        } catch (err) {
            console.error(err);
            alert("Error in resolution.");
        } finally {
            setResolving(false);
        }
    };

    useEffect(() => {
        fetchPending();
    }, []);

    return (
        <div className="admin-complaints">
            <header className="adm-comp-header">
                <h2>Complaint Management Portal</h2>
                <p>Track and resolve system issues reported by platform users.</p>
            </header>

            {error && <div className="adm-comp-error">{error}</div>}

            <div className="adm-comp-grid">
                {pending.length === 0 ? (
                    <div className="adm-comp-empty">
                        <p>No pending complaints found.</p>
                    </div>
                ) : (
                    pending.map((c) => (
                        <div key={c.id} className="adm-comp-card" onClick={() => setSelected(c)}>
                            <div className="adm-comp-card-badge">PENDING</div>
                            <h3 className="adm-comp-card-sub">{c.subject}</h3>
                            <p className="adm-comp-card-desc">
                                {c.description.substring(0, 100)}...
                            </p>
                            <div className="adm-comp-card-meta">
                                Reported by ID: {c.reporter?.id}
                                <span>{new Date(c.createdAt).toLocaleDateString()}</span>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {selected && (
                <div className="adm-comp-modal-overlay">
                    <div className="adm-comp-modal">
                        <header className="adm-modal-header">
                            <div>
                                <h2>{selected.subject}</h2>
                                <p>Reported by User ID: {selected.reporter?.id}</p>
                            </div>
                            <button className="adm-modal-close" onClick={() => setSelected(null)}>×</button>
                        </header>

                        <div className="adm-modal-body">
                            <div className="adm-section">
                                <h3>Detailed Problem Description:</h3>
                                <div className="adm-desc-box">{selected.description}</div>
                            </div>

                            {selected.screenshotPath && (
                                <div className="adm-section">
                                    <h3>User Screenshot:</h3>
                                    <div className="adm-screenshot-box">
                                        <img src={`${API_BASE}${selected.screenshotPath}`} alt="Complaint Screenshot" />
                                    </div>
                                </div>
                            )}

                            <form className="adm-resolve-form" onSubmit={handleResolve}>
                                <h3>Resolution Action:</h3>
                                <textarea
                                    placeholder="Steps taken to resolve this issue..."
                                    value={resolutionNote}
                                    onChange={(e) => setResolutionNote(e.target.value)}
                                    rows={4}
                                    required
                                />
                                <button type="submit" disabled={resolving} className="adm-resolve-btn">
                                    {resolving ? "Finalizing..." : "Mark as Resolved"}
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
