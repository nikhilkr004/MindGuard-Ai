"""Synthetic / Benchmark Dataset Generator for MindGuard AI.

Generates realistic structured questionnaire responses for 36 parameters
across 6 categories (Mood, Anxiety, Stress, Sleep, Cognitive, Social)
with calibrated distributions for LOW (0), MODERATE (1), and HIGH (2) risk levels.
"""

import os
import numpy as np
import pandas as pd
from schema import FEATURE_COLUMNS, LIKERT_VALUE_MAP, TARGET_LABELS

def generate_questionnaire_dataset(
    n_samples: int = 3000,
    output_path: str = "../data/processed/questionnaire_dataset.csv",
    random_seed: int = 42
) -> pd.DataFrame:
    np.random.seed(random_seed)
    
    # Class distribution: ~45% Low, ~35% Moderate, ~20% High
    n_low = int(n_samples * 0.45)
    n_mod = int(n_samples * 0.35)
    n_high = n_samples - n_low - n_mod
    
    likert_vals = np.array([0.0, 0.25, 0.50, 0.75, 1.0])
    
    # Probabilities for Likert selection per risk class
    p_low = [0.60, 0.25, 0.12, 0.03, 0.00]      # Skewed towards Never/Rarely
    p_mod = [0.10, 0.25, 0.40, 0.20, 0.05]      # Centered around Sometimes/Often
    p_high = [0.02, 0.08, 0.25, 0.40, 0.25]     # Skewed towards Often/Almost Always
    
    rows = []
    
    # Generate Low risk samples
    for _ in range(n_low):
        sample = np.random.choice(likert_vals, size=len(FEATURE_COLUMNS), p=p_low)
        # Add slight realistic noise
        sample = np.clip(sample + np.random.normal(0, 0.02, size=len(FEATURE_COLUMNS)), 0.0, 1.0)
        rows.append(list(sample) + [0])
        
    # Generate Moderate risk samples
    for _ in range(n_mod):
        sample = np.random.choice(likert_vals, size=len(FEATURE_COLUMNS), p=p_mod)
        sample = np.clip(sample + np.random.normal(0, 0.02, size=len(FEATURE_COLUMNS)), 0.0, 1.0)
        rows.append(list(sample) + [1])
        
    # Generate High risk samples
    for _ in range(n_high):
        sample = np.random.choice(likert_vals, size=len(FEATURE_COLUMNS), p=p_high)
        sample = np.clip(sample + np.random.normal(0, 0.02, size=len(FEATURE_COLUMNS)), 0.0, 1.0)
        rows.append(list(sample) + [2])
        
    df = pd.DataFrame(rows, columns=FEATURE_COLUMNS + ["risk_level"])
    
    # Shuffle dataset
    df = df.sample(frac=1.0, random_state=random_seed).reset_index(drop=True)
    
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    df.to_csv(output_path, index=False)
    print(f"Dataset generated with {len(df)} samples ({n_low} Low, {n_mod} Mod, {n_high} High) saved to {output_path}")
    return df

if __name__ == "__main__":
    generate_questionnaire_dataset()
