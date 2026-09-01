package com.mindguard.ai.ml

object FeatureMapper {
    val ORDERED_FEATURE_KEYS = listOf(
        // Mood & Emotional State
        "mood_01", "mood_02", "mood_03", "mood_04", "mood_05", "mood_06",
        // Anxiety & Worry
        "anxiety_01", "anxiety_02", "anxiety_03", "anxiety_04", "anxiety_05", "anxiety_06",
        // Stress & Coping
        "stress_01", "stress_02", "stress_03", "stress_04", "stress_05", "stress_06",
        // Sleep & Recovery
        "sleep_01", "sleep_02", "sleep_03", "sleep_04", "sleep_05", "sleep_06",
        // Cognitive Functioning
        "cog_01", "cog_02", "cog_03", "cog_04", "cog_05", "cog_06",
        // Daily Functioning & Social
        "social_01", "social_02", "social_03", "social_04", "social_05", "social_06"
    )

    fun mapAnswersToFeatureVector(answersMap: Map<String, Float>): FloatArray {
        val vector = FloatArray(ORDERED_FEATURE_KEYS.size)
        for (i in ORDERED_FEATURE_KEYS.indices) {
            val key = ORDERED_FEATURE_KEYS[i]
            // Normalize default to 0.5 (Neutral) if missing
            vector[i] = answersMap[key] ?: 0.5f
        }
        return vector
    }
}
