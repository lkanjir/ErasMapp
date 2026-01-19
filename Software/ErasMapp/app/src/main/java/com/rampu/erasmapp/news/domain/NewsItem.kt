package com.rampu.erasmapp.news.domain

data class NewsItem(
    val id: String,
    val title: String,
    val body: String,
    val topic: String,
    val isUrgent: Boolean,
    val createdAt: Long,
    val authorId: String?,
    val authorLabel: String?,
    val authorPhotoUrl: String?
)
