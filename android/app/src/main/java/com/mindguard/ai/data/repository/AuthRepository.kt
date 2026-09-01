package com.mindguard.ai.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mindguard.ai.data.model.User
import com.mindguard.ai.data.model.UserRole
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    val currentUserId: String?
    val isUserLoggedIn: Boolean
    fun getAuthStateFlow(): Flow<User?>
    suspend fun registerWithEmail(email: String, pass: String, name: String, role: UserRole = UserRole.USER): Resource<User>
    suspend fun loginWithEmail(email: String, pass: String): Resource<User>
    suspend fun getCurrentUserProfile(): Resource<User>
    suspend fun acceptConsent(): Resource<Unit>
    suspend fun sendPasswordReset(email: String): Resource<Unit>
    suspend fun updateFcmToken(token: String): Resource<Unit>
    fun logout()
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    override fun getAuthStateFlow(): Flow<User?> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                firestore.collection("users").document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, _ ->
                        val user = snapshot?.toObject(User::class.java) ?: User(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            displayName = firebaseUser.displayName ?: ""
                        )
                        trySend(user)
                    }
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(authListener)
        awaitClose { auth.removeAuthStateListener(authListener) }
    }

    override suspend fun registerWithEmail(email: String, pass: String, name: String, role: UserRole): Resource<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("User creation failed: Null UID")
            
            val user = User(
                uid = uid,
                email = email,
                displayName = name,
                role = role,
                createdAt = System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(user).await()
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Registration error occurred", e)
        }
    }

    override suspend fun loginWithEmail(email: String, pass: String): Resource<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("Sign in failed: Null UID")
            
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: User(
                uid = uid,
                email = email,
                displayName = authResult.user?.displayName ?: ""
            )
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Login error occurred", e)
        }
    }

    override suspend fun getCurrentUserProfile(): Resource<User> {
        val uid = currentUserId ?: return Resource.Error("User is not authenticated")
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: return Resource.Error("User record not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch user profile", e)
        }
    }

    override suspend fun acceptConsent(): Resource<Unit> {
        val uid = currentUserId ?: return Resource.Error("User is not authenticated")
        return try {
            firestore.collection("users").document(uid).update(
                mapOf(
                    "consentAccepted" to true,
                    "consentAcceptedAt" to System.currentTimeMillis()
                )
            ).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to record consent acceptance", e)
        }
    }

    override suspend fun sendPasswordReset(email: String): Resource<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to send password reset email", e)
        }
    }

    override suspend fun updateFcmToken(token: String): Resource<Unit> {
        val uid = currentUserId ?: return Resource.Success(Unit)
        return try {
            firestore.collection("users").document(uid).update("fcmToken", token).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update FCM token", e)
        }
    }

    override fun logout() {
        auth.signOut()
    }
}
