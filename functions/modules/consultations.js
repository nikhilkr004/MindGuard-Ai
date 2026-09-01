const functions = require("firebase-functions");
const admin = require("firebase-admin");

const db = admin.firestore();

/**
 * Initializes a secure consultation session (JioCloud video/audio or Firestore chat).
 */
exports.createConsultationSession = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated.");
  }

  const { appointmentId } = data;
  const userId = context.auth.uid;

  if (!appointmentId) {
    throw new functions.https.HttpsError("invalid-argument", "Missing appointmentId.");
  }

  const apptDoc = await db.collection("appointments").doc(appointmentId).get();
  if (!apptDoc.exists) {
    throw new functions.https.HttpsError("not-found", "Appointment record not found.");
  }

  const apptData = apptDoc.data();
  if (apptData.userId !== userId && apptData.professionalId !== userId) {
    throw new functions.https.HttpsError("permission-denied", "Unauthorized access to this session.");
  }

  const sessionRef = db.collection("consultation_sessions").doc(appointmentId);
  const sessionDoc = await sessionRef.get();

  if (!sessionDoc.exists) {
    await sessionRef.set({
      id: appointmentId,
      appointmentId: appointmentId,
      patientId: apptData.userId,
      professionalId: apptData.professionalId,
      consultationType: apptData.consultationType,
      status: "active",
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });
  }

  return {
    sessionId: appointmentId,
    consultationType: apptData.consultationType,
    status: "active",
  };
});
