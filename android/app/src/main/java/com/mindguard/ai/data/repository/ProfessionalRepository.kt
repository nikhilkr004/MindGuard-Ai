package com.mindguard.ai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mindguard.ai.data.model.Professional
import com.mindguard.ai.data.model.Slot
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.tasks.await

interface ProfessionalRepository {
    suspend fun getProfessionals(
        specialty: String? = null,
        language: String? = null,
        onlyVerified: Boolean = true
    ): Resource<List<Professional>>
    
    suspend fun getProfessionalById(professionalId: String): Resource<Professional>
    suspend fun getAvailableSlots(professionalId: String): Resource<List<Slot>>
}

class ProfessionalRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ProfessionalRepository {

    override suspend fun getProfessionals(
        specialty: String?,
        language: String?,
        onlyVerified: Boolean
    ): Resource<List<Professional>> {
        return try {
            val snapshot = firestore.collection("professionals")
                .whereEqualTo("isVerified", onlyVerified)
                .get()
                .await()

            var professionals = snapshot.toObjects(Professional::class.java)

            if (!specialty.isNullOrBlank() && specialty != "All") {
                professionals = professionals.filter { it.specialty.contains(specialty, ignoreCase = true) }
            }

            if (!language.isNullOrBlank() && language != "All") {
                professionals = professionals.filter { prof ->
                    prof.languages.any { it.equals(language, ignoreCase = true) }
                }
            }

            Resource.Success(professionals)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch professionals", e)
        }
    }

    override suspend fun getProfessionalById(professionalId: String): Resource<Professional> {
        return try {
            val doc = firestore.collection("professionals").document(professionalId).get().await()
            val prof = doc.toObject(Professional::class.java)
                ?: return Resource.Error("Professional record not found")
            Resource.Success(prof)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to retrieve professional details", e)
        }
    }

    override suspend fun getAvailableSlots(professionalId: String): Resource<List<Slot>> {
        return try {
            val snapshot = firestore.collection("availability")
                .whereEqualTo("professionalId", professionalId)
                .whereEqualTo("isBooked", false)
                .get()
                .await()
            
            val slots = snapshot.toObjects(Slot::class.java)
            Resource.Success(slots)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to retrieve available slots", e)
        }
    }
}
