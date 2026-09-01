package com.mindguard.ai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.mindguard.ai.data.model.Appointment
import com.mindguard.ai.data.model.ConsultationMode
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface AppointmentRepository {
    suspend fun bookAppointment(
        professionalId: String,
        slotId: String,
        mode: ConsultationMode,
        notes: String? = null
    ): Resource<String>

    suspend fun cancelAppointment(
        appointmentId: String,
        reason: String? = null
    ): Resource<Unit>

    suspend fun rescheduleAppointment(
        appointmentId: String,
        newSlotId: String
    ): Resource<Unit>

    suspend fun getAppointments(userId: String): Resource<List<Appointment>>
    fun getAppointmentsFlow(userId: String): Flow<List<Appointment>>
}

class AppointmentRepositoryImpl(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AppointmentRepository {

    override suspend fun bookAppointment(
        professionalId: String,
        slotId: String,
        mode: ConsultationMode,
        notes: String?
    ): Resource<String> {
        return try {
            val data = hashMapOf(
                "professionalId" to professionalId,
                "slotId" to slotId,
                "mode" to mode.name,
                "notes" to notes
            )
            val result = functions.getHttpsCallable("createAppointment").call(data).await()
            val resultMap = result.data as? Map<*, *>
            val appointmentId = resultMap?.get("appointmentId") as? String ?: ""
            Resource.Success(appointmentId)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to book appointment", e)
        }
    }

    override suspend fun cancelAppointment(appointmentId: String, reason: String?): Resource<Unit> {
        return try {
            val data = hashMapOf(
                "appointmentId" to appointmentId,
                "reason" to reason
            )
            functions.getHttpsCallable("cancelAppointment").call(data).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to cancel appointment", e)
        }
    }

    override suspend fun rescheduleAppointment(appointmentId: String, newSlotId: String): Resource<Unit> {
        return try {
            val data = hashMapOf(
                "appointmentId" to appointmentId,
                "newSlotId" to newSlotId
            )
            functions.getHttpsCallable("rescheduleAppointment").call(data).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to reschedule appointment", e)
        }
    }

    override suspend fun getAppointments(userId: String): Resource<List<Appointment>> {
        return try {
            val snapshot = firestore.collection("appointments")
                .whereEqualTo("userId", userId)
                .orderBy("scheduledTime", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val appointments = snapshot.toObjects(Appointment::class.java)
            Resource.Success(appointments)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch appointments", e)
        }
    }

    override fun getAppointmentsFlow(userId: String): Flow<List<Appointment>> = callbackFlow {
        val listener = firestore.collection("appointments")
            .whereEqualTo("userId", userId)
            .orderBy("scheduledTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Appointment::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}
