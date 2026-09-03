package com.mindguard.ai.ui.wellbeing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindguard.ai.data.repository.AuthRepository
import com.mindguard.ai.data.repository.WellbeingRepository

class WellbeingViewModelFactory(
    private val wellbeingRepository: WellbeingRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WellbeingViewModel::class.java)) {
            return WellbeingViewModel(wellbeingRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
