package com.mindguard.ai.ml

enum class RiskLevel {
    LOW,
    MODERATE,
    HIGH
}

data class PredictionResult(
    val riskLevel: RiskLevel,
    val probabilityMap: Map<RiskLevel, Float>,
    val confidence: Float,
    val modelVersion: String,
    val questionnaireVersion: String,
    val timestamp: Long = System.currentTimeMillis()
)
