# MindGuard AI — Functional & Product Requirements

## 1. Executive Summary
MindGuard AI is an AI-integrated mobile application designed for early mental health risk screening and teleconsultation. It bridges the gap between self-assessment, digital wellbeing awareness, and professional mental health support.

> **Disclaimer:** MindGuard AI provides preliminary risk screening and self-care navigation. It is NOT a diagnostic tool and does not replace evaluation by certified mental healthcare professionals.

---

## 2. Core Functional Requirements

### 2.1 Authentication & Onboarding
- **FR-AUTH-01:** Email/password registration and login with Firebase Authentication.
- **FR-AUTH-02:** Multi-role support: `User`, `Professional`, and `Admin`.
- **FR-AUTH-03:** Mandatory informed consent and safety disclaimer acceptance prior to assessment.

### 2.2 Mental Health Risk Assessment
- **FR-ASSESS-01:** Structured 30–40 parameter questionnaire covering:
  - Mood & Emotional State
  - Anxiety & Worry
  - Stress & Coping
  - Sleep & Recovery
  - Cognitive Functioning
  - Daily Functioning & Social Wellbeing
- **FR-ASSESS-02:** Page-by-page UX (4–6 questions per category) with real-time progress tracking.
- **FR-ASSESS-03:** On-device Random Forest ML inference using ONNX Runtime.
- **FR-ASSESS-04:** Categorization into non-diagnostic risk levels: `Low`, `Moderate`, `High / Elevated Indicators`.
- **FR-ASSESS-05:** Historical assessment tracking and trend visualization over time.

### 2.3 Wellbeing & Calming Zone
- **FR-WELL-01:** Optional daily wellbeing check-ins (Mood, Energy, Stress, Sleep on a 1–5 scale).
- **FR-WELL-02:** Privacy-aware screen-time and usage pattern insights.
- **FR-WELL-03:** Interactive Calming Zone featuring guided breathing (60s, 120s) and grounding exercises.
- **FR-WELL-04:** Context-aware rule-based recommendations.

### 2.4 Professional Directory & Verification
- **FR-PROF-01:** Search and filter verified mental health professionals by specialty, language, and availability.
- **FR-PROF-02:** Detailed professional profiles with qualifications, experience, and available slots.
- **FR-PROF-03:** Admin verification gate for licensed practitioners before appearing in search results.

### 2.5 Appointment Booking & Teleconsultation
- **FR-BOOK-01:** Atomic slot reservation via server-side JavaScript Firebase Cloud Functions to prevent double-booking.
- **FR-BOOK-02:** Video and audio consultations powered by JioCloud SDK.
- **FR-BOOK-03:** Real-time end-to-end authorized chat via Cloud Firestore.
- **FR-BOOK-04:** Automated push notifications and reminders via Firebase Cloud Messaging (FCM).

---

## 3. Non-Functional Requirements
- **NFR-PERF:** Client-side ML inference response under 50ms on Android devices.
- **NFR-SEC:** Strict Firestore & Storage security rules ensuring users access only their own records.
- **NFR-PRIV:** Zero logging of sensitive health data, passwords, or tokens.
