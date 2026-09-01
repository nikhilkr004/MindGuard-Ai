package com.mindguard.ai.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
                val listenerRegistration = firestore.collection("users").document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, _ ->
                        val user = if (snapshot != null && snapshot.exists()) {
                            snapshot.toObject(User::class.java) ?: parseUserFromDoc(snapshot, firebaseUser.uid)
                        } else {
                            User(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                displayName = firebaseUser.displayName ?: ""
                            )
                        }
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
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("User creation failed: Null UID")
            val uid = firebaseUser.uid
            
            // 1. Update Firebase Auth displayName
            try {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
                firebaseUser.updateProfile(profileUpdate).await()
            } catch (e: Exception) {
                // Non-fatal
            }

            // 2. Persist comprehensive user profile in Firestore: users/{uid}
            val user = User(
                uid = uid,
                email = email.trim(),
                displayName = name.trim(),
                role = role.name,
                consentAccepted = false,
                consentAcceptedAt = null,
                createdAt = System.currentTimeMillis(),
                photoUrl = null,
                fcmToken = null
            )
            
            firestore.collection("users").document(uid).set(user.toMap(), SetOptions.merge()).await()

            // 3. If role is PROFESSIONAL, persist in Firestore: professionals/{uid}
            if (role == UserRole.PROFESSIONAL) {
                val professionalMap = hashMapOf<String, Any?>(
                    "professionalId" to uid,
                    "name" to name.trim(),
                    "title" to "Licensed Practitioner",
                    "qualifications" to "Credentials Pending Verification",
                    "specialty" to "General Mental Health",
                    "experienceYears" to 0,
                    "bio" to "",
                    "languages" to listOf("English"),
                    "photoUrl" to "",
                    "rating" to 5.0,
                    "reviewCount" to 0,
                    "isVerified" to false,
                    "consultationFee" to 0.0,
                    "createdAt" to System.currentTimeMillis()
                )
                try {
                    firestore.collection("professionals").document(uid).set(professionalMap, SetOptions.merge()).await()
                } catch (e: Exception) {
                    // Non-fatal
                }
            }
            
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Registration error occurred", e)
        }
    }

    override suspend fun loginWithEmail(email: String, pass: String): Resource<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Sign in failed: Null UID")
            val uid = firebaseUser.uid
            
            // Retrieve from Firestore users/{uid} or create if first-time
            val user = try {
                val doc = firestore.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    doc.toObject(User::class.java) ?: parseUserFromDoc(doc, uid)
                } else {
                    val newUser = User(
                        uid = uid,
                        email = firebaseUser.email ?: email.trim(),
                        displayName = firebaseUser.displayName ?: "",
                        role = UserRole.USER.name,
                        consentAccepted = false,
                        createdAt = System.currentTimeMillis()
                    )
                    firestore.collection("users").document(uid).set(newUser.toMap(), SetOptions.merge()).await()
                    newUser
                }
            } catch (e: Exception) {
                User(
                    uid = uid,
                    email = firebaseUser.email ?: email.trim(),
                    displayName = firebaseUser.displayName ?: ""
                )
            }
            
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Invalid email or password", e)
        }
    }

    override suspend fun getCurrentUserProfile(): Resource<User> {
        val firebaseUser = auth.currentUser ?: return Resource.Error("User is not authenticated")
        val uid = firebaseUser.uid
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = if (doc.exists()) {
                doc.toObject(User::class.java) ?: parseUserFromDoc(doc, uid)
            } else {
                User(
                    uid = uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: ""
                )
            }
            Resource.Success(user)
        } catch (e: Exception) {
            val fallback = User(
                uid = uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: ""
            )
            Resource.Success(fallback)
        }
    }

    override suspend fun acceptConsent(): Resource<Unit> {
        val uid = currentUserId ?: return Resource.Error("User is not authenticated")
        return try {
            val consentUpdate = mapOf(
                "consentAccepted" to true,
                "consentAcceptedAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(consentUpdate, SetOptions.merge()).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Success(Unit)
        }
    }

    override suspend fun sendPasswordReset(email: String): Resource<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
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
            Resource.Success(Unit)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    private fun parseUserFromDoc(doc: com.google.firebase.firestore.DocumentSnapshot, uid: String): User {
        return User(
            uid = uid,
            email = doc.getString("email").orEmpty(),
            displayName = doc.getString("displayName").orEmpty(),
            photoUrl = doc.getString("photoUrl"),
            role = doc.getString("role") ?: "USER",
            consentAccepted = doc.getBoolean("consentAccepted") ?: false,
            consentAcceptedAt = doc.getLong("consentAcceptedAt"),
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
            fcmToken = doc.getString("fcmToken")
        )
    }
}
