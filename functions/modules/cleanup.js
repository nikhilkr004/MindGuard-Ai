const functions = require("firebase-functions");
const admin = require("firebase-admin");

const db = admin.firestore();

/**
 * Scheduled cleanup job to expire past unbooked availability slots.
 */
exports.cleanupExpiredSlots = functions.pubsub
  .schedule("every 24 hours")
  .onRun(async (context) => {
    const now = new Date();

    const snapshot = await db
      .collection("availability")
      .where("status", "==", "available")
      .where("startTime", "<", now)
      .get();

    if (snapshot.empty) {
      return null;
    }

    const batch = db.batch();
    snapshot.forEach((doc) => {
      batch.update(doc.ref, {
        status: "expired",
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    });

    await batch.commit();
    return null;
  });
