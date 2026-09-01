# Antigravity Master Implementation Document
## AI-Integrated Mobile Application for Early Mental Health Risk Detection and Teleconsultation

> **This is the master implementation instruction for Antigravity.**
>
> Build the project in small, independently testable phases.
> **Do not implement the entire project at once.**
> Complete one phase, verify it, document what changed, and wait for the next phase instruction.
>
> **Final architecture decision:**
> - Android frontend: **Kotlin + XML**
> - Backend/platform: **Firebase only**
> - No Express.js
> - No separately developed Node.js backend/server
> - Firebase Cloud Functions: **JavaScript**
> - Scheduled jobs/automation: **JavaScript Firebase Functions**
> - ML development: **Python**
> - Final ML model: **Random Forest**
> - ML deployment: **ONNX + ONNX Runtime on Android**
> - Video/audio consultation: **ZegoCloud**
> - Chat/realtime application data: **Cloud Firestore**
> - Notifications: **Firebase Cloud Messaging**
> - Authentication: **Firebase Authentication**
> - File assets where necessary: **Firebase Storage**
> - Security: **Firebase Authentication + Firestore/Storage Rules + App Check**
>
> **Do not introduce technologies outside this stack unless explicitly instructed.**

---

# 0. Source of Truth

The implementation must be based on the supplied IILM Phase-I report, the supplied ML notebook converted to PDF, and the referenced Maitri repository.

The IILM report defines:
- Android application for preliminary mental-health risk screening.
- Depression, anxiety, and stress-related indicators.
- Custom project-specific questionnaire.
- Random Forest as the proposed ML model.
- Professional search.
- Professional profile.
- Appointment booking.
- Video/audio/chat teleconsultation.
- Assessment and consultation history.
- Privacy-aware handling.
- AI result must be presented as preliminary, not as a diagnosis.
- Future enhancements include voice emotion, facial/video emotion, NLP journaling, advanced multimodal AI, deeper XAI, and adaptive intervention; these are not required for the initial confirmed scope.

The supplied ML PDF contains:
- NumPy, pandas, matplotlib, seaborn, scikit-learn and Plotly imports.
- Four datasets loaded from a Kaggle `mental-health` input path.
- Dataset description/EDA functions.
- Visualizations.
- A final modeling section using **LinearRegression**.
- The main model uses four mental-health prevalence variables as inputs and predicts **Eating disorders**.
- The base model reports:
  - MAE ≈ 0.0800325
  - MSE ≈ 0.0217863
  - RMSE ≈ 0.1476019
  - R² ≈ 0.6289968
- Higher-dimension and interaction-style linear regression experimentation raises R² to about 0.6554 and about 0.6779 in the later experiment.
- The notebook does **not** contain Random Forest.

Therefore:

**The supplied ML notebook is a reference/EDA/baseline artifact. It is NOT the final model for the Android application's 30–40 parameter questionnaire.**

Do not blindly reuse its target or its four population-level predictors.

---

# 1. Non-Negotiable Architecture

## 1.1 Android

Use:

```text
Kotlin
XML layouts
Android SDK
ViewModel
Repository pattern
Coroutines
Flow where useful
Navigation Component
Material components
```

Do NOT use:

```text
Jetpack Compose
React Native
Flutter
Web frontend
```

unless explicitly instructed later.

---

## 1.2 Firebase

Use Firebase as the complete backend/platform:

```text
Firebase Authentication
Cloud Firestore
Firebase Storage
Firebase Cloud Functions
Firebase Cloud Messaging
Firebase App Check
Firestore Security Rules
Storage Security Rules
Firebase Emulator Suite
```

Do NOT build:

```text
Express server
server.js
external REST backend
separate Node backend
Python API
Flask API
FastAPI API
separate backend database
```

---

## 1.3 JavaScript

JavaScript is for Firebase Cloud Functions only.

Use functions for operations that should be trusted/server-controlled:

```text
createAppointment
cancelAppointment
rescheduleAppointment
verifyProfessional
createConsultationSession
sendAppointmentReminder
onAppointmentCreated
onAppointmentUpdated
cleanupExpiredSlots
cleanupExpiredData
```

Do not create a generic Express-style API layer.

---

## 1.4 Python

Python is ONLY for the ML lifecycle:

