package com.mindguard.ai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mindguard.ai.data.model.ChatMessage
import com.mindguard.ai.data.model.ConsultationSession
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

interface ConsultationRepository {
    fun getMessagesFlow(sessionId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(sessionId: String, senderId: String, senderName: String, text: String): Resource<Unit>
    suspend fun getConsultationSession(sessionId: String): Resource<ConsultationSession>
    suspend fun startConsultation(sessionId: String, jioRoomId: String? = null): Resource<Unit>
    suspend fun endConsultation(sessionId: String): Resource<Unit>
}

class ConsultationRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ConsultationRepository {

    override fun getMessagesFlow(sessionId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("consultation_sessions")
            .document(sessionId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(ChatMessage::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(
        sessionId: String,
        senderId: String,
        senderName: String,
        text: String
    ): Resource<Unit> {
        return try {
            val messageId = UUID.randomUUID().toString()
            val message = ChatMessage(
                messageId = messageId,
                sessionId = sessionId,
                senderId = senderId,
                senderName = senderName,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            firestore.collection("consultation_sessions")
                .document(sessionId)
                .collection("messages")
                .document(messageId)
                .set(message)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to send message", e)
        }
    }

    override suspend fun getConsultationSession(sessionId: String): Resource<ConsultationSession> {
        return try {
            val doc = firestore.collection("consultation_sessions").document(sessionId).get().await()
            val session = doc.toObject(ConsultationSession::class.java)
                ?: return Resource.Error("Session not found")
            Resource.Success(session)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to retrieve consultation session", e)
        }
    }

    override suspend fun startConsultation(sessionId: String, jioRoomId: String?): Resource<Unit> {
        return try {
            firestore.collection("consultation_sessions").document(sessionId).update(
                mapOf(
                    "status" to "ACTIVE",
                    "startedAt" to System.currentTimeMillis(),
                    "jioRoomId" to (jioRoomId ?: "jio_room_$sessionId")
                )
            ).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to start consultation session", e)
        }
    }

    override suspend fun endConsultation(sessionId: String): Resource<Unit> {
        return try {
            firestore.collection("consultation_sessions").document(sessionId).update(
                mapOf(
                    "status" to "ENDED",
                    "endedAt" to System.currentTimeMillis()
                )
            ).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to end consultation session", e)
        }
    }
}
