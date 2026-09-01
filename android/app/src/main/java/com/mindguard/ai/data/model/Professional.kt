package com.mindguard.ai.data.model

data class Slot(
    val slotId: String = "",
    val professionalId: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val isBooked: Boolean = false,
    val bookedByUserId: String? = null
)

data class Professional(
    val professionalId: String = "",
    val name: String = "",
    val title: String = "",                 // e.g. "Clinical Psychologist"
    val qualifications: String = "",        // e.g. "M.Phil Clinical Psychology, RCI Registered"
    val specialty: String = "General",     // e.g. "Anxiety & Stress", "Mood Disorders"
    val experienceYears: Int = 0,
    val bio: String = "",
    val languages: List<String> = listOf("English", "Hindi"),
    val photoUrl: String = "",
    val rating: Double = 4.8,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val consultationFee: Double = 0.0,
    val availableSlots: List<Slot> = emptyList()
)