```text
data analysis
data cleaning
preprocessing
feature engineering
Random Forest training
evaluation
model export
ONNX validation
```

Do not create a Python backend server.

---

## 1.5 Video/Audio

Use **JioCloud as the final selected video/audio consultation solution** according to the project decision.

Keep Firebase responsible for:

```text
authentication
appointment data
participant/session authorization
session metadata
notifications
chat
```

Keep the JioCloud integration isolated behind a consultation interface so it can be changed without rewriting the rest of the application.

Do not replace JioCloud with another provider unless explicitly instructed.

---

# 2. Product Vision

The application is a:

> **privacy-aware mobile mental-health screening and care-navigation platform.**

The user journey is:

```text
Awareness
   ↓
Onboarding
   ↓
Authentication
   ↓
Consent / Safety Notice
   ↓
Assessment
   ↓
30–40 structured parameters
   ↓
Preprocessing
   ↓
Random Forest screening
   ↓
Preliminary risk result
   ↓
Wellness / prevention guidance
   ↓
Digital wellbeing guidance
   ↓
Professional search
   ↓
Appointment
   ↓
JioCloud video/audio OR Firestore chat
   ↓
Consultation history
   ↓
Future screening / trend tracking
```

---

# 3. Core Product Parameters

Organize the user inputs into understandable categories instead of showing a random list of 30–40 questions.

Recommended categories:

## Category A — Mood and Emotional State

Possible parameters:
- persistent low mood
- loss of interest
- emotional exhaustion
- mood fluctuation
- irritability
- feeling overwhelmed

## Category B — Anxiety and Worry

Possible parameters:
- excessive worry
- restlessness
- nervousness
- difficulty controlling worry
- fear/tension
- physical anxiety sensations

## Category C — Stress

Possible parameters:
- perceived stress
- workload pressure
- inability to relax
- feeling out of control
- mental overload
- difficulty coping

## Category D — Sleep and Recovery

Possible parameters:
- sleep duration
- sleep quality
- difficulty falling asleep
- waking during night
- daytime tiredness
- irregular sleep schedule

## Category E — Cognitive Functioning

Possible parameters:
- concentration difficulty
- forgetfulness
- indecision
- racing thoughts
- reduced productivity

## Category F — Daily Functioning

Possible parameters:
- difficulty completing daily tasks
- academic/work disruption
- reduced motivation
- reduced self-care
- loss of routine

## Category G — Social Wellbeing

Possible parameters:
- social withdrawal
- reduced communication
- loneliness
- relationship stress
- support availability

## Category H — Digital Wellbeing / Behavior

Possible parameters:
- daily screen time
- social-media use
- late-night phone usage
- continuous session duration
- excessive short-video consumption
- phone use immediately before sleep
- notification-driven checking
- difficulty stopping app use

> **Important:** These categories are a product design framework. They are not automatically valid clinical features. The final ML feature list must be determined from the selected dataset, questionnaire design, and approved modeling methodology.

---

# 4. Questionnaire UX

The assessment must be page-by-page.

Do not show 40 questions on one page.

Recommended:

```text
Assessment
    ↓
Category 1
  4–6 questions
    ↓
Category 2
  4–6 questions
    ↓
Category 3
  4–6 questions
    ↓
...
    ↓
Review
    ↓
Submit
```

Each page should display:

```text
Category title
Question number
Progress bar
Question
Answer options
Next
Back
```

Example:

```text
Stress & Coping

Question 4 of 36

How often have you felt unable to relax?

( ) Never
( ) Rarely
( ) Sometimes
( ) Often
( ) Almost Always

[ Back ]                [ Next ]
```

---

# 5. Parameter Design Rule

For every question, maintain a machine-readable definition:

```json
{
  "id": "stress_relax_01",
  "category": "stress",
  "text": "How often have you felt unable to relax?",
  "answerType": "likert",
  "options": [
    "Never",
    "Rarely",
    "Sometimes",
    "Often",
    "Almost Always"
  ],
  "required": true,
  "questionnaireVersion": "Q-V1"
}
```

Maintain an explicit mapping:

```text
question ID
→ numeric value
→ feature name
→ model input position
```

Never rely on question order alone.

---

# 6. Daily Wellbeing Check-In

Add a lightweight optional feature separate from the main screening model.

Example:

```text
How are you feeling today?

Mood
1 ───── 5

Energy
1 ───── 5

Stress
1 ───── 5

Sleep
1 ───── 5
```

