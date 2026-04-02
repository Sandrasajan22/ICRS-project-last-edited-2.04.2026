import { useState } from "react";

export default function QuestionCard({ questions, submitTest }) {
  const [answers, setAnswers] = useState({});

  const handleChange = (i, value) => {
    setAnswers({ ...answers, [i]: value });
  };

  return (
    <div>
      {questions.map((q, i) => (
        <div key={i} className="card">
          <p>{q.question}</p>

          {q.options.map((opt, j) => (
            <label key={j}>
              <input
                type="radio"
                name={i}
                onChange={() => handleChange(i, opt)}
              />
              {opt}
            </label>
          ))}
        </div>
      ))}

      {questions.length > 0 && (
        <button onClick={() => submitTest(answers)}>Submit</button>
      )}
    </div>
  );
}