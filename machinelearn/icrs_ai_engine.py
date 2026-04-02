"""
ICRS AI Engine - Full Implementation
Uses Random Forest for skill gap prediction,
TF-IDF cosine similarity for content-based feed ranking,
and rule-based recommendation generation.
"""
import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer
import warnings
warnings.filterwarnings('ignore')


FEATURE_COLUMNS = [
    'technical_score', 'communication_score', 'confidence_score',
    'aptitude_score', 'projects_count', 'internships_count',
    'certifications_count', 'coding_platform_score', 'mock_interview_score',
    'resume_score', 'linkedin_score', 'follows_count'
]

# Skill gap to relevant post tag mapping (for content-based filtering)
SKILL_TAG_MAP = {
    'technical':     ['technical', 'coding', 'programming', 'algorithms', 'system design', 'data structures'],
    'communication': ['communication', 'soft skills', 'presentation', 'linkedin', 'networking', 'speaking'],
    'confidence':    ['confidence', 'interview', 'mock interview', 'mindset', 'leadership', 'public speaking'],
    'none':          ['career', 'general', 'tips', 'industry trends', 'growth']
}

# Score thresholds for generating specific warnings
SCORE_THRESHOLDS = {
    'critical': 3,   # Score <= 3 → critical improvement
    'low':      5,   # Score <= 5 → needs improvement
    'good':     8    # Score >= 8 → strong
}


