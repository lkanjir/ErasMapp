package com.rampu.erasmapp.news.ui

import com.rampu.erasmapp.news.domain.NewsItem

data class NewsUiState(
    val news: List<NewsItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val isSignedOut: Boolean = false,
    val isSaving: Boolean = false,
    val isAdmin: Boolean = false,
    val actionError: String? = null,
    val editId: String? = null,
    val editTitle: String = "",
    val editBody: String = "",
    val editTopic: String = "",
    val editUrgent: Boolean = false,
    val editorError: String? = null,
    val editCreatedAt: Long = 0L,
    val editAuthorId: String? = null,
    val editAuthorLabel: String? = null,
    val editAuthorPhotoUrl: String? = null,
    val showEditor: Boolean = false
)
