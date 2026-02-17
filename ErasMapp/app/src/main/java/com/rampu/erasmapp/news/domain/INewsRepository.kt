package com.rampu.erasmapp.news.domain

import kotlinx.coroutines.flow.Flow

interface INewsRepository {
    fun observeNews(): Flow<NewsSyncState>
    suspend fun createNews(item: NewsItem): Result<Unit>
    suspend fun updateNews(item: NewsItem): Result<Unit>
    suspend fun deleteNews(newsId: String): Result<Unit>
}

sealed interface NewsSyncState {
    data object Loading : NewsSyncState
    data class Success(val news: List<NewsItem>) : NewsSyncState
    data class Error(val message: String) : NewsSyncState
    data object SignedOut : NewsSyncState
}