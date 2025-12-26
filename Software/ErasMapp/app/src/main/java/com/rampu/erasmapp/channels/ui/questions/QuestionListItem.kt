package com.rampu.erasmapp.channels.ui.questions

data class QuestionListItem(
    val id: String,
    val title: String,
    val bodyPreview: String,
    val authorLabel: String,
    val authorPhotoUrl: String?,
    val lastActivityAt: Long?,
    val unreadCount: Long
)
