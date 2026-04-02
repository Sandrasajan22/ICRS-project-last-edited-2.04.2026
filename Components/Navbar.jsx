import { Link } from "react-router-dom";
import "../styles/navbar.css"; 

function Navbar() {
  return (
    <nav className="navbar">
      <div className="logo">
        <h2>ICRS</h2>
      </div>

      <ul className="nav-links">
        <li><Link to="/">Home</Link></li>
        <li><Link to="/about">About</Link></li>
        
        <li>
          <Link to="/login" className="signup-btn">
            Login
          </Link>
        </li>
      </ul>
    </nav>
  );
}

export default Navbar;
