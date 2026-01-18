package com.rampu.erasmapp.news.ui

import com.rampu.erasmapp.news.domain.NewsItem

sealed interface NewsEvent {
    data class ShowEditor(val item: NewsItem?) : NewsEvent
    data object DismissEditor : NewsEvent
    data object SaveNews : NewsEvent
    data class TitleChanged(val v: String) : NewsEvent
    data class TopicChanged(val v: String) : NewsEvent
    data class BodyChanged(val v: String) : NewsEvent
    data class UrgentChanged(val v: Boolean) : NewsEvent
}