package com.rampu.erasmapp.channels.ui.questions

import com.rampu.erasmapp.channels.domian.Question
import com.rampu.erasmapp.channels.ui.channels.ChannelEvent

data class QuestionsUiState (
    val channelId: String,
    val channelTitle: String,
    val questions: List<QuestionListItem> = emptyList(),
    val totalCount: Int = 0,
    val openCount: Int = 0,
    val answeredCount: Int = 0,
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val isSignedOut: Boolean = false,
    val newTitle: String = "",
    val newBody: String = "",
    val resultMsg: String? = null,
    val isSaving: Boolean = false,
    val showCreateDialog: Boolean = false,
    val filter: QuestionFilter = QuestionFilter.OPEN
)
