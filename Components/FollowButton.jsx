import { useEffect, useState } from "react";
import { followUser, unfollowUser, checkFollow } from "../utils/followApi";
import "../styles/follow.css";

export default function FollowButton({ userId, targetId, onChange }) {
  const [isFollowing, setIsFollowing] = useState(false);

  useEffect(() => {
    load();
  }, [targetId]);

  const load = async () => {
    const status = await checkFollow(userId, targetId);
    setIsFollowing(status);
  };

  const handleClick = async () => {
    if (isFollowing) {
      const ok = window.confirm("Unfollow this user?");
      if (!ok) return;

      await unfollowUser(userId, targetId);
      setIsFollowing(false);
    } else {
      await followUser(userId, targetId);
      setIsFollowing(true);
    }

    onChange && onChange();
  };

  return (
    <button className={`follow-btn ${isFollowing ? "unfollow" : "follow"}`} onClick={handleClick}>
      {isFollowing ? "Following" : "Follow"}
    </button>
  );
}