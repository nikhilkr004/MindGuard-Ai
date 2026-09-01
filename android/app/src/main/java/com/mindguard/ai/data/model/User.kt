package com.mindguard.ai.data.model

enum class UserRole {
    USER,
    PROFESSIONAL,
    ADMIN
}

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val role: String = "USER",
    val consentAccepted: Boolean = false,
    val consentAcceptedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val fcmToken: String? = null
) {
    fun getUserRole(): UserRole = try {
        UserRole.valueOf(role.uppercase())
    } catch (e: Exception) {
        UserRole.USER
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "email" to email,
        "displayName" to displayName,
        "photoUrl" to photoUrl,
        "role" to role,
        "consentAccepted" to consentAccepted,
        "consentAcceptedAt" to consentAcceptedAt,
        "createdAt" to createdAt,
        "fcmToken" to fcmToken
    )
}
