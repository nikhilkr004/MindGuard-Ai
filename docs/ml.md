# MindGuard AI — Machine Learning Pipeline & ONNX Specification

## 1. Overview
MindGuard AI utilizes an ensemble **Random Forest Classifier** trained in Python (`scikit-learn`) and exported to **ONNX format** for fast, offline, privacy-preserving on-device inference on Android.

---

## 2. ML Architecture & Pipeline

```
[Questionnaire Features (36 Parameters)]
                   │
                   ▼
       [Data Preprocessing & Scaling]
                   │
                   ▼
     [Random Forest Classifier (Python)]
                   │
                   ▼
      [ONNX Model Export (skl2onnx)]
                   │
                   ▼
   [model_v1.onnx (Android Assets)]
                   │
                   ▼
   [ONNX Runtime Android Engine]
                   │
                   ▼
     [Multi-Class Risk Predictions]
     (0: Low, 1: Moderate, 2: High)
```

---

## 3. Evaluation & Validation Metrics
Evaluation on held-out test sets requires:
- **Macro F1-Score & Weighted F1-Score** (handles class imbalance).
- **Multi-Class Confusion Matrix** (tracks false negatives on elevated risk).
- **ROC-AUC per Class** (Low, Moderate, High).
- **ONNX vs Python Parity Verification:** Numerical prediction difference < $10^{-5}$.

---

## 4. On-Device Model Specifications
- **Format:** ONNX Version 1.14+ (Opset 15)
- **Model Size:** < 5 MB
- **Inference Time:** < 30 ms on standard Android hardware
- **Thread Safety:** Single `OrtSession` instance managed by `ModelManager.kt`
