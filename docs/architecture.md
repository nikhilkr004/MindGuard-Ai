# MindGuard AI — System Architecture & Design

## 1. High-Level Architecture

```
                    ┌─────────────────────────────────┐
                    │      Android Application        │
                    │      (Kotlin + XML Layouts)     │
                    │   MVVM + Coroutines + Navigation│
                    └───────────────┬─────────────────┘
                                    │
    ┌───────────────────────────────┼───────────────────────────────┐
    │                               │                               │
    ▼                               ▼                               ▼
┌──────────────────┐    ┌──────────────────────┐    ┌─────────────────────┐
│  Firebase Auth   │    │    Cloud Firestore   │    │  Firebase Storage   │
│  (Token & State) │    │  (Structured Data)   │    │  (Files / Reports)  │
└──────────────────┘    └──────────┬───────────┘    └─────────────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────────┐
                    │    Firebase Cloud Functions     │
                    │          (JavaScript)           │
                    │  - Atomic Appointment Booking   │
                    │  - Professional Verification    │
                    │  - JioCloud Session Tokens      │
                    │  - Scheduled Reminders & Tasks  │
                    └───────────────┬─────────────────┘
                                    │
                                    ▼
                    ┌─────────────────────────────────┐
                    │    Firebase Cloud Messaging     │
                    │       (Push Notifications)      │
                    └─────────────────────────────────┘
```

---

## 2. On-Device Machine Learning Pipeline

```
[User Answers (30-40 Parameters)]
                 │
                 ▼
       [FeatureMapper.kt] ── Maps Question IDs & Likert responses to numerical vector
                 │
                 ▼
     [OnnxModelRunner.kt] ── Runs Random Forest model via ONNX Runtime locally
                 │
                 ▼
     [PredictionResult.kt] ── Produces risk scores (Low / Moderate / High)
                 │
                 ▼
       [RiskMapper.kt] ── Evaluates context-aware wellness recommendations
                 │
                 ▼
    [Firestore Assessment Record] ── Stores non-diagnostic summary to user history
```

---

## 3. Teleconsultation Architecture

- **Video & Audio Sessions:** Handled by **JioCloud SDK**, with session metadata and authorization credentials brokered by Cloud Functions.
- **Chat Consultation:** Real-time bi-directional message streaming using Firestore subcollections (`consultation_sessions/{sessionId}/messages/{messageId}`).

---

## 4. Technology Stack Summary

| Component | Technology | Rationale |
|---|---|---|
| **Mobile UI** | Kotlin + XML (Android SDK) | High stability, deterministic layout rendering, Material 3 compliance |
| **Local ML Engine** | ONNX Runtime Android | Ultra-fast on-device inference (<50ms), zero cloud ML latency, privacy-first |
| **Backend & Database** | Cloud Firestore + Storage | Serverless real-time database with multi-region replication |
| **Cloud Logic** | Firebase Cloud Functions (JavaScript) | Trusted serverless transaction execution without managing servers |
| **Consultation** | JioCloud Audio/Video SDK | High-quality WebRTC-based low-latency teleconferencing |
| **Push Notifications**| Firebase Cloud Messaging (FCM) | Cross-device scheduled and transactional notifications |
