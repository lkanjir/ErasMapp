package com.rampu.erasmapp.channels.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.rampu.erasmapp.channels.domian.Answer
import com.rampu.erasmapp.channels.domian.AnswerSyncState
import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.channels.domian.ChannelSyncState
import com.rampu.erasmapp.channels.domian.IChannelRepository
import com.rampu.erasmapp.channels.domian.Question
import com.rampu.erasmapp.channels.domian.QuestionDetailSyncState
import com.rampu.erasmapp.channels.domian.QuestionMeta
import com.rampu.erasmapp.channels.domian.QuestionMetaSyncState
import com.rampu.erasmapp.channels.domian.QuestionsSyncState
import com.rampu.erasmapp.common.util.emailPrefix
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseChannelRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IChannelRepository {
    override fun observeChannels(): Flow<ChannelSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null
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
                        trySend(ChannelSyncState.Error(message = "Unable to load channels."))
                        return@addSnapshotListener
                    }

                    val channels = snapshot?.documents?.mapNotNull { it.toChannel() }.orEmpty()
                    trySend(ChannelSyncState.Success(channels))
                }
        }

        auth.addAuthStateListener(authListener)
        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    override fun observeQuestions(channelId: String): Flow<QuestionsSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(QuestionsSyncState.SignedOut)
                return@AuthStateListener
            }

            trySend(QuestionsSyncState.Loading)
            registration =
                firestore.questionsFS(channelId)
                    .orderBy("lastActivityAt", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(QuestionsSyncState.Error(message = "Unable to load questions for selected channel"))
                            return@addSnapshotListener
                        }

                        val questions =
                            snapshot?.documents?.mapNotNull { it.toQuestion(channelId) }.orEmpty()

                        Log.d("QUESTION_SNAPSHOT", questions.toString())
                        trySend(QuestionsSyncState.Success(questions))
                    }
        }

        auth.addAuthStateListener(authListener)
        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    override fun observeSingleQuestion(
        channelId: String,
        questionId: String
    ): Flow<QuestionDetailSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(QuestionDetailSyncState.SignedOut)
                return@AuthStateListener
            }

            trySend(QuestionDetailSyncState.Loading)
            registration = firestore.questionsFS(channelId).document(questionId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(QuestionDetailSyncState.Error("Unable to load data for selected question"))
                        return@addSnapshotListener
                    }

                    val question = snapshot?.toQuestion(channelId)
                    if (question == null) {
                        trySend(QuestionDetailSyncState.Error("Question not found"))
                        return@addSnapshotListener
                    }

                    trySend(QuestionDetailSyncState.Success(question))
                }
        }

        auth.addAuthStateListener(authListener)
        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    override fun observeAnswers(
        channelId: String,
        questionId: String
    ): Flow<AnswerSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(AnswerSyncState.SignedOut)
                return@AuthStateListener
            }

            trySend(AnswerSyncState.Loading)
            registration = firestore.answersFS(channelId, questionId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(AnswerSyncState.Error(message = "Unable to load answers for selected question"))
                        return@addSnapshotListener
                    }

                    val answers =
                        snapshot?.documents?.mapNotNull { it.toAnswer(channelId, questionId) }
                            .orEmpty()
                    trySend(AnswerSyncState.Success(answers))
                }
        }

        auth.addAuthStateListener(authListener)
        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    override fun observerQuestionMeta(): Flow<QuestionMetaSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(QuestionMetaSyncState.SignedOut)
                return@AuthStateListener
            }

            trySend(QuestionMetaSyncState.Loading)
            registration = firestore.userQuestionMetaFS(currentUser.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(QuestionMetaSyncState.Error("Unable to load question meta information"))
                        return@addSnapshotListener
                    }

                    val meta = snapshot?.documents?.mapNotNull { it.toQuestionMeta() }.orEmpty()
                    trySend(QuestionMetaSyncState.Success(meta))
                }
        }

        auth.addAuthStateListener(authListener)
        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    override fun currentUserId(): String? = auth.currentUser?.uid

    private fun DocumentSnapshot.toQuestionMeta(): QuestionMeta? {
        val questionId = getString("questionId") ?: id
        val lastSeenAnswerCount = getLong("lastSeenAnswerCount") ?: 0L
        val lastSeenAt = getLong("lastSeenAt") ?: 0L
        return QuestionMeta(
            questionId = questionId,
            lastSeenAnswerCount = lastSeenAnswerCount,
            lastSeenAt = lastSeenAt
        )

    }

    private fun FirebaseFirestore.userQuestionMetaFS(userId: String) =
        collection("users").document(userId).collection("questionMeta")

    private fun DocumentSnapshot.toAnswer(channelId: String, questionId: String): Answer? {
        val id = getString("id") ?: return null
        val body = getString("body") ?: return null
        val authorId = getString("authorId") ?: ""
        val authorLabel = getString("authorLabel") ?: authorId
        val createdAt = getLong("createdAt") ?: 0L

        return Answer(
            id = id,
            channelId = channelId,
            questionId = questionId,
            body = body,
            authorId = authorId,
            authorLabel = authorLabel,
            createdAt = createdAt
        )
    }

    private fun FirebaseFirestore.answersFS(channelId: String, questionId: String) =
        questionsFS(channelId).document(questionId).collection("answers")

    private fun DocumentSnapshot.toQuestion(channelId: String): Question? {
        val questionId = getString("id") ?: id
        val title = getString("title") ?: return null
        val body = getString("body") ?: ""
        val authorId = getString("authorId") ?: ""
        val authorLabel = getString("authorLabel") ?: ""
        val authorPhotoUrl = getString("authorPhotoUrl")
        val createdAt = getLong("createdAt") ?: 0L
        val lastActivityAt = getLong("lastActivityAt") ?: createdAt
        val lastMessagePreview = getString("lastMessagePreview") ?: ""
        val answerCount = getLong("answerCount")?: 0L

        return Question(
            id = questionId,
            channelId = channelId,
            title = title,
            body = body,
            authorId = authorId,
            authorLabel = authorLabel,
            authorPhotoUrl = authorPhotoUrl,
            createdAt = createdAt,
            lastActivityAt = lastActivityAt,
            lastMessagePreview = lastMessagePreview,
            answerCount = answerCount
        )
    }

    private fun FirebaseFirestore.questionsFS(channelId: String) =
        channelFS().document(channelId).collection("questions")

    private fun DocumentSnapshot.toChannel(): Channel? {
        val channelId = getString("id") ?: id
        val title = getString("title") ?: ""
        val topic = getString("topic") ?: ""
        val description = getString("description") ?: ""
        val createdBy = getString("createdBy") ?: ""

        return Channel(
            id = channelId,
            title = title,
            topic = topic,
            description = description,
            createdBy = createdBy,
            //TODO: make this work with Firestore
            iconKey = null,
        )
    }

    override suspend fun createChannel(
        title: String,
        topic: String,
        description: String?
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session")
        val channelId = firestore.channelFS().document().id
        val data = mapOf(
            "id" to channelId,
            "title" to title,
            "topic" to topic,
            "description" to description,
            "createdBy" to user.uid
        )

        firestore.channelFS().document(channelId).set(data).await()
    }

    override suspend fun createQuestion(
        channelId: String,
        title: String,
        body: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session")
        val questionId = firestore.questionsFS(channelId).document().id
        val createdAt = System.currentTimeMillis()
        val authorLabel = emailPrefix(user.email)
        val authorPhotoUrl = user.photoUrl?.toString()
        val preview = body.take(100).ifBlank { title }
        val data = mapOf(
            "id" to questionId,
            "channelId" to channelId,
            "title" to title,
            "body" to body,
            "authorId" to user.uid,
            "authorLabel" to authorLabel,
            "authorPhotoUrl" to authorPhotoUrl,
            "createdAt" to createdAt,
            "lastActivityAt" to createdAt,
            "lastMessagePreview" to preview,
            "answerCount" to 0L
        )

        firestore.questionsFS(channelId).document(questionId).set(data).await()
    }

    //TODO: need to change when i add post registration flow
    override suspend fun createAnswer(
        channelId: String,
        questionId: String,
        body: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session")
        val answerId = firestore.answersFS(channelId, questionId).document().id
        val createdAt = System.currentTimeMillis()
        val authorLabel = emailPrefix(user.email)

        val data = mapOf(
            "id" to answerId,
            "channelId" to channelId,
            "questionId" to questionId,
            "body" to body,
            "authorId" to user.uid,
            "authorLabel" to authorLabel,
            "createdAt" to createdAt
        )

        firestore.answersFS(channelId, questionId).document(answerId).set(data).await()
    }

    override suspend fun updateQuestionMeta(
        questionId: String,
        lastSeenAnswerCount: Long
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session")
        val data = mapOf(
            "questionId" to questionId,
            "lastSeenAnswerCount" to lastSeenAnswerCount,
            "lastSeenAt" to System.currentTimeMillis()
        )

        firestore.userQuestionMetaFS(user.uid).document(questionId).set(data, SetOptions.merge())
            .await()
    }

    private fun FirebaseFirestore.channelFS() = collection("channels")
}
