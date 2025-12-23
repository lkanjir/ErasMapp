package com.rampu.erasmapp.channels.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.channels.domian.ChannelSyncState
import com.rampu.erasmapp.channels.domian.IChannelRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseChannelRepository(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) : IChannelRepository {
    override fun observeChannels(): Flow<ChannelSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null;
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(ChannelSyncState.SignedOut)
                return@AuthStateListener
            }

            trySend(ChannelSyncState.Loading)
            registration = firestore.channelFS()
                .orderBy("title", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(ChannelSyncState.Error(message = "Unable to load channels."));
                        return@addSnapshotListener
                    }

                    val channels = snapshot?.documents?.mapNotNull { it.toChannel() }.orEmpty();
                    trySend(ChannelSyncState.Success(channels))
                }
        }

        auth.addAuthStateListener(authListener);
        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener);
        }
    }

    private fun DocumentSnapshot.toChannel(): Channel? {
        val id = getString("id") ?: return null
        val title = getString("title") ?: ""
        val topic = getString("topic") ?: ""
        val description = getString("description") ?: ""
        val createdBy = getString("createdBy") ?: ""

        return Channel(
            id = id,
            title = title,
            topic = topic,
            description = description,
            createdBy = createdBy
        )
    }

    override suspend fun createChannel(
        title: String,
        topic: String,
        description: String?
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session")
        val channelId = firestore.channelFS().document().id;
        val data = mapOf(
            "id"  to channelId,
            "title" to title,
            "topic" to topic,
            "description" to description,
            "createdBy" to user.uid
        )

        firestore.channelFS().document(channelId).set(data).await()
    }

    private fun FirebaseFirestore.channelFS() = collection("channels")

}
