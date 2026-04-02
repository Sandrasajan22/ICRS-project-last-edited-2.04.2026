export default function FollowStats({ profile, onFollowers, onFollowing }) {
  return (
    <div className="vp-stats">
      <span onClick={onFollowers}>
        <b>{profile.followers}</b> followers
      </span>

      <span onClick={onFollowing}>
        <b>{profile.following}</b> following
      </span>
    </div>
  );
}