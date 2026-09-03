const functions = require("firebase-functions");
const admin = require("firebase-admin");

const db = admin.firestore();

/**
 * Creates an appointment atomically to avoid race conditions and double-booking.
 */
exports.createAppointment = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated.");
  }

  const { professionalId, slotId, consultationType, mode } = data;
  const effectiveConsultationType = consultationType || mode || "video";
  const userId = context.auth.uid;

  if (!professionalId || !slotId) {
    throw new functions.https.HttpsError("invalid-argument", "Missing required appointment parameters.");
  }

  const slotRef = db.collection("availability").doc(slotId);
  const appointmentRef = db.collection("appointments").doc();

  return await db.runTransaction(async (transaction) => {
    const slotDoc = await transaction.get(slotRef);

    if (!slotDoc.exists || slotDoc.data().status !== "available") {
      throw new functions.https.HttpsError("failed-precondition", "This slot is no longer available.");
    }

    if (slotDoc.data().professionalId !== professionalId) {
      throw new functions.https.HttpsError("invalid-argument", "Slot does not belong to the specified professional.");
    }

    // Reserve slot
    transaction.update(slotRef, {
      status: "booked",
      bookedBy: userId,
      appointmentId: appointmentRef.id,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    // Create appointment document
    transaction.set(appointmentRef, {
      id: appointmentRef.id,
      userId: userId,
      professionalId: professionalId,
      slotId: slotId,
      startTime: slotDoc.data().startTime,
      endTime: slotDoc.data().endTime,
      consultationType: effectiveConsultationType.toLowerCase(), // 'video', 'audio', or 'chat'
      status: "confirmed",
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    return {
      appointmentId: appointmentRef.id,
      status: "confirmed",
    };
  });
});

/**
 * Cancels an existing appointment and reopens the availability slot.
 */
exports.cancelAppointment = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated.");
  }

  const { appointmentId, reason } = data;
  const userId = context.auth.uid;

  const apptRef = db.collection("appointments").doc(appointmentId);

  return await db.runTransaction(async (transaction) => {
    const apptDoc = await transaction.get(apptRef);
    if (!apptDoc.exists) {
      throw new functions.https.HttpsError("not-found", "Appointment not found.");
    }

    const apptData = apptDoc.data();
    if (apptData.userId !== userId && apptData.professionalId !== userId) {
      throw new functions.https.HttpsError("permission-denied", "Unauthorized to cancel this appointment.");
    }

    transaction.update(apptRef, {
      status: "cancelled",
      cancellationReason: reason || "Cancelled by user",
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    const slotRef = db.collection("availability").doc(apptData.slotId);
    transaction.update(slotRef, {
      status: "available",
      bookedBy: null,
      appointmentId: null,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    return { success: true, status: "cancelled" };
  });
});

/**
 * Reschedules an appointment to a new slot atomically.
 */
exports.rescheduleAppointment = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated.");
  }

  const { appointmentId, newSlotId } = data;
  const userId = context.auth.uid;

  const apptRef = db.collection("appointments").doc(appointmentId);
  const newSlotRef = db.collection("availability").doc(newSlotId);

  return await db.runTransaction(async (transaction) => {
    const apptDoc = await transaction.get(apptRef);
    if (!apptDoc.exists) {
      throw new functions.https.HttpsError("not-found", "Appointment not found.");
    }

    const apptData = apptDoc.data();
    if (apptData.userId !== userId && apptData.professionalId !== userId) {
      throw new functions.https.HttpsError("permission-denied", "Unauthorized to reschedule this appointment.");
    }

    const newSlotDoc = await transaction.get(newSlotRef);
    if (!newSlotDoc.exists || newSlotDoc.data().status !== "available") {
      throw new functions.https.HttpsError("failed-precondition", "New slot is unavailable.");
    }

    // Release old slot
    const oldSlotRef = db.collection("availability").doc(apptData.slotId);
    transaction.update(oldSlotRef, {
      status: "available",
      bookedBy: null,
      appointmentId: null,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    // Reserve new slot
    transaction.update(newSlotRef, {
      status: "booked",
      bookedBy: userId,
      appointmentId: appointmentId,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    // Update appointment
    transaction.update(apptRef, {
      slotId: newSlotId,
      startTime: newSlotDoc.data().startTime,
      endTime: newSlotDoc.data().endTime,
      status: "rescheduled",
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    return { success: true, status: "rescheduled" };
  });
});

exports.onAppointmentCreated = functions.firestore
  .document("appointments/{appointmentId}")
  .onCreate(async (snap, context) => {
    // Background dispatch trigger
    return null;
  });

exports.onAppointmentUpdated = functions.firestore
  .document("appointments/{appointmentId}")
  .onUpdate(async (change, context) => {
    return null;
  });
