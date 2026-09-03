package com.mindguard.ai.data

import com.mindguard.ai.data.model.DailyCheckIn
import com.mindguard.ai.data.model.DigitalWellbeingMetric
import com.mindguard.ai.data.repository.WellbeingRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WellbeingUnitTest {

    private lateinit var wellbeingRepository: WellbeingRepositoryImpl

    @Before
    fun setUp() {
        wellbeingRepository = WellbeingRepositoryImpl()
    }

    @Test
    fun testHighStressTriggersBreathingAndGroundingRecommendations() = runBlocking {
        val checkIn = DailyCheckIn(
            userId = "user_123",
            moodRating = 2,
            energyRating = 2,
            stressRating = 5,
            sleepRating = 3
        )

        val recs = wellbeingRepository.getPersonalizedRecommendations(
            latestCheckIn = checkIn,
            latestScreenTimeMinutes = 180L
        )

        assertTrue("Expected at least one recommendation for high stress", recs.isNotEmpty())
        assertTrue("Expected breathing action", recs.any { it.actionType == "BREATHING" })
        assertTrue("Expected grounding action", recs.any { it.actionType == "GROUNDING" })
    }

    @Test
    fun testHighScreenTimeTriggersDigitalBreakRecommendation() = runBlocking {
        val checkIn = DailyCheckIn(
            userId = "user_123",
            moodRating = 4,
            energyRating = 4,
            stressRating = 2,
            sleepRating = 4
        )

        // 7 hours = 420 mins (> 360 mins threshold)
        val recs = wellbeingRepository.getPersonalizedRecommendations(
            latestCheckIn = checkIn,
            latestScreenTimeMinutes = 420L
        )

        assertTrue("Expected digital break recommendation for excessive screen time", recs.any { it.actionType == "DIGITAL_BREAK" })
    }

    @Test
    fun testPoorSleepTriggersSleepHygieneRecommendation() = runBlocking {
        val checkIn = DailyCheckIn(
            userId = "user_123",
            moodRating = 3,
            energyRating = 2,
            stressRating = 2,
            sleepRating = 1
        )

        val recs = wellbeingRepository.getPersonalizedRecommendations(
            latestCheckIn = checkIn,
            latestScreenTimeMinutes = 120L
        )

        assertTrue("Expected sleep hygiene tips for low sleep rating", recs.any { it.actionType == "SLEEP_TIPS" })
    }

    @Test
    fun testDefaultEquilibriumProvidesMindfulnessRecommendation() = runBlocking {
        val recs = wellbeingRepository.getPersonalizedRecommendations(
            latestCheckIn = null,
            latestScreenTimeMinutes = 150L
        )

        assertTrue("Expected default wellness recommendations", recs.isNotEmpty())
        assertTrue("Expected daily mindfulness or professional exploration", recs.any { it.actionType == "BREATHING" || it.actionType == "BOOKING" })
    }

    @Test
    fun testDigitalWellbeingMetricCreation() {
        val metric = DigitalWellbeingMetric(
            recordId = "dw_01",
            userId = "user_123",
            totalScreenMinutes = 320L,
            lateNightMinutes = 45L,
            longestSessionMinutes = 90L,
            unlocksCount = 35,
            appCategoryBreakdown = mapOf("Social" to 140L, "Productivity" to 100L)
        )

        assertEquals("user_123", metric.userId)
        assertEquals(320L, metric.totalScreenMinutes)
        assertEquals(2, metric.appCategoryBreakdown.size)
    }
}
