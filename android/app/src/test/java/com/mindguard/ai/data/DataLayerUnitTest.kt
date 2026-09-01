package com.mindguard.ai.data

import com.mindguard.ai.data.model.CategoryType
import com.mindguard.ai.data.model.DailyCheckIn
import com.mindguard.ai.data.model.RiskLevel
import com.mindguard.ai.data.model.User
import com.mindguard.ai.data.model.UserRole
import com.mindguard.ai.data.repository.WellbeingRepositoryImpl
import com.mindguard.ai.ml.FeatureMapper
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataLayerUnitTest {

    @Test
    fun testUserModelDefaults() {
        val user = User(
            uid = "test_uid_123",
            email = "user@test.com",
            displayName = "Jane Doe",
            role = UserRole.USER,
            consentAccepted = true
        )
        assertEquals("test_uid_123", user.uid)
        assertEquals("user@test.com", user.email)
        assertEquals(UserRole.USER, user.role)
        assertTrue(user.consentAccepted)
    }

    @Test
    fun testRiskLevelMapping() {
        assertEquals(RiskLevel.LOW, RiskLevel.fromLabel("low"))
        assertEquals(RiskLevel.MODERATE, RiskLevel.fromLabel("moderate"))
        assertEquals(RiskLevel.HIGH, RiskLevel.fromLabel("high"))
        assertEquals(RiskLevel.LOW, RiskLevel.fromLabel("unknown"))
    }

    @Test
    fun testCategoryTypesEnum() {
        assertEquals(6, CategoryType.entries.size)
        assertEquals(CategoryType.MOOD, CategoryType.fromId("mood"))
        assertEquals(CategoryType.ANXIETY, CategoryType.fromId("anxiety"))
        assertEquals(CategoryType.STRESS, CategoryType.fromId("stress"))
        assertEquals(CategoryType.SLEEP, CategoryType.fromId("sleep"))
        assertEquals(CategoryType.COGNITIVE, CategoryType.fromId("cognitive"))
        assertEquals(CategoryType.SOCIAL, CategoryType.fromId("social"))
    }

    @Test
    fun testFeatureMapperVectorSizeAndCategoryScores() {
        val sampleAnswers = mutableMapOf<String, Float>()
        // Fill 36 parameters
        for (i in 1..6) {
            sampleAnswers["mood_0$i"] = 0.5f
            sampleAnswers["anxiety_0$i"] = 0.75f
            sampleAnswers["stress_0$i"] = 1.0f
            sampleAnswers["sleep_0$i"] = 0.25f
            sampleAnswers["cog_0$i"] = 0.0f
            sampleAnswers["social_0$i"] = 0.5f
        }

        val vector = FeatureMapper.mapAnswersToFeatures(sampleAnswers)
        assertEquals(36, vector.size)

        val categoryScores = FeatureMapper.calculateCategoryScores(sampleAnswers)
        assertEquals(6, categoryScores.size)
        assertEquals(0.5f, categoryScores["mood"] ?: 0f, 0.01f)
        assertEquals(0.75f, categoryScores["anxiety"] ?: 0f, 0.01f)
        assertEquals(1.0f, categoryScores["stress"] ?: 0f, 0.01f)
        assertEquals(0.25f, categoryScores["sleep"] ?: 0f, 0.01f)
        assertEquals(0.0f, categoryScores["cognitive"] ?: 0f, 0.01f)
        assertEquals(0.5f, categoryScores["social"] ?: 0f, 0.01f)
    }

    @Test
    fun testRecommendationEngineHighStressAndScreenTime() = runBlocking {
        val repo = WellbeingRepositoryImpl()
        val highStressCheckin = DailyCheckIn(
            checkInId = "c1",
            userId = "u1",
            moodRating = 2,
            stressRating = 5,
            sleepRating = 2
        )
        
        val recs = repo.getPersonalizedRecommendations(
            latestCheckIn = highStressCheckin,
            latestScreenTimeMinutes = 400L // > 6 hours
        )

        assertTrue("Recommendations should not be empty", recs.isNotEmpty())
        assertTrue("Should recommend breathing when stress is high", recs.any { it.actionType == "BREATHING" })
        assertTrue("Should recommend digital break when screen time > 360m", recs.any { it.actionType == "DIGITAL_BREAK" })
        assertTrue("Should recommend sleep tips when sleep rating <= 2", recs.any { it.actionType == "SLEEP_TIPS" })
    }

    @Test
    fun testResourceSealedClassStates() {
        val successRes: Resource<String> = Resource.Success("Data payload")
        val errorRes: Resource<String> = Resource.Error("Network failure")
        val loadingRes: Resource<String> = Resource.Loading

        assertTrue(successRes is Resource.Success)
        assertEquals("Data payload", (successRes as Resource.Success).data)
        assertTrue(errorRes is Resource.Error)
        assertEquals("Network failure", (errorRes as Resource.Error).message)
        assertTrue(loadingRes is Resource.Loading)
    }
}