Store as a daily wellbeing record.

This can support:

```text
daily trends
weekly trends
monthly trends
simple personalized insights
```

Do not treat daily check-in values as a clinical diagnosis.

---

# 7. Digital Wellbeing / Screen-Usage Feature

The project can consider phone-usage behavior as a **supporting digital-wellbeing signal**.

Important design:

```text
Mental-health screening
        +
Digital wellbeing signals
        +
History
        ↓
Personalized guidance
```

Do NOT automatically mix every phone-usage signal into the Random Forest model.

Recommended first version:

### Layer 1 — ML screening

Uses only the finalized questionnaire/dataset-aligned features.

### Layer 2 — Digital wellbeing engine

Uses phone usage metrics to provide practical behavioral guidance.

Example:

```text
Screen time today: 7h 18m
Late-night usage: 1h 12m
Longest continuous session: 2h 04m

Guidance:
"You have had a long continuous screen session.
Consider taking a short break, stretching, breathing,
or stepping away from the screen."
```

This keeps the clinical-risk model separate from behavioral coaching.

---

# 8. Android Screen Usage Data

Use only APIs/data that are permitted by Android and the user's granted permissions.

Possible signals, subject to platform permission and implementation feasibility:

```text
total daily screen time
application category usage
selected app usage duration
late-night usage
number of sessions
longest continuous session
repeated checking
```

The app should clearly explain why this data is requested.

Do not secretly collect usage information.

Do not claim:

```text
"YouTube caused your depression."
```

Instead:

```text
"Your recent screen-use pattern may be affecting
your routine. Consider a short digital break."
```

---

# 9. Digital Wellbeing Criteria Engine

This is a rule-based prevention layer, not the core medical model.

Example:

```text
IF
continuousScreenTime >= threshold
THEN
show "Take a short break"

IF
lateNightUsage >= threshold
THEN
show "Consider reducing screen use before sleep"

IF
dailyScreenTime trend increases repeatedly
THEN
show "Your screen-use time has been increasing"

IF
socialMediaUsage is high
THEN
show "Try a short offline activity"

IF
stress check-in is high
THEN
show calming/breathing guidance
```

Threshold values must be configurable, documented, and treated as wellbeing heuristics rather than clinical cutoffs.

---

# 10. Prevention and Guidance Engine

After the screening result, the system should combine:

```text
Risk level
+
Category-level indicators
+
Digital wellbeing signals
+
Recent history
+
Optional daily check-ins
```

Then select a safe recommendation category.

Example:

```text
LOW
→ routine wellness
→ sleep/routine support
→ light activity
→ optional daily check-in

MODERATE
→ self-care guidance
→ calming/breathing
→ digital break
→ monitor trend
→ consider professional consultation

HIGH / CONCERNING
→ strongly encourage professional support
→ show safety guidance
→ show professional search
→ do not make a diagnosis
```

---

# 11. Calming Zone

Add a simple prevention module inspired by the Maitri reference.

```text
Calming Zone
├── 60-second breathing
├── 2-minute breathing
├── Grounding exercise
├── Short mindfulness activity
└── Optional calming audio
```

This should be lightweight and can be implemented without another backend.

---

# 12. Dashboard

Dashboard should combine the major project areas:

```text
Welcome

Latest Screening
    Overall: Moderate

Stress
    Moderate

Anxiety
    Low

Depression-related indicators
    Moderate

Digital Wellbeing
    Screen time: 6h 42m

Today's check-in
    Stress: 4/5

Recommended for you
    "Take a 5-minute break"

Quick actions
[ Assess ]
[ Professionals ]
[ Appointments ]
[ Calming Zone ]
[ History ]
```

---

# 13. Trends

Show:

```text
Weekly
Monthly
Previous assessments
Daily wellbeing
Digital wellbeing
```

Example:

```text
Mental Health Screening
      ↓
Low → Moderate → Moderate

Screen Time
      ↓
4h → 5h → 6h → 7h
```

Do not claim that the trend is a clinical diagnosis.

---

# 14. Professional Search

Filters:

```text
Specialization
Language
Consultation mode
Availability
Experience
```

Professional card:

```text
Name
Qualification
Specialization
Experience
Verified
Available slot

[ View Profile ]
```

---

