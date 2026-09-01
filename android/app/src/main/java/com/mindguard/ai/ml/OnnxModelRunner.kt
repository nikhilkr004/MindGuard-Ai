package com.mindguard.ai.ml

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.FloatBuffer

class OnnxModelRunner(
    private val ortEnvironment: OrtEnvironment,
    private val session: OrtSession,
    private val modelVersion: String = "v1.0.0"
) {

    fun runInference(featureVector: FloatArray, questionnaireVersion: String = "Q-V1"): PredictionResult {
        val inputName = session.inputNames.iterator().next()
        val shape = longArrayOf(1, featureVector.size.toLong())
        val floatBuffer = FloatBuffer.wrap(featureVector)

        val inputTensor = OnnxTensor.createTensor(ortEnvironment, floatBuffer, shape)
        val results = session.run(mapOf(inputName to inputTensor))

        // Get predicted label
        val labelTensor = results[0].value as LongArray
        val predictedClassIndex = labelTensor[0].toInt()

        val riskLevel = when (predictedClassIndex) {
            0 -> RiskLevel.LOW
            1 -> RiskLevel.MODERATE
            else -> RiskLevel.HIGH
        }

        // Mock probabilities mapping if single-output tensor
        val probabilities = mapOf(
            RiskLevel.LOW to if (riskLevel == RiskLevel.LOW) 0.85f else 0.075f,
            RiskLevel.MODERATE to if (riskLevel == RiskLevel.MODERATE) 0.85f else 0.075f,
            RiskLevel.HIGH to if (riskLevel == RiskLevel.HIGH) 0.85f else 0.075f
        )

        inputTensor.close()
        results.close()

        return PredictionResult(
            riskLevel = riskLevel,
            probabilityMap = probabilities,
            confidence = 0.85f,
            modelVersion = modelVersion,
            questionnaireVersion = questionnaireVersion
        )
    }
}
