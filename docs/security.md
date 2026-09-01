# MindGuard AI — Security & Privacy Architecture

## 1. Core Principles
1. **Zero Trust & Explicit Ownership:** Users own their mental health assessment records. No client can directly read or mutate another user's personal health information.
2. **Server-Side Authorization:** Critical actions (appointment booking, slot release, provider credentialing, teleconsultation room generation) must execute in trusted Cloud Functions.
3. **No Unsanitized Logging:** Health questionnaire responses, passwords, session tokens, and personal identifying details are never written to log sinks.

---

## 2. Role-Based Access Control (RBAC)

| Role | Access Scope | Verification Method |
|---|---|---|
| **User (Patient)** | Read/write own profile, assessments, daily check-ins, screen usage, own bookings | Firebase Auth `auth.uid` |
| **Professional** | Read/write own professional profile, slots, and authorized appointment sessions | Custom Claims `token.role == 'professional'` & verified badge |
| **Admin** | Read/write platform taxonomy, verify professionals, inspect audit logs | Custom Claims `token.role == 'admin'` |

---

## 3. Firebase App Check
Firebase App Check (Play Integrity on Android) prevents unauthorized clients, automated bots, and reverse-engineered apps from making calls to Firebase backend resources.
