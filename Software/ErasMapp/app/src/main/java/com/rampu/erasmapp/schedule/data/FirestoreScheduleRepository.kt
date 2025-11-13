package com.rampu.erasmapp.schedule.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rampu.erasmapp.schedule.domain.ScheduleEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreScheduleRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ScheduleRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun observeEvents(): Flow<ScheduleSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(ScheduleSyncState.SignedOut)
                return@AuthStateListener
            }

            trySend(ScheduleSyncState.Loading)

            registration = firestore.userSchedule(currentUser.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(
                            ScheduleSyncState.Error(
                                message = "Unable to load your schedule. Check your connection.",
                                throwable = error
                            )
                        )
                        return@addSnapshotListener
                    }

                    val events = snapshot?.documents
                        ?.mapNotNull { it.toScheduleEvent(dateFormatter) }
                        ?.sortedBy { it.date }
                        .orEmpty()

                    trySend(ScheduleSyncState.Success(events))
                }
        }

        auth.addAuthStateListener(authListener)

        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    override suspend fun createEvent(event: ScheduleEvent): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session.")
        val finalEvent = if (event.id.isBlank()) event.copy(id = UUID.randomUUID().toString()) else event
        firestore.userSchedule(user.uid)
            .document(finalEvent.id)
            .set(finalEvent.toFirestoreMap(dateFormatter))
            .await()
    }

    override suspend fun updateEvent(event: ScheduleEvent): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session.")
        require(event.id.isNotBlank()) { "Event id is missing." }
        firestore.userSchedule(user.uid)
            .document(event.id)
            .set(event.toFirestoreMap(dateFormatter))
            .await()
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Missing user session.")
        firestore.userSchedule(user.uid)
            .document(eventId)
            .delete()
            .await()
    }
}

private fun FirebaseFirestore.userSchedule(userId: String) =
    collection("users")
        .document(userId)
        .collection("schedule")

private fun ScheduleEvent.toFirestoreMap(formatter: DateTimeFormatter) = mapOf(
    "id" to id,
    "title" to title,
    "date" to formatter.format(date),
    "startTime" to startTime,
    "endTime" to endTime,
    "location" to location,
    "category" to category,
    "isEveryWeek" to isEveryWeek
)

private fun DocumentSnapshot.toScheduleEvent(formatter: DateTimeFormatter): ScheduleEvent? {
    val dateText = getString("date") ?: return null
    val parsedDate = runCatching { LocalDate.parse(dateText, formatter) }.getOrNull() ?: return null
    val title = getString("title") ?: return null

    return ScheduleEvent(
        id = getString("id") ?: id,
        title = title,
        date = parsedDate,
        startTime = getString("startTime") ?: "-",
        endTime = getString("endTime") ?: "-",
        location = getString("location") ?: "-",
        category = getString("category") ?: "Other",
        isEveryWeek = getBoolean("isEveryWeek") ?: false
    )
}
