import { useEffect, useState } from "react";
import "../styles/publicprofile.css";

export default function FollowersModal({ userId, type, onClose }) {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetch(`http://localhost:8080/api/follow/${type}/${userId}`)
      .then(res => res.json())
      .then(setUsers);
  }, [type, userId]);

  return (
    <div className="modal-bg" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>{type}</h3>

        {users.map(u => (
          <div key={u.id} className="modal-user">
            <b>{u.fname} {u.lname}</b>
            <span>{u.role}</span>
          </div>
        ))}
      </div>
    </div>
  );
}