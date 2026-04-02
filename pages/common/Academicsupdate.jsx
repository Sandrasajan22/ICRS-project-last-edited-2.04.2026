import { useEffect, useMemo, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { API_BASE, getAuthHeaders, getUserId } from "../trainer/api";
import "../../styles/jobseekacadview.css";

const emptyCert = () => ({
  title: "",
  issuer: "",
  year: "",
  description: "",
  imageUrl: "",
  file: null,
});

function getConfig(pathname) {
  const isStudent = pathname.startsWith("/student");

  return {
    backPath: isStudent ? "/student/academics" : "/jobseeker/academics",
  };
}

export default function Academicsupdate() {
  const navigate = useNavigate();
  const location = useLocation();
  const userId = getUserId();
  const cfg = useMemo(() => getConfig(location.pathname), [location.pathname]);
  const fileRefs = useRef({});

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [form, setForm] = useState({
    degree: "",
    field: "",
    college: "",
    university: "",
    year: "",
    marks: "",
    certifications: [emptyCert()],
  });

  useEffect(() => {
    if (!userId) {
      setLoading(false);
      setError("Login required");
      return;
    }

    (async () => {
      try {
        setLoading(true);

        const res = await fetch(`${API_BASE}/api/academics/${userId}`, {
          headers: { ...getAuthHeaders() },
        });

        if (res.ok) {
          const data = await res.json();

          setForm({
            degree: data?.degree || "",
            field: data?.field || "",
            college: data?.college || "",
            university: data?.university || "",
            year: data?.year || "",
            marks: data?.marks || "",
            certifications:
              Array.isArray(data?.certifications) && data.certifications.length
                ? data.certifications.map((c) => ({
                    title: c?.title || "",
                    issuer: c?.issuer || "",
                    year: c?.year || "",
                    description: c?.description || "",
                    imageUrl: c?.imageUrl || "",
                    file: null,
                  }))
                : [emptyCert()],
          });
        }
      } catch (e) {
        console.error(e);
        setError("Failed to load academics");
      } finally {
        setLoading(false);
      }
    })();
  }, [userId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const updateCert = (index, key, value) => {
    setForm((prev) => {
      const next = [...prev.certifications];
      next[index] = { ...next[index], [key]: value };
      return { ...prev, certifications: next };
    });
  };

  const addCert = () => {
    setForm((prev) => ({
      ...prev,
      certifications: [...prev.certifications, emptyCert()],
    }));
  };

  const removeCert = (index) => {
    setForm((prev) => {
      const filtered = prev.certifications.filter((_, i) => i !== index);
      return {
        ...prev,
        certifications: filtered.length ? filtered : [emptyCert()],
      };
    });
  };

  const pickFile = (index) => fileRefs.current[index]?.click();

  const onFileChange = (index, e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (file.size > 3 * 1024 * 1024) {
      setError("Image too large (max 3MB)");
      return;
    }

    updateCert(index, "file", file);
    updateCert(index, "imageUrl", URL.createObjectURL(file));
  };

  const onSave = async (e) => {
    e.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      const payload = {
        degree: form.degree,
        field: form.field,
        college: form.college,
        university: form.university,
        year: form.year,
        marks: form.marks,
        certifications: form.certifications.map((c) => ({
          title: c.title,
          issuer: c.issuer,
          year: c.year,
          description: c.description,
          imageUrl: c.imageUrl && !c.imageUrl.startsWith("blob:") ? c.imageUrl : "",
        })),
      };

      const res = await fetch(`${API_BASE}/api/academics/${userId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          ...getAuthHeaders(),
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || "Failed to save academics");
      }

      for (let i = 0; i < form.certifications.length; i++) {
        const cert = form.certifications[i];
        if (!cert.file) continue;

        const fd = new FormData();
        fd.append("file", cert.file);
        fd.append("index", i);

        await fetch(`${API_BASE}/api/academics/${userId}/certificates`, {
          method: "POST",
          headers: { ...getAuthHeaders() },
          body: fd,
        });
      }

      setSuccess("Academic details saved.");
      setTimeout(() => navigate(cfg.backPath), 700);
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to save academics");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="ac-view-page">
      <div className="ac-card">
        <div className="ac-header">
          <div>
            <h2>Update Academics</h2>
            <p>Add education details and certifications</p>
          </div>

          <div className="ac-actions">
            <button
              type="button"
              className="ac-btn ac-btn-outline"
              onClick={() => navigate(cfg.backPath)}
            >
              Cancel
            </button>

            <button
              type="button"
              className="ac-btn"
              disabled={saving}
              onClick={onSave}
            >
              {saving ? "Saving..." : "Save"}
            </button>
          </div>
        </div>

        {error ? <div className="ac-error">{error}</div> : null}
        {success ? <div className="ac-success">{success}</div> : null}

        {loading ? (
          <div className="ac-loading">Loading...</div>
        ) : (
          <form className="ac-form" onSubmit={onSave}>
            <div className="ac-section">
              <h4>Education</h4>

              <div className="ac-form-grid">
                <input name="degree" value={form.degree} onChange={handleChange} placeholder="Degree" />
                <input name="field" value={form.field} onChange={handleChange} placeholder="Field / Specialization" />
                <input name="college" value={form.college} onChange={handleChange} placeholder="College" />
                <input name="university" value={form.university} onChange={handleChange} placeholder="University" />
                <input name="year" value={form.year} onChange={handleChange} placeholder="Year of Passing" />
                <input name="marks" value={form.marks} onChange={handleChange} placeholder="Marks / CGPA" />
              </div>
            </div>

            <div className="ac-section">
              <div className="ac-cert-head">
                <h4>Certificates</h4>
                <button type="button" className="ac-btn" onClick={addCert}>
                  + Add Certificate
                </button>
              </div>

              {form.certifications.map((cert, index) => (
                <div className="ac-cert-edit" key={index}>
                  <div className="ac-form-grid">
                    <input
                      value={cert.title}
                      onChange={(e) => updateCert(index, "title", e.target.value)}
                      placeholder="Certificate Title"
                    />
                    <input
                      value={cert.issuer}
                      onChange={(e) => updateCert(index, "issuer", e.target.value)}
                      placeholder="Issuer"
                    />
                    <input
                      value={cert.year}
                      onChange={(e) => updateCert(index, "year", e.target.value)}
                      placeholder="Year"
                    />
                    <textarea
                      value={cert.description}
                      onChange={(e) => updateCert(index, "description", e.target.value)}
                      placeholder="Description"
                    />
                  </div>

                  <div className="ac-cert-actions">
                    <input
                      type="file"
                      accept="image/*"
                      ref={(el) => (fileRefs.current[index] = el)}
                      style={{ display: "none" }}
                      onChange={(e) => onFileChange(index, e)}
                    />

                    <button
                      type="button"
                      className="ac-btn ac-btn-outline"
                      onClick={() => pickFile(index)}
                    >
                      Upload Image
                    </button>

                    {form.certifications.length > 1 && (
                      <button
                        type="button"
                        className="ac-btn ac-btn-danger"
                        onClick={() => removeCert(index)}
                      >
                        Remove
                      </button>
                    )}
                  </div>

                  {cert.imageUrl ? (
                    <img src={cert.imageUrl} alt="preview" className="ac-preview-thumb" />
                  ) : null}
                </div>
              ))}
            </div>
          </form>
        )}
      </div>
    </div>
  );
}