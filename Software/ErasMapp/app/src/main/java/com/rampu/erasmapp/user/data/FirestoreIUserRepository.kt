package com.rampu.erasmapp.user.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rampu.erasmapp.common.util.emailPrefix
import com.rampu.erasmapp.user.domain.IUserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.IllegalStateException

class FirestoreIUserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IUserRepository {

    private var cachedUid: String? = null
    private var cachedLabel: String? = null

    override fun observeAdminStatus(): Flow<Boolean> = callbackFlow {
        val tokenListener = FirebaseAuth.IdTokenListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                trySend(false)
                return@IdTokenListener
            }

            user.getIdToken(false)
                .addOnSuccessListener { result ->
                    val role = result.claims["role"] as? String
                    trySend(role == "admin")
                }.addOnFailureListener {
                    trySend(false)
                }
        }

        auth.addIdTokenListener(tokenListener)
        awaitClose {
            auth.removeIdTokenListener(tokenListener)
        }
    }

    override suspend fun getCurrentUserLabel(): String {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session.")
        val uid = user.uid
        val cached = cachedLabel
        if (uid == cachedUid && !cached.isNullOrBlank()) return cached

        val profileSnapshot = runCatching {
            firestore.userProfileFS(uid).get().await()
        }.getOrNull()

        val profileName = profileSnapshot?.getString("name")?.trim().orEmpty()
        val displayName = user.displayName?.trim().orEmpty()
        val resolved = when{
            profileName.isNotEmpty() -> profileName
            displayName.isNotEmpty() -> displayName
            else -> emailPrefix(user.email)
        }

        cachedUid = uid
        cachedLabel = resolved
        return resolved
    }
}

private fun FirebaseFirestore.userProfileFS(userId: String) = collection("users").document(userId)