# 15. Professional Verification

```text
Professional registration
        ↓
Credential submission
        ↓
Admin review
        ↓
Verified / rejected
        ↓
Only verified professionals
appear in user search
```

For an academic MVP, this can be a simplified controlled workflow, but the final project must document that professional credentials are not automatically verified merely by registration.

---

# 16. Appointment Booking

Use Firebase Cloud Function in JavaScript for trusted booking.

```text
User
 ↓
Professional
 ↓
Available slot
 ↓
createAppointment()
 ↓
JS Cloud Function
 ↓
Verify user
 ↓
Verify professional
 ↓
Check slot
 ↓
Reserve slot atomically
 ↓
Create appointment
 ↓
FCM notification
```

No Express backend.

---

# 17. Consultation

## JioCloud

JioCloud is the final selected consultation solution for:

```text
Video call
Audio call
```

Firebase handles:

```text
Authentication
Appointment
Authorization
Session metadata
Notifications
```

## Chat

Firestore realtime:

```text
consultation_sessions/{sessionId}/messages/{messageId}
```

Flow:

```text
User
 ↕
Firestore
 ↕
Professional
```

---

# 18. Firebase Data Model

```text
users/{uid}

professionals/{professionalId}

questionnaires/{questionnaireId}

questionnaires/{questionnaireId}/questions/{questionId}

assessments/{assessmentId}

daily_checkins/{checkinId}

digital_wellbeing/{recordId}

appointments/{appointmentId}

availability/{slotId}

consultation_sessions/{sessionId}

consultation_sessions/{sessionId}/messages/{messageId}

notifications/{notificationId}

consents/{consentId}

audit_logs/{auditId}

model_versions/{modelVersionId}

app_config/{configId}
```

---

# 19. Data Ownership

Users:

```text
own profile
own assessments
own daily check-ins
own digital wellbeing records
own appointments
own consultation history
```

Professionals:

```text
own professional profile
own availability
authorized appointments
authorized consultation sessions
```

Admins:

```text
verification
approved content
controlled administration
audit operations
```

---

# 20. Firebase Security

Use:

```text
Firebase Authentication
Firestore Security Rules
Storage Security Rules
App Check
Cloud Function authorization
```

Rules must verify:

```text
authentication
uid
role
resource ownership
allowed fields
```

Never use:

```text
allow read, write: if true;
```

for private health/consultation data.

Use the Firebase Emulator Suite to test the rules.

---

# 21. JavaScript Functions

Function directory:

```text
functions/
├── index.js
├── package.json
└── modules/
    ├── appointments.js
    ├── professionals.js
    ├── consultations.js
    ├── notifications.js
    └── cleanup.js
```

Functions:

```text
createAppointment
cancelAppointment
rescheduleAppointment
verifyProfessional
createConsultationSession
sendAppointmentReminder
onAppointmentCreated
onAppointmentUpdated
cleanupExpiredSlots
```

Keep each function small.

---

# 22. Scheduled Automation

Use scheduled JavaScript Firebase Functions for:

```text
appointment reminders
expired-slot cleanup
old temporary session cleanup
scheduled wellbeing reminders
```

Example:

```text
Every day
   ↓
Find upcoming appointments
   ↓
Send FCM reminder
```

Do not create a separate cron server.

---

# 23. ML Model — FINAL STRATEGY

The current provided notebook is **not the final app model**.

The notebook uses:

```text
LinearRegression
```

and its modeling section uses four variables:

```text
Schizophrenia disorders
Depressive disorders
Anxiety disorders
Bipolar disorders
```

to predict:

```text
Eating disorders
```

The base linear regression R² is approximately:

```text
0.6289968
```

and later feature-expansion/interaction experiments reach approximately:

```text
0.6554299
0.6779169
```

These results are baseline/reference results from the supplied notebook.

They must NOT be presented as:

```text
mental-health questionnaire classifier
```

or:

```text
depression/anxiety/stress diagnostic model
```

The final application model should be newly created to match the questionnaire and the selected dataset.

---

# 24. Final Model

Use:

```text
Random Forest
```

because this is the algorithm stated in the IILM Phase-I report.

Final conceptual pipeline:

```text
30–40 questionnaire parameters
          ↓
Feature mapping
          ↓
Preprocessing
          ↓
Random Forest
          ↓
Preliminary risk classification
```