class ICRSAIEngine:
    """
    ICRS AI Engine - Full Recommendation, Analytics, and Feed Ranking System.
    Uses Random Forest for skill gap prediction and TF-IDF for content similarity.
    """

    def __init__(self):
        self.rf_model = RandomForestClassifier(
            n_estimators=200,
            max_depth=8,
            min_samples_split=2,
            random_state=42,
            class_weight='balanced'  # handles imbalance across skill gap classes
        )
        self.label_encoder = LabelEncoder()
        self.tfidf_vectorizer = TfidfVectorizer(max_features=500)
        self.is_trained = False
        self.dataset_df = None
        self.feature_importances = {}

    # =====================================================================
    # 1. MODEL TRAINING
    # =====================================================================
    def train(self, data: str = r'F:\frontend\machinelearn\final_ml_dataset.csv'):
        """Train the RF model using the provided CSV dataset."""
        df = pd.read_csv(data)
        self.dataset_df = df

        X = df[FEATURE_COLUMNS].fillna(0)
        y = self.label_encoder.fit_transform(df['skill_gap'])

        # Simple train/test split for accuracy (safer than cross_val on small datasets)
        try:
            X_train, X_test, y_train, y_test = train_test_split(
                X, y, test_size=0.2, random_state=42, stratify=y
            )
            self.rf_model.fit(X_train, y_train)
            accuracy = self.rf_model.score(X_test, y_test)
            print(f"[ICRS AI] Validation Accuracy: {accuracy:.2f}")
        except Exception:
            pass  # Not enough samples for split — just train on all data

        # Final fit on full dataset for best prediction quality
        self.rf_model.fit(X, y)
        self.is_trained = True

        # Save feature importances
        self.feature_importances = dict(zip(FEATURE_COLUMNS, self.rf_model.feature_importances_))

        print(f"[ICRS AI] Model trained successfully.")
        print(f"[ICRS AI] Top feature: {max(self.feature_importances, key=self.feature_importances.get)}")

    # =====================================================================
    # 2. SKILL GAP PREDICTION
    # =====================================================================
    def predict_skill_gap(self, user_data: dict) -> dict:
        """
        Predict skill gap for a user and return probabilities for all gaps.
        Returns: { predicted_gap, probabilities, confidence_score }
        """
        if not self.is_trained:
            self.train()

        row = {col: user_data.get(col, 0) for col in FEATURE_COLUMNS}
        df = pd.DataFrame([row])

        pred_idx = self.rf_model.predict(df)[0]
        proba = self.rf_model.predict_proba(df)[0]

        predicted_gap = self.label_encoder.inverse_transform([pred_idx])[0]
        model_confidence = round(float(max(proba)) * 100, 1)

        # Build probability for all classes
        class_probabilities = {
            self.label_encoder.inverse_transform([i])[0]: round(float(p) * 100, 1)
            for i, p in enumerate(proba)
        }

        return {
            'predicted_gap': predicted_gap,
            'confidence': model_confidence,
            'class_probabilities': class_probabilities
        }

    # =====================================================================
    # 3. PERSONALIZED FEED (Students / Jobseekers) - ML powered
    # =====================================================================
    def rank_personalized_feed(self, user_data: dict, posts: list) -> list:
        """
        ML-powered content-based feed ranking using TF-IDF cosine similarity.
        Combines: skill gap relevance + tag matching + engagement signals.
        """
        if not posts:
            return []

        prediction = self.predict_skill_gap(user_data)
        predicted_gap = prediction['predicted_gap']

        # Build a user interest profile string from their gap + skills
        user_gap_tags = SKILL_TAG_MAP.get(predicted_gap, [])
        user_profile_text = ' '.join(
            user_gap_tags +
            (user_data.get('skills') if isinstance(user_data.get('skills'), list) else []) +
            (user_data.get('liked_tags') if isinstance(user_data.get('liked_tags'), list) else [])
        )

        # Build TF-IDF corpus: user profile + all post tag strings
        post_texts = [' '.join(
            (p.get('tags') if isinstance(p.get('tags'), list) else []) + 
            (p.get('keywords') if isinstance(p.get('keywords'), list) else [])
        ) for p in posts]
        corpus = [user_profile_text] + post_texts

        # Compute cosine similarity between user profile and each post
        try:
            tfidf_matrix = self.tfidf_vectorizer.fit_transform(corpus)
            similarity_scores = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:])[0]
        except Exception:
            similarity_scores = np.zeros(len(posts))

        # Score each post combining similarity + engagement + role match
        followed_set = set(user_data.get('following', []))
        role = user_data.get('role', '')

        scored_posts = []
        for i, post in enumerate(posts):
            score = similarity_scores[i] * 40            # ML similarity (max 40 pts)
            score += post.get('likes', 0) * 0.5          # engagement
            score += post.get('comments', 0) * 1.2       # comments weighted higher
            if post.get('author_id') in followed_set:
                score += 25                               # following boost
            if role in post.get('target_audience', []):
                score += 15                               # role targeting boost
            scored_posts.append({**post, '_score': round(score, 2)})

        scored_posts.sort(key=lambda x: x['_score'], reverse=True)
        return scored_posts

    # =====================================================================
    # 4. FEED FOR OTHER USERS (Mentor/Employer/Trainer/Institution)
    # =====================================================================
    def rank_general_feed(self, user_data: dict, posts: list) -> list:
        """
        For non-student users: rank posts by engagement and followed users.
        """
        if not posts:
            return []

        followed_set = set(user_data.get('following', []))

        scored_posts = []
        for post in posts:
            score = 0
            if post.get('author_id') in followed_set:
                score += 100                                  # followed author = top priority
            score += post.get('likes', 0) * 1.0
            score += post.get('comments', 0) * 2.5
            score += post.get('views', 0) * 0.2
            score += post.get('author_followers', 0) * 0.05   # virality signal
            scored_posts.append({**post, '_score': round(score, 2)})

        scored_posts.sort(key=lambda x: x['_score'], reverse=True)
        return scored_posts

    # =====================================================================
    # 5. RECOMMENDATION SYSTEM (Skill Courses + Interview)
    # =====================================================================
    def recommend(self, user_data: dict) -> dict:
        """
        Full recommendation payload based on ML-predicted skill gap and raw score analysis.
        """
        prediction = self.predict_skill_gap(user_data)
        gap = prediction['predicted_gap']

        # Low score warnings (rule-based layer on top of ML)
        low_score_warnings = []
        skill_map = {
            'technical_score': 'Technical Skills',
            'communication_score': 'Communication',
            'confidence_score': 'Confidence',
            'aptitude_score': 'Aptitude/Problem Solving',
            'projects_count': 'Portfolio Size',
            'coding_platform_score': 'Coding Platform Stats',
            'resume_score': 'Resume Depth',
            'internships_count': 'Internship Experience',
            'certifications_count': 'Certification Depth',
            'linkedin_score': 'LinkedIn Profile',
            'follows_count': 'Network/Followers',
        }
        for field, label in skill_map.items():
            val = user_data.get(field, 0)
            if val <= SCORE_THRESHOLDS['critical']:
                low_score_warnings.append(f"⚠️ CRITICAL: {label} is very low ({val}/10). Immediate focus required.")
            elif val <= SCORE_THRESHOLDS['low']:
                low_score_warnings.append(f"ℹ️ {label} needs improvement ({val}/10).")

        # Gap-specific recommendations lookup
        RECOMMENDATIONS = {
            'technical': {
                'courses': [
                    'Data Structures & Algorithms (Coursera)',
                    'System Design Fundamentals (Udemy)',
                    'LeetCode Curated 75 Problem Set'
                ],
                'exercises': [
                    'Solve 2 LeetCode problems daily',
                    'Build a project using a new framework',
                    'Participate in Codeforces contests'
                ],
                'interview_tips': [
                    'Practice whiteboard coding',
                    'Study Big-O complexity analysis',
                    'Do at least 3 mock technical interviews'
                ],
                'focus': 'Prioritize building coding hands-on experience. Projects and consistent coding practice are the fastest way to improve your technical score.'
            },
            'communication': {
                'courses': [
                    'Effective Business Communication (LinkedIn Learning)',
                    'Public Speaking Masterclass (Udemy)',
                    'Professional Writing Workshop'
                ],
                'exercises': [
                    'Record yourself speaking for 2 minutes daily',
                    'Join Toastmasters or a speaking club',
                    'Write a LinkedIn post once a week'
                ],
                'interview_tips': [
                    'Use the STAR interview format',
                    'Practice answering behavioral questions aloud',
                    'Get feedback on your email/communication style'
                ],
                'focus': 'Focus on clear, structured communication. Improving LinkedIn activity and doing peer mock interviews will rapidly boost this score.'
            },
            'confidence': {
                'courses': [
                    'Overcoming Interview Anxiety (Udemy)',
                    'Leadership & Executive Presence (Coursera)',
                    'Body Language and Confidence Training'
                ],
                'exercises': [
                    'Complete a 1-on-1 mentoring session',
                    'Present a topic to a group weekly',
                    'Track your daily wins in a journal'
                ],
                'interview_tips': [
                    'Do power poses before interviews',
                    'Practice mock interviews on camera',
                    'Focus on preparation, not perfection'
                ],
                'focus': 'Your core technical skills are solid. Channel that knowledge into confident delivery. Each mock interview attempt drastically reduces anxiety over time.'
            },
            'none': {
                'courses': [
                    'Advanced Product Management (Coursera)',
                    'AI & Machine Learning Fundamentals',
                    'Entrepreneurship & Innovation (MIT OpenCourseWare)'
                ],
                'exercises': [
                    'Contribute to open source GitHub projects',
                    'Write technical blog posts or articles',
                    'Mentor a junior peer in your field'
                ],
                'interview_tips': [
                    'Focus on advanced case study interviews',
                    'Prepare a strong personal brand story',
                    'Research target companies deeply'
                ],
                'focus': "You're well-rounded! Push into advanced territory and start building thought leadership in your domain."
            }
        }

        rec = RECOMMENDATIONS.get(gap, RECOMMENDATIONS['none'])
        return {
            'skill_gap_prediction': prediction,
            'low_score_warnings': low_score_warnings,
            'recommended_courses': rec['courses'],
            'practice_exercises': rec['exercises'],
            'interview_tips': rec['interview_tips'],
            'ai_focus_message': rec['focus']
        }

    # =====================================================================
    # 6. PERFORMANCE DASHBOARD ANALYTICS
    # =====================================================================
    def generate_dashboard(self, user_data: dict) -> dict:
        """
        Full performance dashboard payload with scores, strengths,
        weaknesses, AI insights and action plan.
        """
        scores = {
            'technical': user_data.get('technical_score', 0),
            'communication': user_data.get('communication_score', 0),
            'confidence': user_data.get('confidence_score', 0),
            'aptitude': user_data.get('aptitude_score', 0),
            'projects': user_data.get('projects_count', 0),
            'internships': user_data.get('internships_count', 0),
            'certifications': user_data.get('certifications_count', 0),
            'coding_platform': user_data.get('coding_platform_score', 0),
            'mock_interview': user_data.get('mock_interview_score', 0),
            'resume': user_data.get('resume_score', 0),
            'linkedin': user_data.get('linkedin_score', 0),
            'follows': user_data.get('follows_count', 0),
        }

        sorted_scores = sorted(scores.items(), key=lambda x: x[1])
        weakest = sorted_scores[0]
        strongest = sorted_scores[-1]
        avg = round(sum(scores.values()) / len(scores), 2) if scores else 0

        # Dataset-level percentile rank (compare to all 10 users in dataset)
        all_avgs = []
        if self.dataset_df is not None:
            for _, row in self.dataset_df.iterrows():
                row_avg = row[FEATURE_COLUMNS].mean()
                all_avgs.append(row_avg)
        user_avg_comparable = np.mean(list(scores.values()))
        percentile = round(
            (sum(1 for x in all_avgs if x < user_avg_comparable) / len(all_avgs)) * 100
            if all_avgs else 50, 1
        )

        # Run full recommendation engine
        rec = self.recommend(user_data)

        return {
            'user_id': user_data.get('user_id'),
            'overview': {
                'average_score': avg,
                'percentile_among_peers': percentile,
                'strongest_skill': strongest[0],
                'weakest_skill': weakest[0],
                'strongest_value': strongest[1],
                'weakest_value': weakest[1],
            },
            'scores': scores,
            'ai_insights': {
                'predicted_skill_gap': rec['skill_gap_prediction']['predicted_gap'],
                'model_confidence': rec['skill_gap_prediction']['confidence'],
                'gap_probabilities': rec['skill_gap_prediction']['class_probabilities'],
                'focus_message': rec['ai_focus_message'],
                'warnings': rec['low_score_warnings']
            },
            'action_plan': {
                'recommended_courses': rec['recommended_courses'],
                'practice_exercises': rec['practice_exercises'],
                'interview_tips': rec['interview_tips'],
            }
        }


