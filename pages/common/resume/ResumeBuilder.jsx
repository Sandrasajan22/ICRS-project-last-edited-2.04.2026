import { useEffect, useState } from "react";
import axios from "axios";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";

import Resumeform from "./Resumeform";
import TemplateClassic from "./TemplateClassic";
import TemplateMinimal from "./TemplateMinimal";
import TemplateModern from "./TemplateModern";

import "../../../styles/resumebuild.css";

const defaultData = {
  fullName: "",
  role: "",
  phone: "",
  location: "",
  email: "",
  linkedin: "",
  portfolio: "",
  profile_photo: "",
  objective: "",

  skills: { tools: "", coursework: "", soft: "" },

  educations: [
    { degree: "", major: "", university: "", year: "", location: "", gpa: "" }
  ],
  experiences: [
    { company: "", role: "", location: "", start: "", end: "", point: "" }
  ],
  languages: [{ name: "", level: "" }],
  certifications: [{ name: "", issuer: "", year: "", desc: "" }],
  projects: [{ name: "", tools: "", point: "" }]
};

export default function ResumeBuilder() {
  const [resumeData, setResumeData] = useState(defaultData);
  const [selectedTemplate, setSelectedTemplate] = useState("classic");

  // ✅ Get userId directly (NO user object needed)
  const getUserId = () => {
    return localStorage.getItem("userId");
  };

  // ================= FETCH =================
  useEffect(() => {
    const userId = getUserId();

    console.log("UserId from localStorage:", userId);

    if (!userId) return;

    axios
      .get(`http://localhost:8080/api/resume/user/${userId}`)
      .then((res) => {
        if (res.data) {
          setResumeData({
            ...defaultData,
            ...res.data,
            skills: JSON.parse(
              res.data.skillsJson || '{"tools":"","coursework":"","soft":""}'
            ),
            educations: JSON.parse(res.data.educationsJson || "[]"),
            experiences: JSON.parse(res.data.experiencesJson || "[]"),
            languages: JSON.parse(res.data.languagesJson || "[]"),
            certifications: JSON.parse(res.data.certificationsJson || "[]"),
            projects: JSON.parse(res.data.projectsJson || "[]"),
            profile_photo: res.data.profilePhoto || ""
          });

          setSelectedTemplate(res.data.selectedTemplate || "classic");
        }
      })
     .catch((err) => {
  console.error("Fetch error:", err.response?.data || err.message);
});
  }, []);

  // ================= SAVE =================
  const handleSave = async () => {
    const userId = getUserId();

    if (!userId) {
      alert("User not logged in");
      return;
    }

    try {
      await axios.post("http://localhost:8080/api/resume/save", {
        userId: Number(userId),
        selectedTemplate,
        fullName: resumeData.fullName,
        role: resumeData.role,
        phone: resumeData.phone,
        location: resumeData.location,
        email: resumeData.email,
        linkedin: resumeData.linkedin,
        portfolio: resumeData.portfolio,
        profilePhoto: resumeData.profile_photo,
        objective: resumeData.objective,
        skillsJson: JSON.stringify(resumeData.skills),
        educationsJson: JSON.stringify(resumeData.educations),
        experiencesJson: JSON.stringify(resumeData.experiences),
        languagesJson: JSON.stringify(resumeData.languages),
        certificationsJson: JSON.stringify(resumeData.certifications),
        projectsJson: JSON.stringify(resumeData.projects)
      });

      alert("Resume saved successfully");
    } catch (error) {
  console.error("Save error:", error.response?.data || error.message);
  alert("Failed to save resume");
}
  };

  // ================= DOWNLOAD =================
  const handleDownload = async () => {
    const input = document.getElementById("resume-preview");
    if (!input) return;

    try {
      const canvas = await html2canvas(input, {
        scale: 2,
        useCORS: true,
        backgroundColor: "#ffffff"
      });

      const img = canvas.toDataURL("image/png");
      const pdf = new jsPDF("p", "mm", "a4");

      const imgWidth = 210;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;

      pdf.addImage(img, "PNG", 0, 0, imgWidth, imgHeight);
      pdf.save("resume.pdf");
    } catch (error) {
      console.error("Download error:", error);
    }
  };

  // ================= TEMPLATE =================
  const renderTemplate = () => {
    switch (selectedTemplate) {
      case "minimal":
        return <TemplateMinimal data={resumeData} />;
      case "modern":
        return <TemplateModern data={resumeData} />;
      default:
        return <TemplateClassic data={resumeData} />;
    }
  };

  return (
    <div className="resume-builder-page">
      <aside className="resume-builder-left">
        <h2>Resume Builder</h2>

        <select
          value={selectedTemplate}
          onChange={(e) => setSelectedTemplate(e.target.value)}
        >
          <option value="classic">Classic</option>
          <option value="minimal">Minimal</option>
          <option value="modern">Modern</option>
        </select>

        <button onClick={handleSave}>Save</button>
        <button onClick={handleDownload}>Download</button>

        <Resumeform
          resumeData={resumeData}
          setResumeData={setResumeData}
        />
      </aside>

      <main className="resume-builder-right">
        {renderTemplate()}
      </main>
    </div>
  );
}