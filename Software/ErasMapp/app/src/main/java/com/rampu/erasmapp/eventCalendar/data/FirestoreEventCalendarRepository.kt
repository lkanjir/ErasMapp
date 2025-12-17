package com.rampu.erasmapp.eventCalendar.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.orEmpty

class FirestoreEventCalendarRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : EventCalendarRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun observeEvents(): Flow<EventCalendarSyncState> = callbackFlow {
        var registration: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()

            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(EventCalendarSyncState.SignedOut)
                return@AuthStateListener
            }

            trySend(EventCalendarSyncState.Loading)

            registration = firestore.calendarEventsFS()
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(
                            EventCalendarSyncState.Error(
                                message = "Unable to load calendar events. Check your connection.",
                                throwable = error
                            )
                        )
                        return@addSnapshotListener
                    }

                    val events = snapshot?.documents
                        ?.mapNotNull { it.toCalendarEvent(dateFormatter) }
                        ?.sortedBy { it.date }
                        .orEmpty()

                    trySend(EventCalendarSyncState.Success(events))
                }
        }

        auth.addAuthStateListener(authListener)

        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    override fun observeAdminStatus(): Flow<Boolean> = callbackFlow {
        var registration: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            registration?.remove()

            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(false)
                return@AuthStateListener
            }

            registration = firestore.userProfileFS(currentUser.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(false)
                        return@addSnapshotListener
                    }

                    val isAdmin = snapshot?.getString("role") == "admin"
                    trySend(isAdmin)
                }
        }

        auth.addAuthStateListener(authListener)

        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    override suspend fun createEvent(event: CalendarEvent): Result<Unit> = runCatching {
        auth.currentUser ?: throw IllegalStateException("Missing user session.")
        val finalEvent = if (event.id.isBlank()) {
            event.copy(id = firestore.collection("calendarEvents").document().id)
        } else {
            event
        }
        firestore.calendarEventsFS()
            .document(finalEvent.id)
            .set(finalEvent.toFirestoreMap(dateFormatter))
            .await()
    }
}

private fun FirebaseFirestore.calendarEventsFS() =
    collection("calendarEvents")

private fun FirebaseFirestore.userProfileFS(userId: String) =
    collection("users").document(userId)

private fun DocumentSnapshot.toCalendarEvent(formatter: DateTimeFormatter): CalendarEvent? {
    val dateText = getString("date") ?: return null
    val parsedDate = runCatching { LocalDate.parse(dateText, formatter) }.getOrNull() ?: return null
    val title = getString("title") ?: return null

    val stringId: String = getString("id")
        ?: getLong("id")?.toString()
        ?: getDouble("id")?.toLong()?.toString()
        ?: id

    return CalendarEvent(
        id = stringId,
        date = parsedDate,
        title = title,
        time = getString("time") ?: "-",
        location = getString("location") ?: "-",
        description = getString("description") ?: "-"
    )
}

private fun CalendarEvent.toFirestoreMap(formatter: DateTimeFormatter) = mapOf(
    "id" to id,
    "title" to title,
    "date" to formatter.format(date),
    "time" to time,
    "location" to location,
    "description" to description,
)
