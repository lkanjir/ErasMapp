package com.rampu.erasmapp.channels.ui.threads

sealed interface ThreadEvent {
    data object PostAnswer: ThreadEvent
    data class BodyChanged(val v : String) : ThreadEvent
    data class AcceptAnswer(val answerId: String) : ThreadEvent
    data object ToggleLock: ThreadEvent
}