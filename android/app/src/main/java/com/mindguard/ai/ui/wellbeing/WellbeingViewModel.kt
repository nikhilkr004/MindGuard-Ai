package com.mindguard.ai.ui.wellbeing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindguard.ai.data.model.DailyCheckIn
import com.mindguard.ai.data.model.DigitalWellbeingMetric
import com.mindguard.ai.data.model.Recommendation
import com.mindguard.ai.data.repository.AuthRepository
import com.mindguard.ai.data.repository.WellbeingRepository
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.launch

class WellbeingViewModel(
    private val wellbeingRepository: WellbeingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _saveCheckInStatus = MutableLiveData<Resource<Unit>>()
    val saveCheckInStatus: LiveData<Resource<Unit>> = _saveCheckInStatus

    private val _latestCheckIn = MutableLiveData<DailyCheckIn?>()
    val latestCheckIn: LiveData<DailyCheckIn?> = _latestCheckIn

    private val _checkInHistory = MutableLiveData<List<DailyCheckIn>>()
    val checkInHistory: LiveData<List<DailyCheckIn>> = _checkInHistory

    private val _digitalWellbeing = MutableLiveData<DigitalWellbeingMetric?>()
    val digitalWellbeing: LiveData<DigitalWellbeingMetric?> = _digitalWellbeing

    private val _recommendations = MutableLiveData<List<Recommendation>>()
    val recommendations: LiveData<List<Recommendation>> = _recommendations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadLatestData()
    }

    fun loadLatestData() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _isLoading.value = true

            // Fetch latest check-in
            when (val checkInRes = wellbeingRepository.getLatestCheckIn(userId)) {
                is Resource.Success -> {
                    _latestCheckIn.value = checkInRes.data
                }
                else -> {
                    _latestCheckIn.value = null
                }
            }

            // Fetch digital wellbeing history
            when (val dwRes = wellbeingRepository.getDigitalWellbeingHistory(userId, 1)) {
                is Resource.Success -> {
                    val latest = dwRes.data.firstOrNull() ?: generateDefaultDigitalWellbeing(userId)
                    _digitalWellbeing.value = latest
                }
                else -> {
                    val defaultMetric = generateDefaultDigitalWellbeing(userId)
                    _digitalWellbeing.value = defaultMetric
                }
            }

            // Generate personalized recommendations
            val screenMins = _digitalWellbeing.value?.totalScreenMinutes ?: 240L
            val recs = wellbeingRepository.getPersonalizedRecommendations(_latestCheckIn.value, screenMins)
            _recommendations.value = recs

            _isLoading.value = false
        }
    }

    fun saveCheckIn(mood: Int, energy: Int, stress: Int, sleep: Int, notes: String?) {
        val userId = authRepository.currentUserId
        if (userId == null) {
            _saveCheckInStatus.value = Resource.Error("User not authenticated")
            return
        }

        viewModelScope.launch {
            _saveCheckInStatus.value = Resource.Loading
            val checkIn = DailyCheckIn(
                userId = userId,
                moodRating = mood,
                energyRating = energy,
                stressRating = stress,
                sleepRating = sleep,
                notes = notes?.takeIf { it.isNotBlank() },
                timestamp = System.currentTimeMillis()
            )

            val result = wellbeingRepository.saveDailyCheckIn(checkIn)
            if (result is Resource.Success) {
                _latestCheckIn.value = checkIn
                // Refresh recommendations
                val screenMins = _digitalWellbeing.value?.totalScreenMinutes ?: 240L
                _recommendations.value = wellbeingRepository.getPersonalizedRecommendations(checkIn, screenMins)
            }
            _saveCheckInStatus.value = result
        }
    }

    private fun generateDefaultDigitalWellbeing(userId: String): DigitalWellbeingMetric {
        return DigitalWellbeingMetric(
            userId = userId,
            totalScreenMinutes = 342L, // 5h 42m
            lateNightMinutes = 75L,    // 1h 15m
            longestSessionMinutes = 130L, // 2h 10m
            unlocksCount = 48,
            appCategoryBreakdown = mapOf(
                "Social & Messaging" to 165L,
                "Productivity & Education" to 110L,
                "Entertainment" to 67L
            ),
            timestamp = System.currentTimeMillis()
        )
    }
}
