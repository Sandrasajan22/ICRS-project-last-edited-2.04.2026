export default function Resumeform({ resumeData, setResumeData }) {
  const handleChange = (e) => {
    const { name, value } = e.target;
    setResumeData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSkillsChange = (e) => {
    const { name, value } = e.target;
    setResumeData((prev) => ({
      ...prev,
      skills: {
        ...prev.skills,
        [name]: value
      }
    }));
  };

  const handleArrayChange = (section, index, field, value) => {
    setResumeData((prev) => {
      const updated = [...prev[section]];
      updated[index] = { ...updated[index], [field]: value };
      return { ...prev, [section]: updated };
    });
  };

  const addItem = (section, emptyItem) => {
    setResumeData((prev) => ({
      ...prev,
      [section]: [...prev[section], emptyItem]
    }));
  };

  const removeItem = (section, index) => {
    setResumeData((prev) => ({
      ...prev,
      [section]: prev[section].filter((_, i) => i !== index)
    }));
  };

  return (
    <div className="resume-form">
      <h3>Header</h3>
      <input name="fullName" value={resumeData.fullName} onChange={handleChange} placeholder="Full Name" />
      <input name="role" value={resumeData.role} onChange={handleChange} placeholder="Target Role" />
      <input name="phone" value={resumeData.phone} onChange={handleChange} placeholder="Phone" />
      <input name="location" value={resumeData.location} onChange={handleChange} placeholder="Location" />
      <input name="email" value={resumeData.email} onChange={handleChange} placeholder="Email" />
      <input name="linkedin" value={resumeData.linkedin} onChange={handleChange} placeholder="LinkedIn" />
      <input name="portfolio" value={resumeData.portfolio} onChange={handleChange} placeholder="Portfolio / Github" />
   

      <h3>Summary</h3>
      <textarea name="objective" value={resumeData.objective} onChange={handleChange} placeholder="Summary / Objective" rows="4" />

      <h3>Skills</h3>
      <input name="tools" value={resumeData.skills.tools} onChange={handleSkillsChange} placeholder="Programming / Tools" />
      <input name="coursework" value={resumeData.skills.coursework} onChange={handleSkillsChange} placeholder="Coursework" />
      <input name="soft" value={resumeData.skills.soft} onChange={handleSkillsChange} placeholder="Soft Skills" />

      <h3>Education</h3>
      {resumeData.educations.map((edu, index) => (
        <div key={index} className="dynamic-block">
          <input value={edu.degree} onChange={(e) => handleArrayChange("educations", index, "degree", e.target.value)} placeholder="Degree" />
          <input value={edu.major} onChange={(e) => handleArrayChange("educations", index, "major", e.target.value)} placeholder="Major" />
          <input value={edu.university} onChange={(e) => handleArrayChange("educations", index, "university", e.target.value)} placeholder="University" />
          <input value={edu.year} onChange={(e) => handleArrayChange("educations", index, "year", e.target.value)} placeholder="Year / Duration" />
          <input value={edu.location} onChange={(e) => handleArrayChange("educations", index, "location", e.target.value)} placeholder="Location" />
          <input value={edu.gpa} onChange={(e) => handleArrayChange("educations", index, "gpa", e.target.value)} placeholder="GPA" />
          <input value={edu.coursework} onChange={(e) => handleArrayChange("educations", index, "coursework", e.target.value)} placeholder="Relevant Coursework" />
          {resumeData.educations.length > 1 && (
            <button type="button" className="remove-btn" onClick={() => removeItem("educations", index)}>
              Remove Education
            </button>
          )}
        </div>
      ))}
      <button
        type="button"
        className="add-btn"
        onClick={() =>
          addItem("educations", {
            degree: "",
            major: "",
            university: "",
            year: "",
            location: "",
            gpa: "",
            coursework: ""
          })
        }
      >
        + Add Education
      </button>

      <h3>Internship</h3>
      {resumeData.experiences.map((exp, index) => (
        <div key={index} className="dynamic-block">
          <input value={exp.company} onChange={(e) => handleArrayChange("experiences", index, "company", e.target.value)} placeholder="Company" />
          <input value={exp.role} onChange={(e) => handleArrayChange("experiences", index, "role", e.target.value)} placeholder="Role" />
          <input value={exp.location} onChange={(e) => handleArrayChange("experiences", index, "location", e.target.value)} placeholder="Location" />
          <input value={exp.start} onChange={(e) => handleArrayChange("experiences", index, "start", e.target.value)} placeholder="Start Date" />
          <input value={exp.end} onChange={(e) => handleArrayChange("experiences", index, "end", e.target.value)} placeholder="End Date" />
          <textarea value={exp.point} onChange={(e) => handleArrayChange("experiences", index, "point", e.target.value)} placeholder="Experience Point" rows="3" />
          {resumeData.experiences.length > 0 && (
            <button type="button" className="remove-btn" onClick={() => removeItem("experiences", index)}>
              Remove Internship
            </button>
          )}
        </div>
      ))}
      <button
        type="button"
        className="add-btn"
        onClick={() =>
          addItem("experiences", {
            company: "",
            role: "",
            location: "",
            start: "",
            end: "",
            point: ""
          })
        }
      >
        + Add Internship
      </button>

      <h3>Languages</h3>
      {resumeData.languages.map((lang, index) => (
        <div key={index} className="dynamic-block">
          <input value={lang.name} onChange={(e) => handleArrayChange("languages", index, "name", e.target.value)} placeholder="Language" />
          <input value={lang.level} onChange={(e) => handleArrayChange("languages", index, "level", e.target.value)} placeholder="Level" />
          {resumeData.languages.length > 0 && (
            <button type="button" className="remove-btn" onClick={() => removeItem("languages", index)}>
              Remove Language
            </button>
          )}
        </div>
      ))}
      <button
        type="button"
        className="add-btn"
        onClick={() => addItem("languages", { name: "", level: "" })}
      >
        + Add Language
      </button>

      <h3>Certifications</h3>
      {resumeData.certifications.map((cert, index) => (
        <div key={index} className="dynamic-block">
          <input value={cert.name} onChange={(e) => handleArrayChange("certifications", index, "name", e.target.value)} placeholder="Certification" />
          <input value={cert.issuer} onChange={(e) => handleArrayChange("certifications", index, "issuer", e.target.value)} placeholder="Issuer" />
          <input value={cert.year} onChange={(e) => handleArrayChange("certifications", index, "year", e.target.value)} placeholder="Year" />
          <textarea value={cert.desc} onChange={(e) => handleArrayChange("certifications", index, "desc", e.target.value)} placeholder="Description" rows="2" />
          {resumeData.certifications.length > 0 && (
            <button type="button" className="remove-btn" onClick={() => removeItem("certifications", index)}>
              Remove Certification
            </button>
          )}
        </div>
      ))}
      <button
        type="button"
        className="add-btn"
        onClick={() =>
          addItem("certifications", {
            name: "",
            issuer: "",
            year: "",
            desc: ""
          })
        }
      >
        + Add Certification
      </button>

      <h3>Projects</h3>
      {resumeData.projects.map((project, index) => (
        <div key={index} className="dynamic-block">
          <input value={project.name} onChange={(e) => handleArrayChange("projects", index, "name", e.target.value)} placeholder="Project Name" />
          <input value={project.tools} onChange={(e) => handleArrayChange("projects", index, "tools", e.target.value)} placeholder="Project Tools" />
          <textarea value={project.point} onChange={(e) => handleArrayChange("projects", index, "point", e.target.value)} placeholder="Project Description" rows="3" />
          {resumeData.projects.length > 0 && (
            <button type="button" className="remove-btn" onClick={() => removeItem("projects", index)}>
              Remove Project
            </button>
          )}
        </div>
      ))}
      <button
        type="button"
        className="add-btn"
        onClick={() =>
          addItem("projects", {
            name: "",
            tools: "",
            point: ""
          })
        }
      >
        + Add Project
      </button>
    </div>
  );
}