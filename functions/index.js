const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

const appointments = require("./modules/appointments");
const professionals = require("./modules/professionals");
const consultations = require("./modules/consultations");
const notifications = require("./modules/notifications");
const cleanup = require("./modules/cleanup");

// Callable APIs
exports.createAppointment = appointments.createAppointment;
exports.cancelAppointment = appointments.cancelAppointment;
exports.rescheduleAppointment = appointments.rescheduleAppointment;

exports.verifyProfessional = professionals.verifyProfessional;
exports.createConsultationSession = consultations.createConsultationSession;

// Background triggers
exports.onAppointmentCreated = appointments.onAppointmentCreated;
exports.onAppointmentUpdated = appointments.onAppointmentUpdated;

// Scheduled tasks (Cron)
exports.sendAppointmentReminder = notifications.sendAppointmentReminder;
exports.cleanupExpiredSlots = cleanup.cleanupExpiredSlots;
