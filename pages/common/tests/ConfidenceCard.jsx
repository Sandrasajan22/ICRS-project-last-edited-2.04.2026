export default function ConfidenceTaskCard({ index, task, values, onChange }) {
  return (
    <div className="conf-task-card">
      <div className="conf-task-head">
        <h4>
          Task {index + 1}: {task.task}
        </h4>
        <span className="conf-task-focus">{task.skill_focus}</span>
      </div>

      <p className="conf-task-instruction">{task.instruction}</p>

      <div className="conf-task-meta">
        <span>Expected Time: {task.expected_time} min</span>
        <span>Difficulty: {task.difficulty}</span>
      </div>

      <div className="conf-task-controls">
        <label className="conf-checkbox">
          <input
            type="checkbox"
            checked={!!values.completed}
            onChange={(e) => onChange(index, "completed", e.target.checked)}
          />
          <span>Completed</span>
        </label>

        <div className="conf-input-group">
          <label>Time Taken (min)</label>
          <input
            type="number"
            min="0"
            value={values.timeTaken ?? ""}
            onChange={(e) => onChange(index, "timeTaken", e.target.value)}
          />
        </div>

        <div className="conf-input-group">
          <label>Self Rating (1-5)</label>
          <select
            value={values.selfRating ?? ""}
            onChange={(e) => onChange(index, "selfRating", e.target.value)}
          >
            <option value="">Select</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
          </select>
        </div>
      </div>
    </div>
  );
}