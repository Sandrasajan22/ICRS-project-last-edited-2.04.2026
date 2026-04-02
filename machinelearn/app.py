"""
ICRS AI Flask API
Bridges the Python ML engine to the Java Spring Boot backend.
Run: python app.py
Runs on http://127.0.0.1:5000
"""
from flask import Flask, request, jsonify
from flask_cors import CORS
from icrs_ai_engine import engine

app = Flask(__name__)
CORS(app)  # Allow requests from Java backend / React frontend


# ============================================================
# POST /dashboard
# Body: { user_data: { ...scores, role, skills, liked_tags } }
# Returns full performance dashboard payload
# ============================================================
@app.route('/dashboard', methods=['POST'])
def dashboard():
    body = request.get_json(force=True)
    user_data = body.get('user_data', {})
    if not user_data:
        return jsonify({'error': 'user_data is required'}), 400
    result = engine.generate_dashboard(user_data)
    return jsonify(result)


# ============================================================
# POST /recommend
# Body: { user_data: { ...scores } }
# Returns course recommendations and skill improvement tips
# ============================================================
@app.route('/recommend', methods=['POST'])
def recommend():
    body = request.get_json(force=True)
    user_data = body.get('user_data', {})
    if not user_data:
        return jsonify({'error': 'user_data is required'}), 400
    result = engine.recommend(user_data)
    return jsonify(result)


# ============================================================
# POST /feed/rank
# Body: { user_data: {...}, posts: [...], user_type: "student" }
# Returns posts sorted by ML relevance score
# ============================================================
@app.route('/feed/rank', methods=['POST'])
def rank_feed():
    body = request.get_json(force=True)
    user_data = body.get('user_data', {})
    posts = body.get('posts', [])
    user_type = body.get('user_type', 'student')

    if not posts:
        return jsonify([])

    if user_type in ('student', 'job_seeker'):
        ranked = engine.rank_personalized_feed(user_data, posts)
    else:
        ranked = engine.rank_general_feed(user_data, posts)

    return jsonify(ranked)


# ============================================================
# GET /predict/<userId>
# Simple skill gap prediction for a userId
# Looks up user_data from request params
# ============================================================
@app.route('/predict', methods=['POST'])
def predict():
    body = request.get_json(force=True)
    user_data = body.get('user_data', {})
    result = engine.predict_skill_gap(user_data)
    return jsonify(result)


# ============================================================
# GET /health
# Health check — used by Java backend to verify Flask is up
# ============================================================
@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok', 'model_trained': engine.is_trained})


if __name__ == '__main__':
    print("[ICRS Flask AI Server] Starting on port 5000...")
    app.run(host='0.0.0.0', port=5000, debug=False)
