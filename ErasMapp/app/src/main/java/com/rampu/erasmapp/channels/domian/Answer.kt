package com.rampu.erasmapp.channels.domian

data class Answer(
    val id: String,
    val channelId: String,
    val questionId: String,
    val body: String,
    val authorId: String,
    val authorLabel: String,
    val createdAt: Long,
    val authorPhotoUrl: String? = null
)
