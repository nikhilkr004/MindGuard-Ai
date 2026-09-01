const functions = require("firebase-functions");
const admin = require("firebase-admin");

const db = admin.firestore();

/**
 * Scheduled cron to check for upcoming appointments and dispatch push notifications via FCM.
 */
exports.sendAppointmentReminder = functions.pubsub
  .schedule("every 15 minutes")
  .onRun(async (context) => {
    const now = new Date();
    const futureWindow = new Date(now.getTime() + 30 * 60 * 1000);

    const snapshot = await db
      .collection("appointments")
      .where("status", "==", "confirmed")
      .where("startTime", ">=", now)
      .where("startTime", "<=", futureWindow)
      .get();

    if (snapshot.empty) {
      return null;
    }

    const promises = [];
    snapshot.forEach((doc) => {
      const appt = doc.data();
      const payload = {
        notification: {
          title: "Upcoming MindGuard Consultation",
          body: `Your ${appt.consultationType} consultation is starting soon.`,
        },
        data: {
          appointmentId: doc.id,
          click_action: "FLUTTER_NOTIFICATION_CLICK",
        },
      };
      promises.push(admin.messaging().sendToTopic(`user_${appt.userId}`, payload));
    });

    await Promise.all(promises);
    return null;
  });
