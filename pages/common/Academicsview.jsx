import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { API_BASE, getAuthHeaders, getUserId } from "../trainer/api";
import "../../styles/jobseekacadview.css";

const normalizeUrl = (url) => {
  if (!url) return "";
  if (url.startsWith("blob:")) return url;
  if (url.startsWith("http")) return url;
  if (url.startsWith("/")) return `${API_BASE}${url}`;
  return `${API_BASE}/${url}`;
};

function getConfig(pathname) {
  const isStudent = pathname.startsWith("/student");

  return {
    isStudent,
    backPath: isStudent ? "/student/profile" : "/jobseeker/profile",
    updatePath: isStudent ? "/student/academics/update" : "/jobseeker/academics/update",
  };
}

export default function Academicsview() {
  const navigate = useNavigate();
  const location = useLocation();
  const userId = getUserId();
  const cfg = useMemo(() => getConfig(location.pathname), [location.pathname]);

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [previewImage, setPreviewImage] = useState("");

  useEffect(() => {
    if (!userId) {
      setLoading(false);
      return;
    }

    (async () => {
      try {
        setLoading(true);

        const res = await fetch(`${API_BASE}/api/academics/${userId}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            ...getAuthHeaders(),
          },
        });

        if (res.ok) {
          const d = await res.json();
          setData(d || null);
        } else if (res.status === 404) {
          setData(null);
        } else {
          const txt = await res.text();
          console.error("Academics fetch failed:", res.status, txt);
          setData(null);
        }
      } catch (e) {
        console.error("Academics page load error:", e);
        setData(null);
      } finally {
        setLoading(false);
      }
    })();
  }, [userId]);

  return (
    <div className="ac-view-page">
      <div className="ac-card">
        <div className="ac-header">
          <div>
            <h2>Academic Details</h2>
            <p>Your education, marks, and certifications</p>
          </div>

          <div className="ac-actions">
            <button
              type="button"
              className="ac-btn ac-btn-outline"
              onClick={() => navigate(cfg.backPath)}
            >
              Back
            </button>

            <button
              type="button"
              className="ac-btn"
              onClick={() => navigate(cfg.updatePath)}
            >
              {data ? "Update" : "Add Academics"}
            </button>
          </div>
        </div>

        {loading ? (
          <div className="ac-loading">Loading...</div>
        ) : !data ? (
          <div className="ac-empty">
            <p>No academic details added.</p>
            <button
              type="button"
              className="ac-btn"
              onClick={() => navigate(cfg.updatePath)}
            >
              Add Academics
            </button>
          </div>
        ) : (
          <div className="ac-content">
            <div className="ac-section">
              <h4>Education</h4>
              <div className="ac-grid">
                <p><strong>Degree:</strong> {data.degree || "-"}</p>
                <p><strong>Field / Specialization:</strong> {data.field || "-"}</p>
                <p><strong>College:</strong> {data.college || "-"}</p>
                <p><strong>University:</strong> {data.university || "-"}</p>
                <p><strong>Year of Passing:</strong> {data.year || "-"}</p>
                <p><strong>Marks / CGPA:</strong> {data.marks || "-"}</p>
              </div>
            </div>

            <div className="ac-section">
              <h4>Certificates</h4>

              {data.certifications?.length ? (
                <div className="ac-cert-list">
                  {data.certifications.map((c, i) => {
                    const imageSrc = c.imageUrl ? normalizeUrl(c.imageUrl) : "";

                    return (
                      <div key={c.id || i} className="ac-cert">
                        <div className="ac-cert-info">
                          <p><strong>{c.title || "-"}</strong></p>
                          <p>{c.issuer || "-"}</p>
                          <p>{c.year || "-"}</p>
                          {c.description ? <p>{c.description}</p> : null}
                        </div>

                        {imageSrc ? (
                          <div
                            className="ac-cert-image"
                            onClick={() => setPreviewImage(imageSrc)}
                            title="Click to preview"
                          >
                            <img
                              src={imageSrc}
                              alt={c.title || `Certificate ${i + 1}`}
                            />
                          </div>
                        ) : null}
                      </div>
                    );
                  })}
                </div>
              ) : (
                <p className="ac-muted">No certifications added.</p>
              )}
            </div>
          </div>
        )}
      </div>

      {previewImage ? (
        <div className="ac-modal-overlay" onClick={() => setPreviewImage("")}>
          <div className="ac-modal-content" onClick={(e) => e.stopPropagation()}>
            <button
              type="button"
              className="ac-modal-close"
              onClick={() => setPreviewImage("")}
            >
              ×
            </button>
            <img src={previewImage} alt="Certificate preview" />
          </div>
        </div>
      ) : null}
    </div>
  );
}