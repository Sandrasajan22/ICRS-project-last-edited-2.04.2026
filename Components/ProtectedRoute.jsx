import { Navigate, useLocation } from "react-router-dom";

function ProtectedRoute({ children, role }) {
  const isAuthenticated = localStorage.getItem("isAuthenticated");
  const userRole = localStorage.getItem("role");
  const location = useLocation();

  // ❌ Not logged in
  if (!isAuthenticated) {
    alert("Please login to continue");
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // ❌ Logged in but role mismatch
  if (role && userRole !== role) {
    alert("You are not authorized to access this page");
    return <Navigate to="/" replace />;
  }

  // ✅ Authorized
  return children;
}

export default ProtectedRoute;
