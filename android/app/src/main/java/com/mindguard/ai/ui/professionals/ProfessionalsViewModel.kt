package com.mindguard.ai.ui.professionals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindguard.ai.data.model.Professional
import com.mindguard.ai.data.model.Slot
import com.mindguard.ai.data.repository.ProfessionalRepository
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.launch

class ProfessionalsViewModel(
    private val professionalRepository: ProfessionalRepository
) : ViewModel() {

    private val _professionals = MutableLiveData<Resource<List<Professional>>>()
    val professionals: LiveData<Resource<List<Professional>>> = _professionals

    private val _filteredProfessionals = MutableLiveData<List<Professional>>()
    val filteredProfessionals: LiveData<List<Professional>> = _filteredProfessionals

    private val _selectedProfessional = MutableLiveData<Resource<Professional>>()
    val selectedProfessional: LiveData<Resource<Professional>> = _selectedProfessional

    private val _availableSlots = MutableLiveData<Resource<List<Slot>>>()
    val availableSlots: LiveData<Resource<List<Slot>>> = _availableSlots

    private var allProfessionalsList: List<Professional> = emptyList()
    private var currentSearchQuery: String = ""
    private var currentSpecialtyFilter: String = "All"

    init {
        loadProfessionals()
    }

    fun loadProfessionals(specialty: String? = null, language: String? = null) {
        viewModelScope.launch {
            _professionals.value = Resource.Loading
            when (val result = professionalRepository.getProfessionals(specialty, language)) {
                is Resource.Success -> {
                    val list = if (result.data.isEmpty()) getSampleProfessionals() else result.data
                    allProfessionalsList = list
                    applyFilters()
                    _professionals.value = Resource.Success(list)
                }
                is Resource.Error -> {
                    // Fallback to sample data for offline / preview
                    val sampleList = getSampleProfessionals()
                    allProfessionalsList = sampleList
                    applyFilters()
                    _professionals.value = Resource.Success(sampleList)
                }
                is Resource.Loading -> {
                    _professionals.value = Resource.Loading
                }
            }
        }
    }

    fun search(query: String) {
        currentSearchQuery = query.trim()
        applyFilters()
    }

    fun filterBySpecialty(specialty: String) {
        currentSpecialtyFilter = specialty
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = allProfessionalsList

        if (currentSpecialtyFilter.isNotBlank() && currentSpecialtyFilter != "All" && currentSpecialtyFilter != "All Specialties") {
            filtered = filtered.filter { prof ->
                prof.specialty.contains(currentSpecialtyFilter, ignoreCase = true) ||
                prof.title.contains(currentSpecialtyFilter, ignoreCase = true)
            }
        }

        if (currentSearchQuery.isNotBlank()) {
            filtered = filtered.filter { prof ->
                prof.name.contains(currentSearchQuery, ignoreCase = true) ||
                prof.specialty.contains(currentSearchQuery, ignoreCase = true) ||
                prof.bio.contains(currentSearchQuery, ignoreCase = true) ||
                prof.title.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        _filteredProfessionals.value = filtered
    }

    fun loadProfessionalDetail(professionalId: String) {
        viewModelScope.launch {
            _selectedProfessional.value = Resource.Loading
            _availableSlots.value = Resource.Loading

            val existing = allProfessionalsList.find { it.professionalId == professionalId }
            if (existing != null) {
                _selectedProfessional.value = Resource.Success(existing)
            } else {
                _selectedProfessional.value = professionalRepository.getProfessionalById(professionalId)
            }

            // Load slots
            when (val slotRes = professionalRepository.getAvailableSlots(professionalId)) {
                is Resource.Success -> {
                    val slots = if (slotRes.data.isEmpty()) getSampleSlots(professionalId) else slotRes.data
                    _availableSlots.value = Resource.Success(slots)
                }
                else -> {
                    _availableSlots.value = Resource.Success(getSampleSlots(professionalId))
                }
            }
        }
    }

    private fun getSampleProfessionals(): List<Professional> {
        return listOf(
            Professional(
                professionalId = "prof_01",
                name = "Dr. Ananya Sharma",
                title = "Licensed Clinical Psychologist",
                qualifications = "M.Phil Clinical Psychology, RCI Registered",
                specialty = "Anxiety & Stress, Panic Disorders",
                experienceYears = 8,
                bio = "Specializes in Cognitive Behavioral Therapy (CBT), panic intervention, and stress regulation. 8+ years experience guiding individuals through severe anxiety and burnout.",
                languages = listOf("English", "Hindi"),
                rating = 4.9,
                reviewCount = 128,
                isVerified = true,
                consultationFee = 800.0
            ),
            Professional(
                professionalId = "prof_02",
                name = "Dr. Rajesh Varma",
                title = "Senior Consultant Psychiatrist",
                qualifications = "MD Psychiatry, NIMHANS Fellow",
                specialty = "Mood Disorders, Clinical Depression",
                experienceYears = 14,
                bio = "Extensive experience in clinical depression, mood stabilization, and holistic psychiatric consultations with over 14 years of practice across top institutions.",
                languages = listOf("English", "Hindi", "Bengali"),
                rating = 4.8,
                reviewCount = 210,
                isVerified = true,
                consultationFee = 1200.0
            ),
            Professional(
                professionalId = "prof_03",
                name = "Ms. Priya Mukherjee",
                title = "Counseling Psychologist & CBT Specialist",
                qualifications = "M.Sc Counseling Psychology",
                specialty = "Work Stress, Emotional Regulation",
                experienceYears = 6,
                bio = "Compassionate therapist focusing on mindfulness-based cognitive therapy (MBCT), student distress, burnout, and young adult transition support.",
                languages = listOf("English", "Hindi"),
                rating = 4.9,
                reviewCount = 94,
                isVerified = true,
                consultationFee = 600.0
            )
        )
    }

    private fun getSampleSlots(professionalId: String): List<Slot> {
        val now = System.currentTimeMillis()
        val oneHour = 3600 * 1000L
        return listOf(
            Slot(
                slotId = "slot_${professionalId}_1",
                professionalId = professionalId,
                startTime = now + 24 * oneHour, // Tomorrow +0h
                endTime = now + 24 * oneHour + 45 * 60 * 1000L,
                isBooked = false
            ),
            Slot(
                slotId = "slot_${professionalId}_2",
                professionalId = professionalId,
                startTime = now + 28 * oneHour, // Tomorrow +4h
                endTime = now + 28 * oneHour + 45 * 60 * 1000L,
                isBooked = false
            ),
            Slot(
                slotId = "slot_${professionalId}_3",
                professionalId = professionalId,
                startTime = now + 48 * oneHour, // Day after
                endTime = now + 48 * oneHour + 45 * 60 * 1000L,
                isBooked = false
            )
        )
    }
}
