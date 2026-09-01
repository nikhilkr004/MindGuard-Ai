# MindGuard AI — Testing Strategy & Verification Plan

## 1. Test Layers

### 1.1 Android Client
- **Unit Tests (JUnit 5 + MockK + Turbine):**
  - ViewModels state transitions and Flow emissions.
  - Data mapping (`FeatureMapper`, `PredictionResult`).
  - Repository error handling and offline caching.
- **Instrumented Tests (Espresso + UI Automator):**
  - Navigation flow from onboarding to category-by-category assessment.
  - Form validation on authentication and booking screens.
- **ONNX Local Inference Parity Tests:**
  - Validates ONNX Runtime predictions match expected Python benchmark vectors with zero drift.

### 1.2 Firebase & Security
- **Firebase Emulator Rules Tests (`@firebase/rules-unit-testing`):**
  - Verified User A cannot read User B's assessment data.
  - Unauthenticated access returns `PERMISSION_DENIED`.
  - Non-admin cannot invoke `verifyProfessional`.

### 1.3 Machine Learning Pipeline
- **Python Unit & Integration Tests (`pytest`):**
  - Input validation, missing value imputation, and feature alignment.
  - Train/test leakage checks.
  - ONNX model graph validity checks (`onnx.checker.check_model`).
