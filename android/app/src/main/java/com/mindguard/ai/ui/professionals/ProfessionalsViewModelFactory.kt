package com.mindguard.ai.ui.professionals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindguard.ai.data.repository.ProfessionalRepository

class ProfessionalsViewModelFactory(
    private val professionalRepository: ProfessionalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfessionalsViewModel::class.java)) {
            return ProfessionalsViewModel(professionalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
