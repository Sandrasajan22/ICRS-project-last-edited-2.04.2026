import { useEffect, useMemo, useState } from "react";
import "../../styles/admindash.css";

export default function AdminVerification() {
  const [requests, setRequests] = useState([]);
  const [selected, setSelected] = useState(null);
  const [showDocs, setShowDocs] = useState(false);
  const [activeDoc, setActiveDoc] = useState("id");

  // ✅ UI only: Fit view toggle (zoom out inside the preview area)
  const [fitView, setFitView] = useState(true);

  // ===== FETCH PENDING REQUESTS =====
  useEffect(() => {
    fetch("http://localhost:8080/api/admin/verification/pending")
      .then((res) => res.json())
      .then((data) => setRequests(data))
      .catch((err) => console.error(err));
  }, []);

  // ===== APPROVE / REJECT =====
  const handleAction = async (id, action) => {
    const res = await fetch(
      `http://localhost:8080/api/admin/verification/${id}/${action}`,
      { method: "POST" }
    );

    if (res.ok) {
      alert(`Verification ${action}ed`);
      setRequests((prev) => prev.filter((r) => r.id !== id));
      setSelected(null);
      setShowDocs(false);
    } else {
      alert("Action failed");
    }
  };

  // ✅ Helper: current doc url (UI only)
  const currentDocUrl = useMemo(() => {
    if (!selected) return "";

    const base = "http://localhost:8080";

    if (activeDoc === "id" && selected.idProofPath)
      return base + selected.idProofPath;

    if (activeDoc === "cert" && selected.certificatePath)
      return base + selected.certificatePath;

    if (activeDoc === "other" && selected.otherProofPath)
      return base + selected.otherProofPath;

    return "";
  }, [selected, activeDoc]);

  return (
    <div className="admin-page">
      <h2>Pending Verification Requests</h2>

      <table className="admin-table">
        <thead>
          <tr>
            <th>User ID</th>
            <th>User</th>
            <th>Status</th>
            <th>Documents</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          {requests.length === 0 ? (
            <tr>
              <td colSpan="5" style={{ textAlign: "center" }}>
                No pending requests
              </td>
            </tr>
          ) : (
            requests.map((req) => (
              <tr key={req.id}>
                <td>{req.user.id}</td>

                <td>
                  <div className="user-cell">
                    <strong>
                      {req.user.fname} {req.user.lname}
                    </strong>
                    <div className="user-email">{req.user.email}</div>
                  </div>
                </td>

                <td>
                  <span className="status pending">{req.status}</span>
                </td>

                <td>
                  <button
                    className="btn btn-view"
                    onClick={() => {
                      setSelected(req);
                      setShowDocs(true);
                      setActiveDoc("id");
                      setFitView(true); // ✅ default to fit view
                    }}
                  >
                    View
                  </button>
                </td>

                <td>
                  <div className="action-cell">
                    <button
                      className="btn approve"
                      onClick={() => handleAction(req.id, "approve")}
                    >
                      Approve
                    </button>
                    <button
                      className="btn reject"
                      onClick={() => handleAction(req.id, "reject")}
                    >
                      Reject
                    </button>
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {/* ===== DOCUMENT PREVIEW ===== */}
      {selected && showDocs && (
        <div className="doc-preview">
          <div className="doc-header">
            <h3>
              Documents — {selected.user.fname} {selected.user.lname}
            </h3>

            <div className="doc-header-actions">
              {/* ✅ UI only */}
              <button
                className="btn btn-fit"
                onClick={() => setFitView((p) => !p)}
                type="button"
              >
                {fitView ? "Normal" : "Fit"}
              </button>

              {/* ✅ UI only (best viewing) */}
              {currentDocUrl && (
                <a
                  className="btn btn-open"
                  href={currentDocUrl}
                  target="_blank"
                  rel="noreferrer"
                >
                  Open
                </a>
              )}

              <button
                className="btn btn-hide"
                onClick={() => setShowDocs(false)}
                type="button"
              >
                Hide
              </button>
            </div>
          </div>

          {/* Document Tabs */}
          <div className="doc-tabs">
            {selected.idProofPath && (
              <button
                className={activeDoc === "id" ? "tab active" : "tab"}
                onClick={() => setActiveDoc("id")}
                type="button"
              >
                ID Proof
              </button>
            )}
            {selected.certificatePath && (
              <button
                className={activeDoc === "cert" ? "tab active" : "tab"}
                onClick={() => setActiveDoc("cert")}
                type="button"
              >
                Certificate
              </button>
            )}
            {selected.otherProofPath && (
              <button
                className={activeDoc === "other" ? "tab active" : "tab"}
                onClick={() => setActiveDoc("other")}
                type="button"
              >
                Other
              </button>
            )}
          </div>

          {/* Document Frame */}
          <div className={`doc-frame ${fitView ? "fit-view" : "normal-view"}`}>
            <div className="doc-frame-inner">
              {activeDoc === "id" && selected.idProofPath && (
                <iframe src={currentDocUrl} title="ID Proof" />
              )}

              {activeDoc === "cert" && selected.certificatePath && (
                <iframe src={currentDocUrl} title="Certificate" />
              )}

              {activeDoc === "other" && selected.otherProofPath && (
                <iframe src={currentDocUrl} title="Other Proof" />
              )}
            </div>
          </div>

          {/* Small helper text */}
          <div className="doc-hint">
            Tip: Use <b>Open</b> for the best fit/zoom controls.
          </div>
        </div>
      )}
    </div>
  );
}
