import "./templateminimal.css";

export default function TemplateMinimal({ data }) {
  return (
    <div className="minimal-template-white" id="resume-preview">
      <header className="minimal-white-header">
        <h1>{data.fullName || "Your Name"}</h1>
        <h2>{data.role || "Target Role"}</h2>

        <div className="minimal-white-contact">
          {data.email && <span>{data.email}</span>}
          {data.phone && <span>{data.phone}</span>}
          {data.location && <span>{data.location}</span>}
          {data.linkedin && <span>{data.linkedin}</span>}
          {data.portfolio && <span>{data.portfolio}</span>}
        </div>
      </header>

      {data.objective && (
        <section className="minimal-white-section">
          <h3>Summary</h3>
          <p>{data.objective}</p>
        </section>
      )}

      {data.experiences?.length > 0 && (
        <section className="minimal-white-section">
          <h3>Experience</h3>

          {data.experiences.map((exp, index) => (
            <div className="minimal-white-entry" key={index}>
              <div className="minimal-white-entry-head">
                <div>
                  <h4>{exp.role}</h4>
                  <p className="minimal-white-sub">
                    {exp.company}
                    {exp.location ? ` | ${exp.location}` : ""}
                  </p>
                </div>

                <div className="minimal-white-date">
                  <p>
                    {exp.start}
                    {exp.end ? ` - ${exp.end}` : ""}
                  </p>
                </div>
              </div>

              {exp.point && (
                <ul>
                  <li>{exp.point}</li>
                </ul>
              )}
            </div>
          ))}
        </section>
      )}

      {data.educations?.length > 0 && (
        <section className="minimal-white-section">
          <h3>Education</h3>

          {data.educations.map((edu, index) => (
            <div className="minimal-white-entry" key={index}>
              <div className="minimal-white-entry-head">
                <div>
                  <h4>{edu.degree}</h4>
                  <p className="minimal-white-sub">
                    {edu.university}
                    {edu.location ? ` | ${edu.location}` : ""}
                  </p>
                  {edu.major && <p>{edu.major}</p>}
                </div>

                <div className="minimal-white-date">
                  {edu.year && <p>{edu.year}</p>}
                  {edu.gpa && <p>GPA: {edu.gpa}</p>}
                </div>
              </div>

              {edu.coursework && <p>{edu.coursework}</p>}
            </div>
          ))}
        </section>
      )}

      {(data.skills?.tools ||
        data.skills?.coursework ||
        data.skills?.soft) && (
        <section className="minimal-white-section">
          <h3>Skills</h3>
          <div className="minimal-white-skills">
            {data.skills?.tools && (
              <p>
                <strong>Tools:</strong> {data.skills.tools}
              </p>
            )}
            {data.skills?.coursework && (
              <p>
                <strong>Coursework:</strong> {data.skills.coursework}
              </p>
            )}
            {data.skills?.soft && (
              <p>
                <strong>Soft Skills:</strong> {data.skills.soft}
              </p>
            )}
          </div>
        </section>
      )}

      {data.languages?.length > 0 && (
        <section className="minimal-white-section">
          <h3>Languages</h3>
          <div className="minimal-white-inline">
            {data.languages.map((lang, index) => (
              <div className="minimal-white-inline-item" key={index}>
                <strong>{lang.name}</strong>
                <span>{lang.level}</span>
              </div>
            ))}
          </div>
        </section>
      )}

      {data.certifications?.length > 0 && (
        <section className="minimal-white-section">
          <h3>Certifications</h3>

          {data.certifications.map((cert, index) => (
            <div className="minimal-white-entry" key={index}>
              <h4>{cert.name}</h4>
              {(cert.issuer || cert.year) && (
                <p className="minimal-white-sub">
                  {[cert.issuer, cert.year].filter(Boolean).join(" | ")}
                </p>
              )}
              {cert.desc && <p>{cert.desc}</p>}
            </div>
          ))}
        </section>
      )}

      {data.projects?.length > 0 && (
        <section className="minimal-white-section">
          <h3>Projects</h3>

          {data.projects.map((project, index) => (
            <div className="minimal-white-entry" key={index}>
              <h4>
                {project.name}
                {project.tools ? ` | ${project.tools}` : ""}
              </h4>
              {project.point && (
                <ul>
                  <li>{project.point}</li>
                </ul>
              )}
            </div>
          ))}
        </section>
      )}
    </div>
  );
}