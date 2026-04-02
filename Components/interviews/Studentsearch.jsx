import { useEffect, useState } from "react";
import { getStudentSlots } from "./interviewapi";
import { getUserId } from "../../utils/auth";

export default function StudentSearch({ onSelectMentor }) {
  const [slots, setSlots] = useState([]);
  const [filter, setFilter] = useState("");
  const studentId = getUserId();

  useEffect(() => {
    getStudentSlots(studentId).then(r => setSlots(r.data || [])).catch(()=>setSlots([]));
  }, [studentId]);

  const mentors = Object.values(
    slots.reduce((acc, s) => {
      acc[s.mentorId] = s;
      return acc;
    }, {})
  );

  const filtered = mentors.filter(m =>
    (m.mentorName || "").toLowerCase().includes(filter.toLowerCase())
  );

  return (
    <div className="page">
      <input placeholder="Search mentor" onChange={e=>setFilter(e.target.value)} />
      {filtered.map(m => (
        <div className="card" key={m.mentorId} onClick={()=>onSelectMentor(m.mentorId)}>
          <h4>{m.mentorName}</h4>
          <p>{m.interviewType}</p>
        </div>
      ))}
    </div>
  );
}
