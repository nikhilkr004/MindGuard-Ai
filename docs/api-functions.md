# MindGuard AI — Cloud Functions API Specification

## 1. Overview
All sensitive business transactions are executed via callable Firebase Cloud Functions in JavaScript, ensuring ACID-like guarantees in Cloud Firestore transactions.

---

## 2. Callable Functions

### `createAppointment(data)`
- **Caller:** Authenticated User
- **Payload:** `{ professionalId: string, slotId: string, consultationType: 'video' | 'audio' | 'chat' }`
- **Logic:**
  1. Validates user authentication.
  2. Runs Firestore Transaction: checks slot status is `available`.
  3. Marks slot as `booked`, creates document in `appointments/`.
  4. Dispatches push notification via FCM to professional.
- **Returns:** `{ appointmentId: string, status: 'confirmed' }`

### `cancelAppointment(data)`
- **Caller:** Patient or Assigned Professional
- **Payload:** `{ appointmentId: string, reason: string }`
- **Logic:** Updates appointment status to `cancelled` and atomically sets slot back to `available`.

### `verifyProfessional(data)`
- **Caller:** Admin only
- **Payload:** `{ professionalId: string, isApproved: boolean }`
- **Logic:** Updates professional status to `verified` and sets custom claim.

### `createConsultationSession(data)`
- **Caller:** Participant of scheduled appointment
- **Payload:** `{ appointmentId: string }`
- **Logic:** Verifies active appointment time window and issues authorized JioCloud room token & Firestore chat session.

---

## 3. Scheduled Functions (Cron Automation)
- **`sendAppointmentReminder`**: Runs every 15 minutes to notify users of appointments starting in the next 30 minutes.
- **`cleanupExpiredSlots`**: Runs nightly to archive past unbooked availability slots.
