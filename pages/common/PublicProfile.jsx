import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import FollowButton from "../../Components/FollowButton";
import FollowStats from "../../Components/FollowStats";
import FollowersModal from "../../Components/FollowersModal";
import "../../styles/publicprofile.css";

export default function PublicProfile() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const currentUserId = localStorage.getItem("userId");

  const [profile, setProfile] = useState(null);
  const [refresh, setRefresh] = useState(false);

  const [showFollowers, setShowFollowers] = useState(false);
  const [showFollowing, setShowFollowing] = useState(false);

  // 🔥 FETCH PROFILE
  useEffect(() => {
    fetch(`http://localhost:8080/api/profile/${userId}`)
      .then(res => res.json())
      .then(data => {
        console.log("PROFILE DATA:", data); // DEBUG
        setProfile(data);
      })
      .catch(err => console.error(err));
  }, [userId, refresh]);

  if (!profile) return <div className="vp-loading">Loading...</div>;

  return (
    <div className="vp-page">
      <div className="vp-card">

        {/* BACK BUTTON */}
        <button className="back-btn" onClick={() => navigate(-1)}>
          ← Back
        </button>

        {/* HEADER */}
        <div className="vp-top">

          {/* PROFILE IMAGE */}
          <div className="vp-avatar">
            {profile.profilePhoto ? (
              <img
                src={`http://localhost:8080${profile.profilePhoto}`}
                alt="profile"
              />
            ) : (
              <div className="vp-avatar-fallback">
                {profile.fullName?.[0]}
              </div>
            )}
          </div>

          {/* INFO */}
          <div className="vp-info">

            <div className="vp-headerRow">
              <h2>{profile.fullName || "No Name"}</h2>

              {/* FOLLOW BUTTON */}
              {currentUserId != profile.userId && (
                <FollowButton
                  userId={currentUserId}
                  targetId={profile.userId}
                  onChange={() => setRefresh(!refresh)}
                />
              )}
            </div>

            {/* FOLLOW STATS */}
            <FollowStats
              profile={profile}
              onFollowers={() => setShowFollowers(true)}
              onFollowing={() => setShowFollowing(true)}
            />

            {/* META */}
            <div className="vp-meta">
              <b>{profile.headline || "No headline"}</b>
              <p>{profile.location || "No location"}</p>
              <p>{profile.email}</p>
            </div>

          </div>
        </div>

        {/* BIO */}
        <div className="vp-section">
          <h4>About</h4>
          <p>{profile.bio || "No bio available"}</p>
        </div>
        {/* 🎓 ACADEMICS (Student / Job Seeker) */}
{(profile.degree || profile.university || profile.marks) && (
  <div className="vp-section">
    <h4>Academics</h4>
    <div className="vp-card-box">
      <p><b>Degree:</b> {profile.degree}</p>
      <p><b>University:</b> {profile.university}</p>
      <p><b>Marks:</b> {profile.marks}</p>
    </div>
  </div>
)}

{/* 📜 CERTIFICATIONS */}
{profile.bio?.includes("Certification") && (
  <div className="vp-section">
    <h4>Certifications</h4>
    <div className="vp-card-box">
      <pre className="vp-pre">{profile.bio}</pre>
    </div>
  </div>
)}

{/* 📚 TRAINER COURSES */}
{profile.category && (
  <div className="vp-section">
    <h4>Courses</h4>
    <div className="vp-card-box">
      <p><b>Category:</b> {profile.category}</p>
      <p><b>Description:</b> {profile.description}</p>
      <p><b>Mode:</b> {profile.mode}</p>
      <p><b>Duration:</b> {profile.duration}</p>
      <p><b>Fee:</b> {profile.fee}</p>
    </div>
  </div>
)}

        {/* SKILLS */}
        {profile.skills && profile.skills.length > 0 && (
          <div className="vp-section">
            <h4>Skills</h4>
            <div className="vp-skills">
              {profile.skills.map((s, i) => (
                <span key={i}>{s}</span>
              ))}
            </div>
          </div>
        )}

    {/* POSTS */}
<div className="vp-section">
  <h4>Posts</h4>

  <div className="vp-grid">
    {profile.posts && profile.posts.length > 0 ? (
      profile.posts.map(p => (
        <div key={p.id} className="vp-post">

          {p.mediaType === "VIDEO" ? (
            <video
              src={`http://localhost:8080${p.mediaUrl}`}
              controls
              className="vp-media"
            />
          ) : (
            <img
              src={`http://localhost:8080${p.mediaUrl}`}
              alt="post"
              className="vp-media"
            />
          )}

          {/* 🔥 CAPTION */}
          {p.caption && (
            <div className="vp-caption">
              {p.caption}
            </div>
          )}

        </div>
      ))
    ) : (
      <p className="vp-empty">No posts yet</p>
    )}
  </div>
</div>
        {/* MODALS */}
        {showFollowers && (
          <FollowersModal
            userId={profile.userId}
            type="followers"
            onClose={() => setShowFollowers(false)}
          />
        )}

        {showFollowing && (
          <FollowersModal
            userId={profile.userId}
            type="following"
            onClose={() => setShowFollowing(false)}
          />
        )}

      </div>
    </div>
  );
}