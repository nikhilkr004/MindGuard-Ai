package com.mindguard.ai.data.model

enum class AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

enum class ConsultationMode {
    VIDEO,
    AUDIO,
    CHAT
}

data class Appointment(
    val appointmentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val professionalId: String = "",
    val professionalName: String = "",
    val professionalTitle: String = "",
    val slotId: String = "",
    val scheduledTime: Long = 0L,
    val mode: ConsultationMode = ConsultationMode.VIDEO,
    val status: AppointmentStatus = AppointmentStatus.CONFIRMED,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class ConsultationSession(
    val sessionId: String = "",
    val appointmentId: String = "",
    val userId: String = "",
    val professionalId: String = "",
    val mode: ConsultationMode = ConsultationMode.VIDEO,
    val status: String = "SCHEDULED",  // SCHEDULED, ACTIVE, ENDED
    val startedAt: Long? = null,
    val endedAt: Long? = null,
    val jioRoomId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val messageId: String = "",
    val sessionId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class Recommendation(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "General",
    val actionType: String = "NAVIGATE", // CALMING_ZONE, BREATHING, BOOKING, CHECK_IN
    val iconName: String = "ic_sparkle"
)
