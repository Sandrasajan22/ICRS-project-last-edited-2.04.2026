import "./templatemodern.css";

export default function TemplateModern({ data }) {
  return (
    <div className="modern-template" id="resume-preview">
      <div className="modern-top">
        <div className="modern-top-left">
          <h1>{data.fullName || "Your Name"}</h1>
          <h2>{data.role || "Target Role"}</h2>

          {data.objective && (
            <p className="modern-summary-text">{data.objective}</p>
          )}
        </div>

        <div className="modern-top-right">
          {data.email && <p>{data.email}</p>}
          {data.phone && <p>{data.phone}</p>}
          {data.linkedin && <p>{data.linkedin}</p>}
          {data.location && <p>{data.location}</p>}
          {data.portfolio && <p>{data.portfolio}</p>}
        </div>
      </div>

      {data.experiences?.length > 0 && (
        <section className="modern-section">
          <div className="modern-section-title">
            <span>Professional Experience</span>
          </div>

          {data.experiences.map((exp, index) => (
            <div className="modern-entry" key={index}>
              <div className="modern-entry-head">
                <div>
                  <h3>{exp.role}</h3>
                  <p className="modern-subline">
                    {exp.company}
                    {exp.location ? `, ${exp.location}` : ""}
                  </p>
                </div>

                <div className="modern-date">
                  <p>
                    {exp.start}
                    {exp.end ? ` - ${exp.end}` : ""}
                  </p>
                </div>
              </div>

              {exp.point && (
                <ul className="modern-list">
                  <li>{exp.point}</li>
                </ul>
              )}
            </div>
          ))}
        </section>
      )}

      {data.educations?.length > 0 && (
        <section className="modern-section">
          <div className="modern-section-title">
            <span>Education</span>
          </div>

          {data.educations.map((edu, index) => (
            <div className="modern-entry" key={index}>
              <div className="modern-entry-head">
                <div>
                  <h3>{edu.degree}</h3>
                  <p className="modern-subline">
                    {edu.university}
                    {edu.location ? `, ${edu.location}` : ""}
                  </p>
                  {edu.major && <p className="modern-subline">{edu.major}</p>}
                </div>

                <div className="modern-date">
                  {edu.year && <p>{edu.year}</p>}
                  {edu.gpa && <p>{edu.gpa}</p>}
                </div>
              </div>
            </div>
          ))}
        </section>
      )}

      {(data.skills?.tools ||
        data.skills?.coursework ||
        data.skills?.soft) && (
        <section className="modern-section">
          <div className="modern-section-title">
            <span>Skills</span>
          </div>

          <div className="modern-skill-group">
            {data.skills?.tools && <p>{data.skills.tools}</p>}
            {data.skills?.coursework && <p>{data.skills.coursework}</p>}
            {data.skills?.soft && <p>{data.skills.soft}</p>}
          </div>
        </section>
      )}

      {data.languages?.length > 0 && (
        <section className="modern-section">
          <div className="modern-section-title">
            <span>Languages</span>
          </div>

          <div className="modern-inline-items">
            {data.languages.map((lang, index) => (
              <div className="modern-inline-item" key={index}>
                <strong>{lang.name}</strong>
                <span>{lang.level}</span>
              </div>
            ))}
          </div>
        </section>
      )}

      {data.certifications?.length > 0 && (
        <section className="modern-section">
          <div className="modern-section-title">
            <span>Certifications</span>
          </div>

          <ol className="modern-ordered-list">
            {data.certifications.map((cert, index) => (
              <li key={index}>
                <strong>{cert.name}</strong>
                {(cert.issuer || cert.year) && (
                  <span>
                    {" "}
                    - {[cert.issuer, cert.year].filter(Boolean).join(" | ")}
                  </span>
                )}
                {cert.desc && <p>{cert.desc}</p>}
              </li>
            ))}
          </ol>
        </section>
      )}

      {data.projects?.length > 0 && (
        <section className="modern-section">
          <div className="modern-section-title">
            <span>Projects</span>
          </div>

          {data.projects.map((project, index) => (
            <div className="modern-entry" key={index}>
              <h3>
                {project.name}
                {project.tools ? ` | ${project.tools}` : ""}
              </h3>
              {project.point && (
                <ul className="modern-list">
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