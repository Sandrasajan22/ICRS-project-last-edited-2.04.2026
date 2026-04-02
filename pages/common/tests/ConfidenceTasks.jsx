import { useEffect, useMemo, useState } from "react";
import {
  getConfidenceLevelStatus,
  getConfidenceTasks,
  submitConfidenceTasks,
} from "./confidenceApi";
import ConfidenceTaskCard from "./ConfidenceCard";
import "./confidencetasks.css";

const LEVEL_OPTIONS = ["Beginner", "Intermediate", "Difficult"];

export default function ConfidenceTasks() {
  const [level, setLevel] = useState("");
  const [taskData, setTaskData] = useState(null);
  const [taskInputs, setTaskInputs] = useState({});
  const [result, setResult] = useState(null);
  const [levelStatus, setLevelStatus] = useState({
    Beginner: true,
    Intermediate: false,
    Difficult: false,
  });
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("user"));
    } catch {
      return null;
    }
  }, []);

  const userId = user?.id ?? user?.userId ?? null;
  const userName =
    user?.name || `${user?.fname || ""} ${user?.lname || ""}`.trim() || "User";

  useEffect(() => {
    if (userId) {
      fetchLevelStatus();
    }
  }, [userId]);

  const fetchLevelStatus = async () => {
    try {
      const data = await getConfidenceLevelStatus(userId);
      setLevelStatus(data);
    } catch (err) {
      console.error(err);
    }
  };

  const isLevelUnlocked = (selectedLevel) => {
    return !!levelStatus[selectedLevel];
  };

  const handleLoadTasks = async () => {
    if (!level) {
      setErrorMsg("Please select a level.");
      return;
    }

    if (!userId) {
      setErrorMsg("User not found. Please login again.");
      return;
    }

    try {
      setLoading(true);
      setErrorMsg("");
      setResult(null);
      setTaskInputs({});

      const data = await getConfidenceTasks(userId, level);
      setTaskData(data);

      const initialInputs = {};
      (data.tasks || []).forEach((task, index) => {
        initialInputs[index] = {
          completed: false,
          timeTaken: "",
          selfRating: "",
        };
      });
      setTaskInputs(initialInputs);
    } catch (err) {
      console.error(err);
      setErrorMsg(
        err?.response?.data || "Failed to load confidence tasks."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleTaskChange = (index, field, value) => {
    setTaskInputs((prev) => ({
      ...prev,
      [index]: {
        ...prev[index],
        [field]: value,
      },
    }));
  };

  const handleSubmit = async () => {
    if (!taskData?.tasks?.length) {
      setErrorMsg("No tasks loaded.");
      return;
    }

    try {
      setLoading(true);
      setErrorMsg("");

     const payload = {
  userId,
  name: userName,
  level,
  setNumber: taskData.set_number,
  tasks: taskData.tasks.map((task, index) => ({
    task: task.task,
    instruction: task.instruction,
    expectedTime: task.expected_time,
    difficulty: task.difficulty,
    skillFocus: task.skill_focus,
    completed: !!taskInputs[index]?.completed,
    timeTaken:
      taskInputs[index]?.timeTaken === ""
        ? null
        : Number(taskInputs[index]?.timeTaken),
    selfRating:
      taskInputs[index]?.selfRating === ""
        ? null
        : Number(taskInputs[index]?.selfRating),
  })),
};
      const response = await submitConfidenceTasks(payload);
      setResult(response);
      await fetchLevelStatus();
    } catch (err) {
      console.error(err);
      setErrorMsg(err?.response?.data || "Failed to submit confidence tasks.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="conf-page-bg">
      <div className="conf-container">
        <div className="conf-topbar">
          <div>
            <h2>Confidence Task Arena</h2>
            <p className="conf-subtitle">
              Complete practice tasks and unlock higher levels
            </p>
          </div>

          <button className="conf-back-btn" onClick={() => window.history.back()}>
            ← Back
          </button>
        </div>

        <div className="conf-player-card">
          <div className="conf-player-badge">
            <span className="conf-player-label">Candidate</span>
            <strong>{userName}</strong>
          </div>

          <div className="conf-player-badge">
            <span className="conf-player-label">Role</span>
            <strong>{user?.role || "User"}</strong>
          </div>
        </div>

        <div className="conf-filters">
          <select value={level} onChange={(e) => setLevel(e.target.value)}>
            <option value="">Select Level</option>
            {LEVEL_OPTIONS.map((item) => (
              <option key={item} value={item} disabled={!isLevelUnlocked(item)}>
                {item} {!isLevelUnlocked(item) ? "🔒" : "✅"}
              </option>
            ))}
          </select>

          <button onClick={handleLoadTasks} className="conf-btn" disabled={loading}>
            {loading ? "Loading..." : "Load Tasks"}
          </button>
        </div>

        {errorMsg && <div className="conf-error-box">{errorMsg}</div>}

        <div className="conf-level-status-box">
          <h3>🏆 Level Unlock Status</h3>
          <div className="conf-level-grid">
            <div className="conf-level-tile unlocked">
              <span>Beginner</span>
              <strong>Unlocked</strong>
            </div>

            <div
              className={`conf-level-tile ${
                levelStatus.Intermediate ? "unlocked" : "locked"
              }`}
            >
              <span>Intermediate</span>
              <strong>{levelStatus.Intermediate ? "Unlocked" : "Locked"}</strong>
            </div>

            <div
              className={`conf-level-tile ${
                levelStatus.Difficult ? "unlocked" : "locked"
              }`}
            >
              <span>Difficult</span>
              <strong>{levelStatus.Difficult ? "Unlocked" : "Locked"}</strong>
            </div>
          </div>
          <p className="conf-unlock-note">
            To unlock the next level, get at least <strong>60%</strong> completion
            score in the previous level.
          </p>
        </div>

        {taskData && (
          <>
            <div className="conf-header-box">
              <div>
                <h3>{taskData.level} Confidence Tasks</h3>
                <p>Assigned Set: {taskData.set_number}</p>
              </div>
            </div>

            <div className="conf-task-list">
              {taskData.tasks.map((task, index) => (
                <ConfidenceTaskCard
                  key={index}
                  index={index}
                  task={task}
                  values={taskInputs[index] || {}}
                  onChange={handleTaskChange}
                />
              ))}
            </div>

            <button className="conf-submit-btn" onClick={handleSubmit} disabled={loading}>
              {loading ? "Submitting..." : "Submit Tasks"}
            </button>
          </>
        )}

        {result && (
          <div className="conf-result-box">
            <h3>🎯 Result</h3>
            <p>Completion Score: {result.completionScore}%</p>
            <p>Confidence Score: {result.confidenceScore}%</p>
            <p>Consistency Score: {result.consistencyScore}%</p>
            <p>
              Final Evaluation: <strong>{result.finalEvaluation}</strong>
            </p>
          </div>
        )}
      </div>
    </div>
  );
}