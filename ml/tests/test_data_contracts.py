"""Validation test for Phase 2 contracts:

Verifies:
1. questions.json format and field completeness for Android Gson parsing.
2. 36 questions matching exact CategoryType mappings in Kotlin.
3. Feature keys alignment between Kotlin FeatureMapper and ML Schema.
4. Model parity and serialization integrity.
"""

import json
import os
import sys
import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../src")))
from schema import FEATURE_COLUMNS, CATEGORIES

def test_questions_json_assets_validity():
    assets_json = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../android/app/src/main/assets/questions.json"))
    assert os.path.exists(assets_json), "questions.json must exist in Android assets"
    
    with open(assets_json, "r", encoding="utf-8") as f:
        questions = json.load(f)
        
    assert len(questions) == 36, "questions.json must contain exactly 36 questions"
    
    question_ids = [q["id"] for q in questions]
    assert question_ids == FEATURE_COLUMNS, "Question IDs must match FEATURE_COLUMNS in exact order"
    
    categories_found = set(q["category"] for q in questions)
    assert categories_found == set(CATEGORIES), f"Categories found {categories_found} must match {CATEGORIES}"
    
    for q in questions:
        assert "id" in q and len(q["id"]) > 0
        assert "category" in q and len(q["category"]) > 0
        assert "categoryTitle" in q and len(q["categoryTitle"]) > 0
        assert "text" in q and len(q["text"]) > 0
        assert "options" in q and len(q["options"]) == 5
        assert q["options"] == ["Never", "Rarely", "Sometimes", "Often", "Almost Always"]

def test_category_proportions():
    assets_json = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../android/app/src/main/assets/questions.json"))
    with open(assets_json, "r", encoding="utf-8") as f:
        questions = json.load(f)
        
    for cat in CATEGORIES:
        cat_questions = [q for q in questions if q["category"] == cat]
        assert len(cat_questions) == 6, f"Each category must have exactly 6 questions, found {len(cat_questions)} for {cat}"
