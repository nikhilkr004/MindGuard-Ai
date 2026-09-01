package com.mindguard.ai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mindguard.ai.data.local.QuestionnaireLocalDataSource
import com.mindguard.ai.data.model.AssessmentResult
import com.mindguard.ai.data.model.Question
import com.mindguard.ai.data.model.QuestionCategory
import com.mindguard.ai.data.model.RiskLevel
import com.mindguard.ai.ml.FeatureMapper
import com.mindguard.ai.ml.ModelManager
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

interface AssessmentRepository {
    suspend fun getQuestions(): List<Question>
    suspend fun getCategories(): List<QuestionCategory>
    suspend fun performAssessment(
        userId: String,
        answers: Map<String, Float>
    ): Resource<AssessmentResult>
    suspend fun getAssessmentHistory(userId: String): Resource<List<AssessmentResult>>
    suspend fun getLatestAssessment(userId: String): Resource<AssessmentResult?>
}

class AssessmentRepositoryImpl(
    private val localDataSource: QuestionnaireLocalDataSource,
    private val modelManager: ModelManager,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AssessmentRepository {

    override suspend fun getQuestions(): List<Question> = localDataSource.getQuestions()

    override suspend fun getCategories(): List<QuestionCategory> = localDataSource.getCategories()

    override suspend fun performAssessment(
        userId: String,
        answers: Map<String, Float>
    ): Resource<AssessmentResult> = withContext(Dispatchers.Default) {
        try {
            // 1. Map answers to 36-feature input vector
            val featureVector = FeatureMapper.mapAnswersToFeatures(answers)
            val categoryScores = FeatureMapper.calculateCategoryScores(answers)

            // 2. Run on-device ONNX Random Forest inference
            val prediction = modelManager.runInference(featureVector)

            val assessmentId = UUID.randomUUID().toString()
            val result = AssessmentResult(
                assessmentId = assessmentId,
                userId = userId,
                riskLevel = RiskLevel.fromLabel(prediction.riskLevel.name),
                overallScore = prediction.confidence,
                categoryScores = categoryScores,
                answers = answers,
                timestamp = System.currentTimeMillis(),
                modelVersion = "1.0.0",
                questionnaireVersion = "Q-V1"
            )

            // 3. Persist to Firestore
            if (userId.isNotBlank()) {
                firestore.collection("assessments").document(assessmentId).set(result).await()
            }

            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to execute assessment", e)
        }
    }

    override suspend fun getAssessmentHistory(userId: String): Resource<List<AssessmentResult>> {
        return try {
            val snapshot = firestore.collection("assessments")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val results = snapshot.toObjects(AssessmentResult::class.java)
            Resource.Success(results)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch assessment history", e)
        }
    }

    override suspend fun getLatestAssessment(userId: String): Resource<AssessmentResult?> {
        return try {
            val snapshot = firestore.collection("assessments")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
            
            val result = snapshot.documents.firstOrNull()?.toObject(AssessmentResult::class.java)
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch latest assessment", e)
        }
    }
}
