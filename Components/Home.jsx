import { Link } from "react-router-dom";
import "../styles/home.css";
import "../Components/Navbar.jsx";
import Navbar from "../Components/Navbar.jsx";

function Home() {
  return (
    <div className="app-container">

      <Navbar />
      {/* Hero Section with Video Background */}
      <header className="hero-section video-hero">
        <video
          className="hero-video"
          autoPlay
          muted
          loop
          playsInline
          poster="/images/career-sign-post.webp"
        >
          <source src="/videos/The Seasons.mp4" type="video/mp4" />
          Your browser does not support the video tag.
        </video>

        <div className="hero-overlay">
          <h1>Integrated Career Readiness System</h1>
          <p>
            A centralized platform for skill assessment, personalized learning
            recommendations, and career readiness support.
          </p>

          {/* Get Started CTA */}
          <Link to="/signup" className="get-started-btn">
            Get Started
          </Link>
        </div>
      </header>

      {/* Sliding Features Section (kept as system overview visuals) */}
      <section className="slider-section">
        <h2>Features</h2>

        <div className="slider">
          <div className="slide-track">

            <div className="slide">
              <img src="https://samelane.com/wp-content/uploads/2023/07/skills-assessment.jpg" />
              <p>Skill Assessment</p>
            </div>

            <div className="slide">
              <img src="https://energymanagementsolutions.co.uk/wp-content/uploads/2024/04/shutterstock_443121277_5772697021947.jpg" />
              <p>AI-Based Recommendations</p>
            </div>

            <div className="slide">
              <img src="https://gulfleaderscircle.com/wp-content/uploads/2023/05/The-road-to-career-development.png" />
              <p>Mentor-Based Career Guidance</p>
            </div>

            <div className="slide">
              <img src="https://png.pngtree.com/thumb_back/fh260/background/20230613/pngtree-resume-and-resume-template-on-a-blue-table-image_2974658.jpg" />
              <p>ATS-Friendly Resume Builder</p>
            </div>

            <div className="slide">
              <img src="https://iticollege.edu/wp-content/uploads/2022/10/Practicing-The-Interview-e1665670606809.jpg" />
              <p>Mock Interviews</p>
            </div>

            {/* Duplicate slides for smooth animation */}
            <div className="slide">
              <img src="https://samelane.com/wp-content/uploads/2023/07/skills-assessment.jpg" />
              <p>Skill Assessment</p>
            </div>

            <div className="slide">
              <img src="https://energymanagementsolutions.co.uk/wp-content/uploads/2024/04/shutterstock_443121277_5772697021947.jpg" />
              <p>AI-Based Recommendations</p>
            </div>

            <div className="slide">
              <img src="https://gulfleaderscircle.com/wp-content/uploads/2023/05/The-road-to-career-development.png" />
              <p>Mentor-Based Career Guidance</p>
            </div>

            <div className="slide">
              <img src="https://png.pngtree.com/thumb_back/fh260/background/20230613/pngtree-resume-and-resume-template-on-a-blue-table-image_2974658.jpg" />
              <p>ATS-Friendly Resume Builder</p>
            </div>

            <div className="slide">
              <img src="https://iticollege.edu/wp-content/uploads/2022/10/Practicing-The-Interview-e1665670606809.jpg" />
              <p>Mock Interviews</p>
            </div>

          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="footer">
        <p>© 2026 Integrated Career Readiness System (ICRS)</p>
      </footer>

    </div>
  );
}

export default Home;
