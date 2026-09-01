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
    val role: UserRole = UserRole.USER,
    val consentAccepted: Boolean = false,
    val consentAcceptedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val fcmToken: String? = null
)
