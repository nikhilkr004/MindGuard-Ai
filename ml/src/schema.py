"""Feature Schema and Parameter Mapping for MindGuard AI.

Defines the exact list of 36 questionnaire parameters and their positional
order for model training, validation, and Android on-device inference.
"""

from typing import List, Dict

CATEGORIES = [
    "mood",
    "anxiety",
    "stress",
    "sleep",
    "cognitive",
    "social"
]

FEATURE_COLUMNS: List[str] = [
    # Mood & Emotional State
    "mood_01", "mood_02", "mood_03", "mood_04", "mood_05", "mood_06",
    # Anxiety & Worry
    "anxiety_01", "anxiety_02", "anxiety_03", "anxiety_04", "anxiety_05", "anxiety_06",
    # Stress & Coping
    "stress_01", "stress_02", "stress_03", "stress_04", "stress_05", "stress_06",
    # Sleep & Recovery
    "sleep_01", "sleep_02", "sleep_03", "sleep_04", "sleep_05", "sleep_06",
    # Cognitive Functioning
    "cog_01", "cog_02", "cog_03", "cog_04", "cog_05", "cog_06",
    # Daily Functioning & Social
    "social_01", "social_02", "social_03", "social_04", "social_05", "social_06"
]

TARGET_LABELS: Dict[int, str] = {
    0: "LOW",
    1: "MODERATE",
    2: "HIGH"
}

LIKERT_VALUE_MAP: Dict[str, float] = {
    "Never": 0.0,
    "Rarely": 0.25,
    "Sometimes": 0.50,
    "Often": 0.75,
    "Almost Always": 1.00
}
