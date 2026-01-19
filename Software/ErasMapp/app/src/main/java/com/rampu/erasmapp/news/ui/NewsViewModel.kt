package com.rampu.erasmapp.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.news.domain.INewsRepository
import com.rampu.erasmapp.news.domain.NewsItem
import com.rampu.erasmapp.news.domain.NewsSyncState
import com.rampu.erasmapp.user.domain.IUserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsViewModel(private val repo: INewsRepository, private val userRepo: IUserRepository) :
    ViewModel() {
    var uiState = MutableStateFlow(NewsUiState())
        private set
    val defaultCategory = "general"

    private var observeJob: Job? = null
    private var adminJob: Job? = null

    init {
        observeNews()
        observeAdminStatus()
    }

    private fun observeAdminStatus() {
        adminJob?.cancel()
        adminJob = viewModelScope.launch {
            userRepo.observeAdminStatus().collect { isAdmin ->
                uiState.update {
                    it.copy(
                        isAdmin = isAdmin,
                        showEditor = if (isAdmin) it.showEditor else false
                    )
                }
            }
        }
    }

    private fun observeNews() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repo.observeNews().collect { state ->
                when (state) {
                    is NewsSyncState.Loading -> uiState.update {
                        it.copy(
                            isLoading = true,
                            errorMsg = null
                        )
                    }

                    is NewsSyncState.Success -> {
                        uiState.update {
                            it.copy(
                                news = state.news,
                                isLoading = false,
                                errorMsg = null,
                                isSignedOut = false
                            )
                        }
                    }

                    is NewsSyncState.Error -> uiState.update {
                        it.copy(
                            isLoading = false,
                            isSignedOut = false,
                            errorMsg = state.message
                        )
                    }

                    is NewsSyncState.SignedOut -> uiState.update {
                        it.copy(
                            news = emptyList(),
                            isLoading = false,
                            errorMsg = "You need to sign in to view the news",
                            isSignedOut = true
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: NewsEvent) {
        when (event) {
            is NewsEvent.TitleChanged -> uiState.update {
                it.copy(
                    editTitle = event.v,
                    editorError = null
                )
            }

            is NewsEvent.BodyChanged -> uiState.update {
                it.copy(
                    editBody = event.v,
                    editorError = null
                )
            }

            is NewsEvent.TopicChanged -> uiState.update {
                it.copy(
                    editTopic = event.v,
                    editorError = null
                )
            }

            is NewsEvent.UrgentChanged -> uiState.update { it.copy(editUrgent = event.v) }
            is NewsEvent.ShowEditor -> openEditor(event.item)
            is NewsEvent.DismissEditor -> dismissEditor()
            is NewsEvent.SaveNews -> saveNews()
            is NewsEvent.DeleteNews -> deleteNews(event.newsId)
        }
    }

    private fun deleteNews(newsId: String) {
        if (!uiState.value.isAdmin) {
            uiState.update { it.copy(actionError = "Only staff can manage news") }
            return
        }
        if (newsId.isBlank()) return

        viewModelScope.launch {
            uiState.update { it.copy(isSaving = true) }
            val result = repo.deleteNews(newsId)
            uiState.update {
                if (result.isSuccess) it.copy(isSaving = false, actionError = null) else it.copy(
                    isSaving = false,
                    actionError = "Error when deleting news"
                )
            }

        }
    }

    private fun saveNews() {
        val state = uiState.value
        if (!state.isAdmin) {
            uiState.update { it.copy(actionError = "Only staff can manage news") }
            return
        }

        val title = state.editTitle.trim()
        val topic = state.editTopic.trim()
        val body = state.editBody.trim()

        if (title.isBlank() || body.isBlank() || topic.isBlank()) {
            uiState.update { it.copy(editorError = "Title, topic, and body are required") }
            return
        }

        val createdAt =
            if (state.editId.isNullOrBlank()) System.currentTimeMillis() else state.editCreatedAt
        val authorId = if (state.editId.isNullOrBlank()) null else state.editAuthorId

        val item = NewsItem(
            id = state.editId.orEmpty(),
            title = title,
            body = body,
            topic = topic,
            isUrgent = state.editUrgent,
            createdAt = createdAt,
            authorId = authorId,
            authorLabel = state.editAuthorLabel,
            authorPhotoUrl = state.editAuthorPhotoUrl
        )

        viewModelScope.launch {
            uiState.update { it.copy(isSaving = true, editorError = null) }
            val result =
                if (state.editId.isNullOrBlank()) repo.createNews(item) else repo.updateNews(item)

            uiState.update {
                if (result.isSuccess) it.resetEditor() else it.copy(
                    isSaving = false,
                    editorError = "Error when saving news."
                )
            }
        }
    }

    private fun dismissEditor() {
        uiState.update { it.resetEditor() }
    }

    private fun openEditor(item: NewsItem?) {
        if (!uiState.value.isAdmin) {
            uiState.update { it.copy(actionError = "Only staff can manage news") }
            return
        }

        uiState.update {
            if (item == null) {
                it.copy(
                    showEditor = true,
                    editId = null,
                    editTitle = "",
                    editTopic = defaultCategory,
                    editBody = "",
                    editUrgent = false,
                    editCreatedAt = 0L,
                    editAuthorId = null,
                    editAuthorLabel = null,
                    editAuthorPhotoUrl = null,
                    editorError = null,
                )
            } else {
                it.copy(
                    showEditor = true,
                    editId = item.id,
                    editTitle = item.title,
                    editBody = item.body,
                    editTopic = item.topic,
                    editUrgent = item.isUrgent,
                    editCreatedAt = item.createdAt,
                    editAuthorId = item.authorId,
                    editAuthorLabel = item.authorLabel,
                    editAuthorPhotoUrl = item.authorPhotoUrl,
                    editorError = null
                )
            }
        }
    }
}

private fun NewsUiState.resetEditor(): NewsUiState = copy(
    showEditor = false,
    editId = null,
    editTitle = "",
    editTopic = "",
    editBody = "",
    editUrgent = false,
    editCreatedAt = 0L,
    editAuthorId = null,
    editAuthorLabel = null,
    editAuthorPhotoUrl = null,
    isSaving = false,
    editorError = null
)
