"""Random Forest Model Training for MindGuard AI."""

import os
import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
from preprocess import prepare_train_test
from schema import FEATURE_COLUMNS, TARGET_LABELS

def train_model(
    data_path: str,
    output_model_path: str = "../models/rf_v1.pkl",
    n_estimators: int = 150,
    max_depth: int = 8,
    random_state: int = 42
) -> RandomForestClassifier:
    """Trains a Random Forest Classifier on questionnaire features."""
    print(f"Loading training data from {data_path}...")
    df = pd.read_csv(data_path)
    
    X_train, X_test, y_train, y_test = prepare_train_test(df, target_col="risk_level")
    
    print(f"Training Random Forest (n_estimators={n_estimators}, max_depth={max_depth})...")
    clf = RandomForestClassifier(
        n_estimators=n_estimators,
        max_depth=max_depth,
        random_state=random_state,
        class_weight="balanced"
    )
    clf.fit(X_train, y_train)
    
    # Evaluate
    y_pred = clf.predict(X_test)
    print("\n--- Test Set Evaluation ---")
    print(classification_report(y_test, y_pred, target_names=[TARGET_LABELS[i] for i in sorted(TARGET_LABELS.keys())]))
    
    os.makedirs(os.path.dirname(output_model_path), exist_ok=True)
    joblib.dump(clf, output_model_path)
    print(f"Model saved to {output_model_path}")
    return clf

if __name__ == "__main__":
    import sys
    if len(sys.argv) > 1:
        train_model(sys.argv[1])
    else:
        print("Usage: python train.py <path_to_processed_dataset.csv>")
