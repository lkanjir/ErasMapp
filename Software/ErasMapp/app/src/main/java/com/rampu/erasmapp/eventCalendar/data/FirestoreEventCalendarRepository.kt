package com.rampu.erasmapp.eventCalendar.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
}

private fun FirebaseFirestore.calendarEventsFS() =
    collection("calendarEvents")

private fun DocumentSnapshot.toCalendarEvent(formatter: DateTimeFormatter): CalendarEvent? {
    val dateText = getString("date") ?: return null
    val parsedDate = runCatching { LocalDate.parse(dateText, formatter) }.getOrNull() ?: return null
    val title = getString("title") ?: return null

    val numericId: Number = getLong("id")
        ?: getDouble("id")?.toLong()
        ?: id.hashCode().toLong()

    return CalendarEvent(
        id = numericId,
        date = parsedDate,
        title = title,
        time = getString("time") ?: "-",
        location = getString("location") ?: "-",
        description = getString("description") ?: "-"
    )
}
