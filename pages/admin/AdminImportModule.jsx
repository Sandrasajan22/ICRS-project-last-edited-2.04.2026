import { useRef, useState } from "react";
import "./adminimport.css"

const BASE_URL = "http://localhost:8080/api/admin";

const EXPECTED_HEADERS = {
  technical: [
    "stream",
    "skill",
    "level",
    "set_number",
    "question",
    "option1",
    "option2",
    "option3",
    "option4",
    "correct_answer",
    "difficulty",
    "question_type",
  ],
  communication: [
    "stream",
    "level",
    "set_number",
    "question",
    "option1",
    "option2",
    "option3",
    "option4",
    "correct_answer",
    "difficulty",
    "question_type",
  ],
};

export default function AdminImportModule() {
  const [file, setFile] = useState(null);
  const [type, setType] = useState("technical");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const inputRef = useRef(null);

  const handleTypeChange = (e) => {
    setType(e.target.value);
    setFile(null);
    setMessage("");
    if (inputRef.current) inputRef.current.value = "";
  };

  const validateFile = (selectedFile, uploadType) => {
    if (!selectedFile) return "Please select a file.";

    const name = selectedFile.name.toLowerCase();
    if (!name.endsWith(".xlsx")) {
      return "Only .xlsx files are allowed.";
    }

    const maxSize = 5 * 1024 * 1024;
    if (selectedFile.size > maxSize) {
      return "File is too large. Maximum size is 5 MB.";
    }

    if (!EXPECTED_HEADERS[uploadType]) {
      return "Invalid upload type selected.";
    }

    return "";
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    setMessage("");

    const validationError = validateFile(file, type);
    if (validationError) {
      setMessage(validationError);
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      setLoading(true);

      const res = await fetch(`${BASE_URL}/import/${type}/upload`, {
        method: "POST",
        body: formData,
      });

      const text = await res.text();
      setMessage(text);

      if (res.ok) {
        setFile(null);
        if (inputRef.current) inputRef.current.value = "";
      }
    } catch (err) {
      setMessage("Upload failed.");
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadTemplate = async () => {
    setMessage("");

    try {
      const res = await fetch(`${BASE_URL}/import/${type}/template`);
      if (!res.ok) {
        const text = await res.text();
        setMessage(text || "Template download failed.");
        return;
      }

      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);

      const a = document.createElement("a");
      a.href = url;
      a.download = `${type}_template.xlsx`;
      document.body.appendChild(a);
      a.click();
      a.remove();

      window.URL.revokeObjectURL(url);
    } catch (err) {
      setMessage("Template download failed.");
    }
  };

  return (
    <div className="admin-import-page">
      <div className="admin-import-card">
        <h2>Question Import Module</h2>

        <div className="form-group">
          <label>Import Type</label>
          <select value={type} onChange={handleTypeChange}>
            <option value="technical">Technical Questions</option>
            <option value="communication">Communication Questions</option>
          </select>
        </div>

        <div className="template-actions">
          <button
            type="button"
            className="template-btn"
            onClick={handleDownloadTemplate}
          >
            Download Excel Template
          </button>
        </div>

        <form onSubmit={handleUpload} className="upload-form">
          <div className="form-group">
            <label>Upload Excel File (.xlsx)</label>
            <input
              ref={inputRef}
              type="file"
              accept=".xlsx"
              onChange={(e) => {
                setMessage("");
                setFile(e.target.files?.[0] || null);
              }}
            />
          </div>

          <div className="header-preview">
            <strong>Expected columns:</strong>
            <div>{EXPECTED_HEADERS[type].join(", ")}</div>
          </div>

          <button type="submit" disabled={loading}>
            {loading ? "Uploading..." : "Upload"}
          </button>
        </form>

        {message && <div className="upload-message">{message}</div>}
      </div>
    </div>
  );
}