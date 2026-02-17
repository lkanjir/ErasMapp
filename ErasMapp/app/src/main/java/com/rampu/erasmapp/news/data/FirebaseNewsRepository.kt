package com.rampu.erasmapp.news.data

import com.google.android.libraries.places.api.model.kotlin.authorAttribution
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rampu.erasmapp.news.domain.INewsRepository
import com.rampu.erasmapp.news.domain.NewsItem
import com.rampu.erasmapp.news.domain.NewsSyncState
import com.rampu.erasmapp.user.domain.IUserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseNewsRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepo: IUserRepository
) : INewsRepository {
    override fun observeNews(): Flow<NewsSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null;
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()

            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(NewsSyncState.SignedOut)
                return@AuthStateListener
            }

            trySend(NewsSyncState.Loading)
            registration = firestore.newsFS().addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(NewsSyncState.Error(message = "Unable to load news"))
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { it.toNewsItem() }.orEmpty()
                    .sortedWith(compareByDescending {
                        it.createdAt
                    })
                trySend(NewsSyncState.Success(items))
            }
        }

        auth.addAuthStateListener(authListener)
        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    private fun DocumentSnapshot.toNewsItem(): NewsItem? {
        val id = getString("id") ?: id
        val title = getString("title") ?: return null;
        val body = getString("body") ?: return null;
        val topic = getString("topic") ?: return null
        val authorId = getString("authorId")
        val authorLabel = getString("authorLabel")
        val authorPhotoUrl = getString("authorPhotoUrl")

        return NewsItem(
            id = id,
            title = title,
            body = body,
            topic = topic,
            isUrgent = getBoolean("isUrgent") ?: false,
            createdAt = getLong("createdAt") ?: 0L,
            authorId = authorId,
            authorLabel = authorLabel,
            authorPhotoUrl = authorPhotoUrl
        )
    }

    private fun FirebaseFirestore.newsFS() = collection("news")

    override suspend fun createNews(item: NewsItem): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session.")
        val authorId = item.authorId ?: user.uid
        val authorLabel = item.authorLabel ?: userRepo.getCurrentUserLabel()
        val authorPhotoUrl = item.authorPhotoUrl ?: user.photoUrl?.toString()
        val itemId = item.id.ifBlank { firestore.newsFS().document().id }

        val news = item.copy(
            id = itemId,
            authorId = authorId,
            authorLabel = authorLabel,
            authorPhotoUrl = authorPhotoUrl
        )
        firestore.newsFS().document(itemId).set(news.toFirestoreMap()).await()
    }

    override suspend fun updateNews(item: NewsItem): Result<Unit> = runCatching {
        require(item.id.isNotBlank()) { "News id missing." }
        firestore.newsFS().document(item.id).set(item.toFirestoreMap()).await()
    }

    override suspend fun deleteNews(newsId: String): Result<Unit> = runCatching {
        firestore.newsFS().document(newsId).delete().await()
    }
}

private fun NewsItem.toFirestoreMap() = mapOf(
    "id" to id,
    "title" to title,
    "body" to body,
    "topic" to topic,
    "isUrgent" to isUrgent,
    "createdAt" to createdAt,
    "authorId" to authorId,
    "authorLabel" to authorLabel,
    "authorPhotoUrl" to authorPhotoUrl
)
