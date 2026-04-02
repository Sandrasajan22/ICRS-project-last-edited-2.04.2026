import "./templateclassic.css";

export default function TemplateClassic({ data }) {
  return (
    <div className="classic-template" id="resume-preview">
      <div className="classic-top">
        <div className="classic-header-info">
          <h1>{data.fullName || "Your Name"}</h1>
          <p>{data.phone}</p>
          <p>{data.email}</p>
          <p>{data.location}</p>
          <p>{data.linkedin}</p>
          <p>{data.portfolio}</p>
        </div>

        {data.profile_photo ? (
          <div className="classic-photo-wrap">
            <img
              src={data.profile_photo}
              alt="Profile"
              className="classic-photo"
            />
          </div>
        ) : null}
      </div>

      <div className="classic-main">
        {data.objective && (
          <div className="classic-summary-block">
            <div className="classic-section-label">SUMMARY</div>
            <div className="classic-summary-content">
              <p>{data.objective}</p>
            </div>
          </div>
        )}

        {data.educations?.length > 0 && (
          <div className="classic-section-block">
            <div className="classic-section-label">EDUCATION</div>
            <div className="classic-section-content">
              {data.educations.map((edu, index) => (
                <div className="classic-row-block" key={index}>
                  <div className="classic-left-col">
                    <p>{edu.year}</p>
                  </div>
                  <div className="classic-right-col">
                    <h3>{edu.degree}</h3>
                    <p>
                      {edu.major}
                      {edu.university ? ` — ${edu.university}` : ""}
                      {edu.location ? ` — ${edu.location}` : ""}
                    </p>
                    {edu.gpa && <p>GPA: {edu.gpa}</p>}
                    {edu.coursework && <p>Coursework: {edu.coursework}</p>}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {data.experiences?.length > 0 && (
          <div className="classic-section-block">
            <div className="classic-section-label">WORK EXPERIENCE</div>
            <div className="classic-section-content">
              {data.experiences.map((exp, index) => (
                <div className="classic-row-block" key={index}>
                  <div className="classic-left-col">
                    <p>
                      {exp.start}
                      {exp.end ? ` – ${exp.end}` : ""}
                    </p>
                  </div>
                  <div className="classic-right-col">
                    <h3>{exp.role}</h3>
                    <p>
                      {exp.company}
                      {exp.location ? ` — ${exp.location}` : ""}
                    </p>
                    {exp.point && (
                      <ul>
                        <li>{exp.point}</li>
                      </ul>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {(data.skills?.tools || data.skills?.coursework || data.skills?.soft) && (
          <div className="classic-section-block">
            <div className="classic-section-label">SKILLS</div>
            <div className="classic-section-content">
              <div className="classic-skills-grid">
                {data.skills?.tools && (
                  <div className="classic-skill-box">
                    <h4>Tools</h4>
                    <p>{data.skills.tools}</p>
                  </div>
                )}
                {data.skills?.coursework && (
                  <div className="classic-skill-box">
                    <h4>Coursework</h4>
                    <p>{data.skills.coursework}</p>
                  </div>
                )}
                {data.skills?.soft && (
                  <div className="classic-skill-box">
                    <h4>Soft Skills</h4>
                    <p>{data.skills.soft}</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {data.languages?.length > 0 && (
          <div className="classic-section-block">
            <div className="classic-section-label">LANGUAGES</div>
            <div className="classic-section-content">
              <div className="classic-inline-grid">
                {data.languages.map((lang, index) => (
                  <div key={index} className="classic-inline-item">
                    <strong>{lang.name}</strong>
                    <span>{lang.level}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {data.certifications?.length > 0 && (
          <div className="classic-section-block">
            <div className="classic-section-label">CERTIFICATIONS</div>
            <div className="classic-section-content">
              {data.certifications.map((cert, index) => (
                <div className="classic-cert-block" key={index}>
                  <h3>{cert.name}</h3>
                  <p>
                    {cert.issuer}
                    {cert.year ? ` | ${cert.year}` : ""}
                  </p>
                  {cert.desc && <p>{cert.desc}</p>}
                </div>
              ))}
            </div>
          </div>
        )}

        {data.projects?.length > 0 && (
          <div className="classic-section-block">
            <div className="classic-section-label">PROJECTS</div>
            <div className="classic-section-content">
              {data.projects.map((project, index) => (
                <div className="classic-project-block" key={index}>
                  <h3>
                    {project.name}
                    {project.tools ? ` | ${project.tools}` : ""}
                  </h3>
                  {project.point && (
                    <ul>
                      <li>{project.point}</li>
                    </ul>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}