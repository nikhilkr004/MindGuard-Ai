package com.mindguard.ai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mindguard.ai.data.model.DailyCheckIn
import com.mindguard.ai.data.model.DigitalWellbeingMetric
import com.mindguard.ai.data.model.Recommendation
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.tasks.await
import java.util.UUID

interface WellbeingRepository {
    suspend fun saveDailyCheckIn(checkIn: DailyCheckIn): Resource<Unit>
    suspend fun getDailyCheckIns(userId: String, limit: Int = 14): Resource<List<DailyCheckIn>>
    suspend fun getLatestCheckIn(userId: String): Resource<DailyCheckIn?>
    suspend fun saveDigitalWellbeing(metric: DigitalWellbeingMetric): Resource<Unit>
    suspend fun getDigitalWellbeingHistory(userId: String, limit: Int = 7): Resource<List<DigitalWellbeingMetric>>
    suspend fun getPersonalizedRecommendations(
        latestCheckIn: DailyCheckIn?,
        latestScreenTimeMinutes: Long
    ): List<Recommendation>
}

class WellbeingRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : WellbeingRepository {

    override suspend fun saveDailyCheckIn(checkIn: DailyCheckIn): Resource<Unit> {
        return try {
            val id = if (checkIn.checkInId.isBlank()) UUID.randomUUID().toString() else checkIn.checkInId
            val record = checkIn.copy(checkInId = id)
            firestore.collection("daily_checkins").document(id).set(record).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to save daily check-in", e)
        }
    }

    override suspend fun getDailyCheckIns(userId: String, limit: Int): Resource<List<DailyCheckIn>> {
        return try {
            val snapshot = firestore.collection("daily_checkins")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val checkins = snapshot.toObjects(DailyCheckIn::class.java)
            Resource.Success(checkins)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to retrieve check-in history", e)
        }
    }

    override suspend fun getLatestCheckIn(userId: String): Resource<DailyCheckIn?> {
        return try {
            val snapshot = firestore.collection("daily_checkins")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
            
            val checkin = snapshot.documents.firstOrNull()?.toObject(DailyCheckIn::class.java)
            Resource.Success(checkin)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to retrieve latest check-in", e)
        }
    }

    override suspend fun saveDigitalWellbeing(metric: DigitalWellbeingMetric): Resource<Unit> {
        return try {
            val id = if (metric.recordId.isBlank()) UUID.randomUUID().toString() else metric.recordId
            val record = metric.copy(recordId = id)
            firestore.collection("digital_wellbeing").document(id).set(record).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to save digital wellbeing metric", e)
        }
    }

    override suspend fun getDigitalWellbeingHistory(userId: String, limit: Int): Resource<List<DigitalWellbeingMetric>> {
        return try {
            val snapshot = firestore.collection("digital_wellbeing")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val list = snapshot.toObjects(DigitalWellbeingMetric::class.java)
            Resource.Success(list)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to retrieve digital wellbeing history", e)
        }
    }

    override suspend fun getPersonalizedRecommendations(
        latestCheckIn: DailyCheckIn?,
        latestScreenTimeMinutes: Long
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 1. Stress Heuristic
        if (latestCheckIn != null && latestCheckIn.stressRating >= 4) {
            recommendations.add(
                Recommendation(
                    id = "rec_stress_breathing",
                    title = "Take a 2-Minute Calming Breath",
                    description = "Your recent stress indicator is elevated. A brief box breathing cycle can help reset your nervous system.",
                    category = "Calming Zone",
                    actionType = "BREATHING",
                    iconName = "ic_wind"
                )
            )
            recommendations.add(
                Recommendation(
                    id = "rec_grounding",
                    title = "5-4-3-2-1 Sensory Grounding",
                    description = "Reconnect with your immediate environment when feeling overwhelmed.",
                    category = "Grounding",
                    actionType = "GROUNDING",
                    iconName = "ic_sparkle"
                )
            )
        }

        // 2. Screen Time Heuristic (> 360 mins / 6 hrs)
        if (latestScreenTimeMinutes >= 360) {
            recommendations.add(
                Recommendation(
                    id = "rec_digital_break",
                    title = "Digital Break Recommended",
                    description = "You have accumulated over ${latestScreenTimeMinutes / 60} hours of screen time today. Consider a 15-minute offline stretch.",
                    category = "Digital Wellbeing",
                    actionType = "DIGITAL_BREAK",
                    iconName = "ic_phone_off"
                )
            )
        }

        // 3. Sleep Heuristic
        if (latestCheckIn != null && latestCheckIn.sleepRating <= 2) {
            recommendations.add(
                Recommendation(
                    id = "rec_sleep_hygiene",
                    title = "Optimize Your Evening Wind-down",
                    description = "Try powering off screens 30 minutes before sleep and engaging in calming reflection.",
                    category = "Sleep & Rest",
                    actionType = "SLEEP_TIPS",
                    iconName = "ic_moon"
                )
            )
        }

        // 4. Default Routine Wellness if no specific triggers
        if (recommendations.isEmpty()) {
            recommendations.add(
                Recommendation(
                    id = "rec_daily_mindfulness",
                    title = "Daily Mindfulness Check",
                    description = "Take 60 seconds to practice mindful breathing and maintain your healthy equilibrium.",
                    category = "Wellbeing",
                    actionType = "BREATHING",
                    iconName = "ic_smile"
                )
            )
            recommendations.add(
                Recommendation(
                    id = "rec_explore_professionals",
                    title = "Explore Care Providers",
                    description = "Browse certified mental health professionals available for teleconsultations.",
                    category = "Support",
                    actionType = "BOOKING",
                    iconName = "ic_doctor"
                )
            )
        }

        return recommendations
    }
}
