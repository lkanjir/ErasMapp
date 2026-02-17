package com.rampu.erasmapp.channels.ui.threads

import com.rampu.erasmapp.channels.domian.Answer
import com.rampu.erasmapp.channels.domian.Question

data class ThreadUiState(
    val channelId: String,
    val channelTitle: String,
    val questionId: String,
    val question: Question? = null,
    val answers: List<Answer> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val isSignedOut: Boolean = false,
    val isAdmin: Boolean = false,
    val isSaving: Boolean = false,
    val newAnswer: String = "",
    val currentUserId: String? = null,
    val canSendAnswer: Boolean = false,
    val showMessageBox: Boolean = true,
    val toastMsg: String? = null
)