Possible outputs:

```text
LOW
MODERATE
HIGH
```

or separate category outputs where supported by the final dataset/model design.

---

# 25. Dataset Decision Gate

Before writing the final ML training code:

1. Select dataset.
2. Verify access/license.
3. Inspect features.
4. Inspect target labels.
5. Inspect population.
6. Inspect class distribution.
7. Check missing data.
8. Check participant structure.
9. Map dataset features to questionnaire questions.
10. Decide whether one multi-class Random Forest or multiple models are appropriate.

Do not force the existing four-feature Kaggle model into the new questionnaire.

---

# 26. Model Training Project Structure

```text
ml/
├── data/
│   ├── raw/
│   └── processed/
│
├── notebooks/
│   ├── 01_eda.ipynb
│   ├── 02_preprocessing.ipynb
│   ├── 03_random_forest.ipynb
│   └── 04_evaluation.ipynb
│
├── src/
│   ├── preprocess.py
│   ├── train.py
│   ├── evaluate.py
│   ├── export_onnx.py
│   └── schema.py
│
├── models/
│   ├── rf_v1.pkl
│   └── model_v1.onnx
│
└── tests/
```

---

# 27. ML Evaluation

Do not report only accuracy.

Measure:

```text
Accuracy
Precision
Recall
F1-score
Confusion Matrix
Class-wise metrics
ROC-AUC where suitable
```

Also test:

```text
data leakage
class imbalance
participant leakage
train/test separation
feature leakage
```

---

# 28. Python → ONNX

```text
Python
 ↓
Random Forest
 ↓
Export to ONNX
 ↓
model_v1.onnx
 ↓
Validate ONNX output
 ↓
Android assets
 ↓
ONNX Runtime
```

The ONNX output should be compared against Python predictions using the same test data.

---

# 29. Android ML Module

```text
ml/
├── OnnxModelRunner.kt
├── FeatureMapper.kt
├── PredictionResult.kt
└── ModelManager.kt
```

Flow:

```text
Answers
 ↓
FeatureMapper
 ↓
OnnxModelRunner
 ↓
ONNX Runtime
 ↓
PredictionResult
 ↓
Risk Mapper
 ↓
Firestore
```

---

# 30. Assessment Data Flow

```text
User answers
      ↓
Local validation
      ↓
Feature mapping
      ↓
ONNX Random Forest
      ↓
Risk result
      ↓
Safe interpretation
      ↓
Save assessment
      ↓
Show history
```

The raw answers should only be stored when necessary and should be protected by Firestore rules.

---

# 31. Result Interpretation

### Low

```text
Your responses currently show lower levels
of the indicators assessed.

You can continue healthy routines and
use the wellbeing tools available in the app.
```

### Moderate

```text
Your responses show some elevated indicators.

Consider monitoring your wellbeing, using
the calming/wellness tools, and speaking to
a qualified professional if concerns persist.
```

### High / Concerning

```text
Your responses show elevated indicators
that may warrant professional attention.

Consider speaking with a qualified mental-health
professional for proper evaluation.

This screening result is not a diagnosis.
```

---

# 32. Do Not Use Diagnostic Language

Never display:

```text
You have depression.
You have anxiety.
You are clinically depressed.
AI diagnosed you.
```

Use:

```text
Depression-related indicators
Anxiety-related indicators
Stress-related indicators
Preliminary screening result
```

The IILM report explicitly frames the model as preliminary and non-diagnostic.

---

# 33. Personalization Engine

The personalization layer can combine:

```text
screening
+
daily check-ins
+
digital wellbeing
+
history
```

Example:

```text
High stress
+
Late-night screen usage
+
Poor sleep check-in
        ↓
Recommendation:
"Consider reducing late-night screen use
and try a short breathing exercise."
```

This is a wellbeing recommendation, not a medical diagnosis.

---

# 34. Recommendation Rule Engine

Keep recommendation rules separate from the ML model.

```text
ML Model
   ↓
Risk result

Rule Engine
   ↓
Context-aware guidance
```

Example:

```text
IF stress == HIGH
AND screenTime == HIGH
THEN
show digital-break + calming recommendation
```

This makes the system easier to explain and test.

---

# 35. Maitri Reference Integration

Use the Maitri repository as a **feature and UX reference**, not as the backend architecture.

Useful inspiration:

```text
mental-health quiz
stress/anxiety/depression score presentation
weekly/monthly trends
personalized insights
calming zone
breathing support
privacy-first framing
```

Do not copy unnecessary complexity from Maitri.

Do not add to MVP:

```text
facial emotion recognition
voice emotion recognition
multimodal fusion
NLP journaling
social-media sentiment
advanced facial XAI
complex chatbot
gamification
```

The supplied IILM report also places the main multimodal/advanced AI features outside confirmed current scope.

---

# 36. Final Android Screens

```text
01 Splash
02 Onboarding
03 Login
04 Register
05 Verification
06 Consent
07 Safety Notice

08 Home
09 Assessment Intro
10 Category Intro
11 Question
12 Review Answers
13 Processing
14 Result

15 History
16 Assessment Detail
17 Trends
18 Daily Check-in

19 Digital Wellbeing
20 Screen Usage Detail
21 Wellness Guidance
22 Calming Zone
23 Breathing Exercise

24 Professional Search
25 Professional Profile
26 Booking
27 Booking Confirmation
28 Appointments
29 Appointment Detail

30 Chat
31 Video Call — JioCloud
32 Audio Call — JioCloud
33 Consultation History

34 Profile
35 Settings
36 Privacy
37 Help/About
```

---

# 37. Bottom Navigation

Recommended:

```text
Home
Assess
Wellbeing
Professionals
Appointments
```

Profile/settings can be reached from the Home/Profile icon.

---

# 38. Final Android Flow

```text
Login
 ↓
Home
 ├── Assess
 │     ↓
 │  Questionnaire
 │     ↓
 │  ML Result
 │     ↓
 │  Guidance
 │
 ├── Wellbeing
 │     ↓
 │  Screen Usage
 │  Daily Check-in
 │  Calming Zone
 │
 ├── Professionals
 │     ↓
 │  Search
 │     ↓
 │  Profile
 │     ↓
 │  Booking
 │
 └── Appointments
       ↓
    JioCloud Video/Audio
    or Firestore Chat
```

---

# 39. Phase-Based Implementation

## PHASE 0 — Project specification

Deliver:

```text
requirements.md
architecture.md
screen-map.md
data-model.md
```

Do not code the complete app.

---

## PHASE 1 — Android project

Implement:

```text
Kotlin
XML
Navigation
Base theme
Base activity
Common components
```

Verification:

```text
App builds
Navigation works
No Firebase yet
```

---

## PHASE 2 — Firebase setup

Implement:

```text
Firebase project
Authentication
Firestore
Storage
FCM
App Check
Emulator
```

Verification:

```text
Firebase connected
Test user can authenticate
Firestore test read/write works
```

---

## PHASE 3 — Security

Implement:

```text
Firestore Rules
Storage Rules
Role model
User ownership rules
```

Verification:

```text
User A cannot read User B
Professional cannot access unrelated user data
Admin-only operations are restricted
```

---

## PHASE 4 — Authentication UI

Implement:

```text
Register
Login
Logout
Password reset
Email verification
Profile
```

Verification:

```text
All auth flows work
Invalid inputs handled
```

---

## PHASE 5 — Questionnaire engine

Implement:

```text
Question model
Category model
Firestore question bank
Page-by-page question UI
Validation
Progress bar
Review
Submit
```

Verification:

```text
30–40 questions can be completed
Responses are mapped correctly
Questionnaire version is stored
```

---

## PHASE 6 — ML dataset and EDA

Implement separately in Python:

```text
Dataset acquisition
EDA
Cleaning
Feature audit
Target audit
```

Verification:

```text
Dataset selected
Feature mapping documented
Target defined
No leakage
```

---

## PHASE 7 — Random Forest

Implement:

```text
Preprocessing
Train/test split
Random Forest
Evaluation
Feature importance where appropriate
```

Verification:

```text
Metrics documented
Model reproducible
Model version created
```

---

## PHASE 8 — ONNX deployment

Implement:

```text
Export model
Validate ONNX
Android ONNX Runtime
FeatureMapper
OnnxModelRunner
```

Verification:

```text
Python vs ONNX predictions agree
Android prediction works
```

---

## PHASE 9 — Assessment result

Implement:

```text
Risk mapper
Result screen
Safe explanation
Firestore history
```

Verification:

```text
User receives result
Result is stored
No diagnostic language
```

---

## PHASE 10 — Wellbeing module

Implement:

```text
Daily check-in
Screen usage
Digital wellbeing rules
Personalized guidance
Calming Zone
Breathing exercise
```

Verification:

```text
Guidance changes based on configured rules
No clinical claims
```

---

## PHASE 11 — Professional module

Implement:

```text
Professional profile
Verification status
Search
Filters
Availability
```

Verification:

```text
Only verified professionals appear
```

---

## PHASE 12 — Appointment module

Implement JavaScript Cloud Functions:

```text
createAppointment
cancelAppointment
rescheduleAppointment
```

Verification:

```text
No double booking
Correct status transitions
Unauthorized booking blocked
```

---

## PHASE 13 — Notifications

Implement:

```text
FCM
Appointment notification
Reminder
Cancellation
Consultation reminder
```

Use scheduled JavaScript Cloud Functions.

---

## PHASE 14 — Chat

Implement:

```text
Firestore realtime chat
Session authorization
Message rules
```

Verification:

```text
Only participants can read/write chat
```

---

## PHASE 15 — JioCloud consultation

Implement:

```text
JioCloud video
JioCloud audio
Join authorization
Session lifecycle
Consultation metadata
```

Keep integration modular.

---

## PHASE 16 — History and trends

Implement:

```text
Assessment history
Daily wellbeing history
Digital wellbeing history
Weekly trends
Monthly trends
```

---

## PHASE 17 — Security hardening

Test:

```text
Authentication
Rules
App Check
Storage access
Function authorization
Sensitive logging
```

---

## PHASE 18 — Full integration

End-to-end:

```text
Register
 → Assessment
 → ML
 → Result
 → Guidance
 → Professional
 → Booking
 → JioCloud/Chat
 → History
```

---

## PHASE 19 — Testing

Test:

```text
Unit
UI
Firebase
ML
ONNX
Security
Booking
Chat
JioCloud integration
End-to-end
```

---

## PHASE 20 — Release

Prepare:

```text
Signed APK
Firebase production configuration
Production security rules
ML model version
Documentation
Demo script
```

---

# 40. Antigravity Operating Rules

## Rule 1 — Work in small phases

Never generate the entire codebase in one response.

## Rule 2 — Verify before continuing

At the end of every phase:

```text
Files changed
What works
What was tested
Known issues
Next phase
```

Then stop.

## Rule 3 — Do not invent data

If questionnaire, dataset, professional records, or credentials are not provided:

```text
create the schema
create sample development data only
label it clearly as mock/test data
```

Do not present mock data as real.

## Rule 4 — Do not invent ML results

Run the actual model.

## Rule 5 — Preserve model traceability

Every prediction must know:

```text
modelVersion
questionnaireVersion
featureSchemaVersion
```

## Rule 6 — Do not change architecture

No Express.
No separate Node backend.
No Flask/FastAPI.
No database other than Firebase unless explicitly instructed.

## Rule 7 — Keep healthcare claims conservative

The application is a preliminary screening/support platform.

---

# 41. Error Handling

Examples:

```text
Firebase unavailable
→ "Unable to connect. Please try again."

Model unavailable
→ "We could not complete the screening. Please try again."

Booking failure
→ "This slot is no longer available."

JioCloud session failure
→ "Unable to start the consultation. Please try again."
```

Never expose stack traces.

---

# 42. Performance

Target:

```text
Fast app launch
Smooth questionnaire navigation
Local ML inference
Low Firebase read volume
Minimal unnecessary network requests
Efficient Firestore listeners
```

Because the Random Forest is running locally, the basic assessment can avoid sending raw questionnaire data to a separate ML server.

---

# 43. Accessibility

The XML UI should include:

```text
content descriptions
readable text
adequate touch targets
contrast
screen-reader compatibility
keyboard/navigation support where applicable
```

---

# 44. Privacy-Safe Logging

Never log:

```text
passwords
auth tokens
full questionnaire responses
mental-health risk details unnecessarily
chat content
sensitive profile information
JioCloud credentials
```

Use structured technical logs without exposing user health information.

---

# 45. Final Project Folder

