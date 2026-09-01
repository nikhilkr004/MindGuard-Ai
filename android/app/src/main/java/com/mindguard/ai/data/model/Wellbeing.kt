package com.mindguard.ai.data.model

data class DailyCheckIn(
    val checkInId: String = "",
    val userId: String = "",
    val moodRating: Int = 3,       // 1-5 scale
    val energyRating: Int = 3,     // 1-5 scale
    val stressRating: Int = 3,     // 1-5 scale
    val sleepRating: Int = 3,      // 1-5 scale
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class DigitalWellbeingMetric(
    val recordId: String = "",
    val userId: String = "",
    val totalScreenMinutes: Long = 0L,
    val lateNightMinutes: Long = 0L,
    val longestSessionMinutes: Long = 0L,
    val unlocksCount: Int = 0,
    val appCategoryBreakdown: Map<String, Long> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
