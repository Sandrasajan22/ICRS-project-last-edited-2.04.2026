import React, { useState, useEffect } from "react";
import "../../styles/adminanalytics.css";

const BASE_URL = "http://localhost:8080/api/admin/analytics/overview";

export default function AdminAnalytics() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const res = await fetch(BASE_URL);
      if (res.ok) {
        const json = await res.json();
        setData(json);
      }
    } catch (err) {
      console.error("Failed to fetch analytics", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="admin-loading">Loading Analytics Dashboard...</div>;
  }

  if (!data) {
    return <div className="admin-loading">No data available. Try refreshing.</div>;
  }

  // Find max value mapping for dynamic chart scaling
  const maxRegistrations = Math.max(...data.monthlyRegistration.map(m => m.value), 1);

  return (
    <div className="admin-analytics-container">
      <div className="admin-analytics-header">
        <h1>Overview & Analytics</h1>
        <p>Real-time platform health and monthly user growth.</p>
      </div>

      <div className="analytics-kpi-grid">
        <div className="kpi-card general">
          <h3>Total Users</h3>
          <div className="kpi-value">{data.totalUsers}</div>
          <p>Total registered accounts</p>
        </div>
        <div className="kpi-card success">
          <h3>Active Users</h3>
          <div className="kpi-value">{data.activeUsers}</div>
          <p>Current active members</p>
        </div>
        <div className="kpi-card danger">
          <h3>Blocked Users</h3>
          <div className="kpi-value">{data.blockedUsers}</div>
          <p>Accounts with revoked access</p>
        </div>
        <div className="kpi-card info">
          <h3>Verified Users</h3>
          <div className="kpi-value">{data.verifiedUsers}</div>
          <p>Fully approved and verified</p>
        </div>
      </div>

      <div className="analytics-main-grid">
        <div className="analytics-box chart-box">
          <h2>Registration Timeline (Last 6 Months)</h2>
          <div className="bar-chart-container">
            {data.monthlyRegistration.map((month, index) => {
              const heightPercent = (month.value / maxRegistrations) * 100;
              return (
                <div className="bar-wrapper" key={index}>
                  <div className="bar-track">
                    <div 
                      className="bar-fill" 
                      style={{ 
                        height: `${heightPercent}%`, 
                        animationDelay: `${index * 0.1}s` 
                      }}
                    >
                      <div className="bar-tooltip">{month.value} users</div>
                    </div>
                  </div>
                  <span className="bar-label">{month.name}</span>
                </div>
              );
            })}
          </div>
        </div>

        <div className="analytics-box role-breakdown-box">
          <h2>User Roles Distribution</h2>
          <div className="role-lists">
            {data.roleData.length === 0 ? (
              <p className="no-roles-msg">No roles assigned yet.</p>
            ) : (
              data.roleData.map((roleObj, i) => (
                <div className="role-row" key={i}>
                  <span className="role-name">{roleObj.name}</span>
                  <div className="role-bar-bg">
                    <div 
                      className="role-bar-fill"
                      style={{ 
                        width: `${Math.max((roleObj.value / data.totalUsers) * 100, 1)}%`,
                        animationDelay: `${i * 0.15}s`
                      }}
                    ></div>
                  </div>
                  <span className="role-value">{roleObj.value}</span>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
