import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import "../../../styles/communicationtests.css";

const API_BASE = "http://localhost:8080/api";
const TEST_DURATION_SECONDS = 10 * 60;

const STREAM_OPTIONS = [
  "Grammar",
  "Vocabulary",
  "Business Communication",
  "Email Writing",
  "Speaking Skills",
  "Aptitude English",
];

const LEVEL_OPTIONS = ["Beginner", "Intermediate", "Difficult"];

export default function CommunicationTest() {
  const [stream, setStream] = useState("");
  const [level, setLevel] = useState("");
  const [testData, setTestData] = useState(null);
  const [answers, setAnswers] = useState({});
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const [timeLeft, setTimeLeft] = useState(TEST_DURATION_SECONDS);
  const [testStarted, setTestStarted] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const [levelStatus, setLevelStatus] = useState({
    Beginner: true,
    Intermediate: false,
    Difficult: false,
  });

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("user"));
    } catch {
      return null;
    }
  }, []);

  useEffect(() => {
    setLevel("");
    resetTestState();

    if (stream) {
      fetchLevelStatus(stream);
    } else {
      setLevelStatus({
        Beginner: true,
        Intermediate: false,
        Difficult: false,
      });
    }
  }, [stream]);

  useEffect(() => {
    if (!testStarted || submitted) return;

    if (timeLeft <= 0) {
      submitTest(true);
      return;
    }

    const interval = setInterval(() => {
      setTimeLeft((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(interval);
  }, [testStarted, submitted, timeLeft]);

  const resetTestState = () => {
    setTestData(null);
    setAnswers({});
    setResult(null);
    setSubmitted(false);
    setTestStarted(false);
    setTimeLeft(TEST_DURATION_SECONDS);
  };

  const fetchLevelStatus = async (selectedStream) => {
    try {
      if (!user?.id || !selectedStream) return;

      const res = await axios.get(`${API_BASE}/communication-test/level-status`, {
        params: {
          userId: user.id,
          stream: selectedStream,
        },
      });

      setLevelStatus(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const isLevelUnlocked = (selectedLevel) => {
    return levelStatus[selectedLevel];
  };

  const answeredCount = Object.keys(answers).length;
  const progressPercent = testData?.questions?.length
    ? (answeredCount / testData.questions.length) * 100
    : 0;

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
  };

  const loadQuestions = async () => {
    if (!stream || !level) {
      alert("Please select stream and level");
      return;
    }

    try {
      setLoading(true);

      const response = await axios.get(`${API_BASE}/communication-test/random`, {
        params: {
          userId: user?.id || null,
          stream,
          level,
        },
      });

      setTestData(response.data);
      setAnswers({});
      setResult(null);
      setSubmitted(false);
      setTimeLeft(TEST_DURATION_SECONDS);
      setTestStarted(true);
    } catch (error) {
      console.error(error);
      alert(error?.response?.data || "Failed to load communication questions");
    } finally {
      setLoading(false);
    }
  };

  const handleOptionChange = (questionId, selectedOption) => {
    if (submitted) return;

    setAnswers((prev) => ({
      ...prev,
      [questionId]: selectedOption,
    }));
  };

  const submitTest = async (autoSubmit = false) => {
    if (!testData || submitted) return;

    try {
      const payload = {
        userId: user?.id || null,
        name:
          user?.name ||
          `${user?.fname || ""} ${user?.lname || ""}`.trim() ||
          "Anonymous",
        stream,
        skill: "communication",
        level,
        setNumber: testData.set_number,
        answers: Object.entries(answers).map(([questionId, selectedAnswer]) => ({
          questionId: Number(questionId),
          selectedAnswer,
        })),
      };

      const response = await axios.post(
        `${API_BASE}/communication-test/submit`,
        payload
      );

      setResult(response.data);
      setSubmitted(true);
      setTestStarted(false);

      await fetchLevelStatus(stream);

      if (autoSubmit) {
        alert("Time is up. Test submitted automatically.");
      }
    } catch (error) {
      console.error(error);
      alert(error?.response?.data || "Failed to submit communication test");
    }
  };

  return (
    <div className="comm-page-bg">
      <div className="comm-container">
        <div className="comm-topbar">
          <div>
            <h2>Communication Test Arena</h2>
            <p className="comm-subtitle">
              Improve workplace communication and unlock higher levels
            </p>
          </div>

          <button className="comm-back-btn" onClick={() => window.history.back()}>
            ← Back
          </button>
        </div>

        <div className="comm-player-card">
          <div className="comm-player-badge">
            <span className="comm-player-label">Candidate</span>
            <strong>{user?.name || user?.fname || "Guest"}</strong>
          </div>

          <div className="comm-player-badge">
            <span className="comm-player-label">Role</span>
            <strong>{user?.role || "User"}</strong>
          </div>
        </div>

        <div className="comm-filters">
          <select value={stream} onChange={(e) => setStream(e.target.value)}>
            <option value="">Select Stream</option>
            {STREAM_OPTIONS.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>

          <select
            value={level}
            onChange={(e) => setLevel(e.target.value)}
            disabled={!stream}
          >
            <option value="">Select Level</option>
            {LEVEL_OPTIONS.map((item) => (
              <option key={item} value={item} disabled={!isLevelUnlocked(item)}>
                {item} {!isLevelUnlocked(item) ? "🔒" : "✅"}
              </option>
            ))}
          </select>

          <button onClick={loadQuestions} className="comm-btn" disabled={loading}>
            {loading ? "Loading..." : "Start Quest"}
          </button>
        </div>

        {stream && (
          <div className="comm-level-status-box">
            <h3>🏆 Level Unlock Status</h3>
            <div className="comm-level-grid">
              <div className="comm-level-tile unlocked">
                <span>Beginner</span>
                <strong>Unlocked</strong>
              </div>

              <div
                className={`comm-level-tile ${
                  levelStatus.Intermediate ? "unlocked" : "locked"
                }`}
              >
                <span>Intermediate</span>
                <strong>
                  {levelStatus.Intermediate ? "Unlocked" : "Locked"}
                </strong>
              </div>

              <div
                className={`comm-level-tile ${
                  levelStatus.Difficult ? "unlocked" : "locked"
                }`}
              >
                <span>Difficult</span>
                <strong>{levelStatus.Difficult ? "Unlocked" : "Locked"}</strong>
              </div>
            </div>
            <p className="comm-unlock-note">
              To unlock the next level, score at least <strong>Good communication</strong> in the previous one.
            </p>
          </div>
        )}

        {testData && (
          <div>
            <div className="comm-header-box">
              <div>
                <h3>
                  {testData.stream} • {testData.level}
                </h3>
                <p>Assigned Set: {testData.set_number}</p>
              </div>

              <div className="comm-timer-box">
                <span>⏳ Time Left</span>
                <strong>{formatTime(timeLeft)}</strong>
              </div>
            </div>

            <div className="comm-progress-wrapper">
              <div className="comm-progress-label">
                <span>Progress: {answeredCount}/10 answered</span>
                <span>{Math.round(progressPercent)}%</span>
              </div>
              <div className="comm-progress-bar">
                <div
                  className="comm-progress-fill"
                  style={{ width: `${progressPercent}%` }}
                ></div>
              </div>
            </div>

            {testData.questions.map((q, index) => (
              <div key={q.id} className="comm-question-card">
                <p className="comm-question-title">
                  <strong>
                    Q{index + 1}. {q.question}
                  </strong>
                </p>

                {q.options.map((option, idx) => (
                  <label key={idx} className="comm-option">
                    <input
                      type="radio"
                      name={`question-${q.id}`}
                      value={option}
                      checked={answers[q.id] === option}
                      onChange={() => handleOptionChange(q.id, option)}
                      disabled={submitted}
                    />
                    <span>{option}</span>
                  </label>
                ))}
              </div>
            ))}

            {!submitted && (
              <button onClick={() => submitTest(false)} className="comm-submit-btn">
                Submit Quest
              </button>
            )}
          </div>
        )}

        {result && (
          <div className="comm-result-box">
            <h3>🎯 Result</h3>
            <p>Set Score: {result.setScore}/10</p>
            <p>Level Score: {result.levelScore}</p>
            <p>
              Final Evaluation: <strong>{result.finalEvaluation}</strong>
            </p>

            {result.finalEvaluation === "Excellent" && (
              <p className="comm-result-tag excellent">
                🌟 Excellent! You dominated this level.
              </p>
            )}
            {result.finalEvaluation === "Good communication" && (
              <p className="comm-result-tag good">
                ✅ Great work! Next level unlocked.
              </p>
            )}
            {result.finalEvaluation === "Basic understanding" && (
              <p className="comm-result-tag retry">
                🔁 Retry this level to unlock the next stage.
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}