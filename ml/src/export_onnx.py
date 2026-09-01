"""Exports trained Scikit-Learn Random Forest model to ONNX format and verifies numerical parity."""

import os
import joblib
import numpy as np
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType
import onnxruntime as ort
from schema import FEATURE_COLUMNS

def export_to_onnx(
    model_pkl_path: str = "../models/rf_v1.pkl",
    output_onnx_path: str = "../models/model_v1.onnx"
):
    print(f"Loading trained model from {model_pkl_path}...")
    clf = joblib.load(model_pkl_path)
    
    num_features = len(FEATURE_COLUMNS)
    initial_type = [("float_input", FloatTensorType([None, num_features]))]
    
    print(f"Converting Scikit-Learn model to ONNX (Input features: {num_features})...")
    onx = convert_sklearn(
        clf,
        initial_types=initial_type,
        options={id(clf): {"zipmap": False}} # Output raw probabilities array
    )
    
    os.makedirs(os.path.dirname(output_onnx_path), exist_ok=True)
    with open(output_onnx_path, "wb") as f:
        f.write(onx.SerializeToString())
    print(f"ONNX model exported to {output_onnx_path}")
    
    # Parity check
    print("Verifying ONNX vs Python predictions parity...")
    sample_input = np.random.uniform(0.0, 1.0, size=(5, num_features)).astype(np.float32)
    py_pred = clf.predict(sample_input)
    
    sess = ort.InferenceSession(output_onnx_path)
    input_name = sess.get_inputs()[0].name
    label_name = sess.get_outputs()[0].name
    onnx_pred = sess.run([label_name], {input_name: sample_input})[0]
    
    np.testing.assert_array_equal(py_pred, onnx_pred)
    print(" Parity check passed: ONNX output exactly matches Scikit-Learn predictions!")

if __name__ == "__main__":
    export_to_onnx()
