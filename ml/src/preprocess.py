"""Data Preprocessing and Feature Transformation Pipeline for MindGuard AI."""

import numpy as np
import pandas as pd
from typing import Tuple
from schema import FEATURE_COLUMNS

def preprocess_features(df: pd.DataFrame) -> pd.DataFrame:
    """Cleans, validates, and aligns dataset columns with FEATURE_COLUMNS."""
    # Ensure all required features exist
    for col in FEATURE_COLUMNS:
        if col not in df.columns:
            raise ValueError(f"Missing required feature column: {col}")
    
    # Handle missing values via median imputation
    clean_df = df[FEATURE_COLUMNS].copy()
    clean_df = clean_df.fillna(clean_df.median())
    
    # Ensure values are within [0.0, 1.0] range
    clean_df = clean_df.clip(lower=0.0, upper=1.0)
    return clean_df

def prepare_train_test(
    df: pd.DataFrame, 
    target_col: str, 
    test_size: float = 0.2, 
    random_state: int = 42
) -> Tuple[np.ndarray, np.ndarray, np.ndarray, np.ndarray]:
    """Prepares stratified train-test splits."""
    from sklearn.model_selection import train_test_split
    
    X = preprocess_features(df).values.astype(np.float32)
    y = df[target_col].values.astype(np.int64)
    
    return train_test_split(
        X, y, 
        test_size=test_size, 
        random_state=random_state, 
        stratify=y
    )
