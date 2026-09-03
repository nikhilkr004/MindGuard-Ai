package com.mindguard.ai.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindguard.ai.data.repository.AppointmentRepository

class BookingViewModelFactory(
    private val appointmentRepository: AppointmentRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(appointmentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
