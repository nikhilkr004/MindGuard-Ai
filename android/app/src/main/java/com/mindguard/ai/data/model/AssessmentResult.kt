package com.mindguard.ai.data.model

enum class RiskLevel(val label: String) {
    LOW("Low"),
    MODERATE("Moderate"),
    HIGH("High / Elevated Indicators");

    companion object {
        fun fromLabel(label: String): RiskLevel = when (label.uppercase()) {
            "LOW" -> LOW
            "MODERATE" -> MODERATE
            "HIGH" -> HIGH
            else -> LOW
        }
    }
}

data class AssessmentResult(
    val assessmentId: String = "",
    val userId: String = "",
    val riskLevel: String = "LOW",
    val overallScore: Float = 0.0f,
    val categoryScores: Map<String, Float> = emptyMap(),
    val answers: Map<String, Float> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis(),
    val modelVersion: String = "1.0.0",
    val questionnaireVersion: String = "Q-V1"
) {
    fun getRiskLevelEnum(): RiskLevel = RiskLevel.fromLabel(riskLevel)

    fun toMap(): Map<String, Any?> = mapOf(
        "assessmentId" to assessmentId,
        "userId" to userId,
        "riskLevel" to riskLevel,
        "overallScore" to overallScore,
        "categoryScores" to categoryScores,
        "answers" to answers,
        "timestamp" to timestamp,
        "modelVersion" to modelVersion,
        "questionnaireVersion" to questionnaireVersion
    )
}
