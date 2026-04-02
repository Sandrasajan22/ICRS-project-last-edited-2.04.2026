import React, { useState, useEffect } from "react";
import "../../styles/adminusers.css";

const BASE_URL = "http://localhost:8080/api/admin/users";

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [roleFilter, setRoleFilter] = useState("all");

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await fetch(BASE_URL);
      if (res.ok) {
        const data = await res.json();
        setUsers(data);
      }
    } catch (err) {
      console.error("Failed to fetch users", err);
    } finally {
      setLoading(false);
    }
  };

  const toggleBlock = async (userId) => {
    try {
      const res = await fetch(`${BASE_URL}/${userId}/toggle-block`, {
        method: "POST"
      });
      if (res.ok) {
        setUsers(users.map(u => {
          if (u.id === userId) {
            return { ...u, blocked: !u.blocked };
          }
          return u;
        }));
      } else {
        const data = await res.json();
        alert(data.message || "Action failed");
      }
    } catch (err) {
      console.error("Failed to block/unblock", err);
    }
  };

  const filteredUsers = users.filter(user => {
    const matchesSearch = 
      (user.fname?.toLowerCase() || "").includes(searchQuery.toLowerCase()) ||
      (user.lname?.toLowerCase() || "").includes(searchQuery.toLowerCase()) ||
      (user.email?.toLowerCase() || "").includes(searchQuery.toLowerCase());
    
    const matchesRole = roleFilter === "all" || user.role === roleFilter;

    return matchesSearch && matchesRole;
  });

  return (
    <div className="admin-users-container">
      <div className="admin-users-header">
        <h1>User Management</h1>
        <p>Manage all registered accounts, roles, and access controls across the platform.</p>
      </div>

      <div className="admin-users-controls">
        <input 
          type="text" 
          placeholder="Search by name or email..." 
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="admin-search-input"
        />
        <select 
          value={roleFilter} 
          onChange={(e) => setRoleFilter(e.target.value)}
          className="admin-role-select"
        >
          <option value="all">All Roles</option>
          <option value="student">Student</option>
          <option value="job_seeker">Job Seeker</option>
          <option value="mentor">Mentor</option>
          <option value="trainer">Trainer</option>
          <option value="employer">Employer</option>
          <option value="institution">Institution</option>
          <option value="admin">Admin</option>
        </select>
      </div>

      {loading ? (
        <div className="admin-loading">Loading users...</div>
      ) : (
        <div className="admin-table-wrapper">
          <table className="admin-users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Verification</th>
                <th>Access Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan="7" className="admin-no-users">No users found matching your criteria.</td>
                </tr>
              ) : (
                filteredUsers.map(user => (
                  <tr key={user.id} className={user.blocked ? "row-blocked" : ""}>
                    <td className="admin-td-id">#{user.id}</td>
                    <td>
                      <div className="admin-user-name">
                        {user.fname || "Unknown"} {user.lname || ""}
                      </div>
                    </td>
                    <td><div className="admin-user-email">{user.email}</div></td>
                    <td>
                      <span className={`admin-role-badge role-${user.role || 'default'}`}>
                        {(user.role || 'Unknown').replace("_", " ").toUpperCase()}
                      </span>
                    </td>
                    <td>
                      <span className={`admin-status-badge ${user.verified ? "verified" : "pending"}`}>
                        {user.verified ? "Verified" : (user.verificationStatus || "Pending")}
                      </span>
                    </td>
                    <td>
                      {user.blocked ? (
                        <div className="admin-access-badge blocked">Blocked</div>
                      ) : (
                        <div className="admin-access-badge active">Active</div>
                      )}
                    </td>
                    <td className="admin-td-actions">
                      {user.role !== "admin" ? (
                        <button 
                          className={`admin-action-btn ${user.blocked ? "unblock-btn" : "block-btn"}`}
                          onClick={() => toggleBlock(user.id)}
                        >
                          {user.blocked ? "Unblock Access" : "Block User"}
                        </button>
                      ) : (
                        <span className="admin-protected-text">Protected</span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