# Instantiate a global engine and train on startup
engine = ICRSAIEngine()
engine.train()


# ==================================================
# STANDALONE TEST BLOCK
# ==================================================
if __name__ == '__main__':
    import json

    print("\n" + "="*60)
    print("ICRS AI ENGINE - Self Test")
    print("="*60)

    test_user = {
        'user_id': 10,
        'technical_score': 0,
        'communication_score': 3,
        'confidence_score': 2,
        'task_score': 4,
        'problem_solving_score': 3,
        'mentor_rating': 2,
        'skill_python': 0,
        'skill_java': 0,
        'skill_sql': 0,
        'skill_ml': 0,
        'skill_react': 0,
        'likes_count': 15,
        'follows_count': 36,
        'role': 'student',
        'skills': ['java', 'sql'],
        'liked_tags': ['programming', 'technical'],
        'following': [101, 105]
    }

    print("\n[1] Skill Gap Prediction:")
    print(json.dumps(engine.predict_skill_gap(test_user), indent=2))

    print("\n[2] Dashboard:")
    print(json.dumps(engine.generate_dashboard(test_user), indent=2))

    dummy_posts = [
        {'id': 1, 'author_id': 999, 'tags': ['communication', 'soft skills'], 'target_audience': ['student'], 'likes': 50, 'comments': 10, 'views': 200, 'author_followers': 300},
        {'id': 2, 'author_id': 101, 'tags': ['technical', 'coding', 'java'], 'target_audience': ['student', 'jobseeker'], 'likes': 800, 'comments': 45, 'views': 1500, 'author_followers': 1200},
        {'id': 3, 'author_id': 105, 'tags': ['confidence', 'mock interview'], 'target_audience': ['jobseeker'], 'likes': 20, 'comments': 2, 'views': 80, 'author_followers': 50},
    ]

    print("\n[3] Personalized Feed Order (student):")
    ranked = engine.rank_personalized_feed(test_user, dummy_posts)
    for p in ranked:
        print(f"  Post {p['id']} → score: {p['_score']}")

    print("\n[4] Recommendations:")
    recs = engine.recommend(test_user)
    print("  Gap:", recs['skill_gap_prediction']['predicted_gap'])
    print("  Courses:", recs['recommended_courses'])
    print("  Warnings:", recs['low_score_warnings'])
    print("="*60)
