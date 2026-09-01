const functions = require("firebase-functions");
const admin = require("firebase-admin");

const db = admin.firestore();

/**
 * Admin action to verify or reject a licensed mental healthcare professional.
 */
exports.verifyProfessional = functions.https.onCall(async (data, context) => {
  if (!context.auth || context.auth.token.role !== "admin") {
    throw new functions.https.HttpsError("permission-denied", "Only administrators can verify professionals.");
  }

  const { professionalId, isApproved, remarks } = data;
  if (!professionalId || isApproved === undefined) {
    throw new functions.https.HttpsError("invalid-argument", "Missing professional verification parameters.");
  }

  const profRef = db.collection("professionals").doc(professionalId);
  const status = isApproved ? "verified" : "rejected";

  await profRef.update({
    status: status,
    verifiedAt: isApproved ? admin.firestore.FieldValue.serverTimestamp() : null,
    verifiedBy: context.auth.uid,
    verificationRemarks: remarks || null,
    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
  });

  // Assign custom claims
  if (isApproved) {
    await admin.auth().setCustomUserClaims(professionalId, {
      role: "professional",
      verified: true,
    });
  }

  return { professionalId, status };
});
