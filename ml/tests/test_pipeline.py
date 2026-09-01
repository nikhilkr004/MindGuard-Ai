"""Unit and Integration Tests for MindGuard AI ML Pipeline.

Tests:
1. Feature schema consistency (36 features, 6 categories).
2. Synthetic dataset generation and non-empty validation.
3. Random Forest training convergence and output metrics.
4. ONNX model export and strict prediction parity with Scikit-Learn.
"""

import os
import sys
import pytest
import numpy as np
import pandas as pd
import joblib
import onnxruntime as ort

# Add src to python path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../src")))

from schema import FEATURE_COLUMNS, CATEGORIES, TARGET_LABELS, LIKERT_VALUE_MAP
from generate_dataset import generate_questionnaire_dataset
from train import train_model
from export_onnx import export_to_onnx

def test_schema_integrity():
    assert len(FEATURE_COLUMNS) == 36, "Expected 36 features for MindGuard questionnaire"
    assert len(CATEGORIES) == 6, "Expected 6 categories"
    assert len(TARGET_LABELS) == 3, "Expected 3 target risk labels: LOW, MODERATE, HIGH"
    assert LIKERT_VALUE_MAP["Never"] == 0.0
    assert LIKERT_VALUE_MAP["Almost Always"] == 1.0

def test_dataset_generation(tmp_path):
    dataset_file = os.path.join(tmp_path, "test_dataset.csv")
    df = generate_questionnaire_dataset(n_samples=300, output_path=dataset_file)
    
    assert os.path.exists(dataset_file)
    assert len(df) == 300
    assert set(FEATURE_COLUMNS).issubset(set(df.columns))
    assert "risk_level" in df.columns
    assert set(df["risk_level"].unique()).issubset({0, 1, 2})

def test_model_training_and_onnx_parity(tmp_path):
    # 1. Generate data
    dataset_file = os.path.join(tmp_path, "dataset.csv")
    generate_questionnaire_dataset(n_samples=500, output_path=dataset_file)
    
    # 2. Train RF
    model_pkl = os.path.join(tmp_path, "rf_test.pkl")
    clf = train_model(data_path=dataset_file, output_model_path=model_pkl)
    assert os.path.exists(model_pkl)
    
    # 3. Export ONNX
    onnx_file = os.path.join(tmp_path, "model_test.onnx")
    export_to_onnx(model_pkl_path=model_pkl, output_onnx_path=onnx_file)
    assert os.path.exists(onnx_file)
    
    # 4. Parity Test on 50 arbitrary sample inputs
    sess = ort.InferenceSession(onnx_file)
    test_inputs = np.random.uniform(0.0, 1.0, size=(50, len(FEATURE_COLUMNS))).astype(np.float32)
    
    sk_preds = clf.predict(test_inputs)
    
    input_name = sess.get_inputs()[0].name
    label_name = sess.get_outputs()[0].name
    onnx_preds = sess.run([label_name], {input_name: test_inputs})[0]
    
    np.testing.assert_array_equal(sk_preds, onnx_preds)
