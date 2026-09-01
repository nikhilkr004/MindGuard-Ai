"""End-to-End Pipeline Execution Script for MindGuard AI ML Module.

1. Generates processed questionnaire dataset (3000 samples across 36 parameters).
2. Trains Random Forest Classifier with balanced class weights.
3. Exports model to ONNX format and verifies numerical equivalence.
4. Copies ONNX model to Android assets directory.
"""

import os
import shutil
import joblib
import numpy as np
import pandas as pd
from generate_dataset import generate_questionnaire_dataset
from train import train_model
from export_onnx import export_to_onnx

def main():
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_dir = os.path.join(base_dir, "../data/processed")
    models_dir = os.path.join(base_dir, "../models")
    android_assets_dir = os.path.join(base_dir, "../../android/app/src/main/assets")
    
    os.makedirs(data_dir, exist_ok=True)
    os.makedirs(models_dir, exist_ok=True)
    os.makedirs(android_assets_dir, exist_ok=True)
    
    dataset_path = os.path.join(data_dir, "questionnaire_dataset.csv")
    rf_model_path = os.path.join(models_dir, "rf_v1.pkl")
    onnx_model_path = os.path.join(models_dir, "model_v1.onnx")
    android_onnx_path = os.path.join(android_assets_dir, "model_v1.onnx")
    
    print("==================================================")
    print("STEP 1: Generating Questionnaire Dataset...")
    print("==================================================")
    generate_questionnaire_dataset(n_samples=3000, output_path=dataset_path)
    
    print("\n==================================================")
    print("STEP 2: Training Random Forest Classifier...")
    print("==================================================")
    train_model(data_path=dataset_path, output_model_path=rf_model_path)
    
    print("\n==================================================")
    print("STEP 3: Exporting to ONNX & Verifying Parity...")
    print("==================================================")
    export_to_onnx(model_pkl_path=rf_model_path, output_onnx_path=onnx_model_path)
    
    print("\n==================================================")
    print("STEP 4: Bundling ONNX Model into Android Assets...")
    print("==================================================")
    shutil.copyfile(onnx_model_path, android_onnx_path)
    print(f" Successfully copied ONNX model to {android_onnx_path}")
    print(f" Asset size: {os.path.getsize(android_onnx_path):,} bytes")
    print("==================================================")
    print(" PHASE 1 ML PIPELINE EXECUTION COMPLETE")
    print("==================================================")

if __name__ == "__main__":
    main()