```text
mental-health-app/

├── android/
│   └── app/
│
├── functions/
│   ├── index.js
│   ├── package.json
│   ├── modules/
│   └── tests/
│
├── ml/
│   ├── data/
│   ├── notebooks/
│   ├── src/
│   ├── models/
│   └── tests/
│
├── firebase/
│   ├── firestore.rules
│   ├── storage.rules
│   └── firestore.indexes.json
│
├── docs/
│   ├── requirements.md
│   ├── architecture.md
│   ├── questionnaire.md
│   ├── ml.md
│   ├── security.md
│   ├── api-functions.md
│   └── testing.md
│
└── README.md
```

---

# 46. Final End-to-End Diagram

```text
                    ┌────────────────────┐
                    │   ANDROID APP      │
                    │ Kotlin + XML       │
                    └─────────┬──────────┘
                              |
        ┌─────────────────────┼─────────────────────┐
        |                     |                     |
        v                     v                     v
 Firebase Auth          Firestore              Storage
        |                     |                     |
        +---------------------+---------------------+
                              |
                              v
                 Firebase Cloud Functions
                        JavaScript
                              |
               +--------------+--------------+
               |                             |
               v                             v
        Booking/Rules                 Notifications
        Session logic                    FCM
               |
               v
           Firestore


ML:
Python
  ↓
Dataset
  ↓
EDA
  ↓
Preprocessing
  ↓
Random Forest
  ↓
ONNX
  ↓
Android ONNX Runtime
  ↓
Risk result
  ↓
Firestore history


CONSULTATION:
Firebase authorization
        ↓
   JioCloud
   /       \
Video       Audio

CHAT:
Firestore realtime
        ↓
User ↔ Professional
```

---

# 47. Final Demo Flow

```text
1. Launch app
2. Register
3. Login
4. Show Firebase Authentication
5. Accept consent
6. Open dashboard
7. Start 30–40 parameter assessment
8. Complete category-by-category questions
9. Run Random Forest ONNX model locally
10. Show preliminary result
11. Show stress/anxiety/depression-related indicators
12. Show digital wellbeing metrics
13. Show personalized prevention guidance
14. Open Calming Zone
15. Open professional search
16. Apply filters
17. Open verified professional profile
18. Select slot
19. JavaScript Firebase Function confirms booking
20. Receive FCM notification
21. Join JioCloud video/audio OR Firestore chat
22. End consultation
23. Show consultation history
24. Show assessment trends
25. Show Firebase Security Rules
26. Show Python training/evaluation
27. Show ONNX export
28. Show final architecture
```

---

# 48. Final MVP Boundary

### Required

```text
Kotlin + XML
Firebase Authentication
Firestore
Firebase Storage
Firebase Cloud Functions — JavaScript
FCM
Security Rules
App Check
30–40 parameter questionnaire
Random Forest
Python ML training
ONNX
Android on-device inference
Low/Moderate/High preliminary risk
Assessment history
Trend dashboard
Digital wellbeing layer
Prevention guidance
Professional search
Professional profile
Appointment booking
JioCloud video/audio
Firestore chat
Consultation history
```

### Optional but recommended

```text
Daily wellbeing check-in
Calming Zone
Breathing exercise
Personalized trend insights
```

### Future

```text
Voice emotion
Facial emotion
NLP journaling
Multimodal AI
Advanced XAI
Adaptive intervention
Gamification
AI chatbot
```

---

# 49. Final Instruction to Antigravity

When starting the project:

```text
FIRST:
Analyze this complete master document.

SECOND:
Inspect the existing project workspace.

THIRD:
Do not delete existing useful assets immediately.

FOURTH:
Create a migration/implementation plan.

FIFTH:
Start PHASE 0 only.

SIXTH:
At the end of PHASE 0, stop and report:
- what was analyzed
- files created
- assumptions
- unresolved decisions
- exact next phase
```

### Critical instruction

The old ML notebook may be wiped/rebuilt as an implementation artifact, but preserve a copy or record of it as a reference before destructive changes.

The old model must not be silently treated as the final mental-health questionnaire model.

The final system must be built around:

```text
QUESTIONNAIRE
      ↓
RANDOM FOREST
      ↓
PRELIMINARY RISK
      ↓
PREVENTION / DIGITAL WELLBEING
      ↓
PROFESSIONAL CARE
      ↓
JIOCloud VIDEO/AUDIO
      +
FIRESTORE CHAT
```

**End of Antigravity Master Implementation Document**
