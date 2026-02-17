package com.rampu.erasmapp.channels.ui.questions

import com.rampu.erasmapp.channels.domian.QuestionStatus

data class QuestionListItem(
    val id: String,
    val title: String,
    val bodyPreview: String,
    val authorLabel: String,
    val authorPhotoUrl: String?,
    val lastActivityAt: Long?,
    val unreadCount: Long,
    val status: QuestionStatus
)
