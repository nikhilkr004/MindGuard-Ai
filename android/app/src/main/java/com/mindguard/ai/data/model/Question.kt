package com.mindguard.ai.data.model

import com.google.gson.annotations.SerializedName

enum class CategoryType(val id: String, val displayName: String) {
    MOOD("mood", "Mood & Emotional State"),
    ANXIETY("anxiety", "Anxiety & Worry"),
    STRESS("stress", "Stress & Coping"),
    SLEEP("sleep", "Sleep & Recovery"),
    COGNITIVE("cognitive", "Cognitive Functioning"),
    SOCIAL("social", "Daily Functioning & Social");

    companion object {
        fun fromId(id: String): CategoryType = entries.find { it.id.equals(id, ignoreCase = true) } ?: MOOD
    }
}

data class Question(
    @SerializedName("id") val id: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("categoryTitle") val categoryTitle: String = "",
    @SerializedName("text") val text: String = "",
    @SerializedName("options") val options: List<String> = listOf("Never", "Rarely", "Sometimes", "Often", "Almost Always")
)

data class QuestionCategory(
    val type: CategoryType,
    val title: String,
    val description: String,
    val questions: List<Question>
)
