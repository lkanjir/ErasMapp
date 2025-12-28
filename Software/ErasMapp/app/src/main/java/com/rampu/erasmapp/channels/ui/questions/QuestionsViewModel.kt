package com.rampu.erasmapp.channels.ui.questions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.channels.domian.IChannelRepository
import com.rampu.erasmapp.channels.domian.QuestionMetaSyncState
import com.rampu.erasmapp.channels.domian.QuestionsSyncState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuestionsViewModel(
    private val channelId: String,
    private val channelTitle: String,
    private val repo: IChannelRepository
) : ViewModel() {
    private val filterState = MutableStateFlow(QuestionFilter.OPEN)
    var uiState =
        MutableStateFlow(
            QuestionsUiState(
                channelId = channelId,
                channelTitle = channelTitle,
                filter = filterState.value
            )
        )
        private set
    private var observeJob: Job? = null

    init {
        observeQuestions()
    }

    private fun observeQuestions() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                repo.observeQuestions(channelId),
                repo.observerQuestionMeta(),
                filterState
            ) { questionState, metaState, filter ->
                Triple(
                    questionState,
                    metaState,
                    filter
                )
            }.collect { (questionState, metaState, filter) ->
                when (questionState) {
                    is QuestionsSyncState.Loading -> uiState.update {
                        it.copy(
                            isLoading = true,
                            errorMsg = null,
                            filter = filter
                        )
                    }

                    is QuestionsSyncState.Success -> {
                        val metaMap = when (metaState) {
                            is QuestionMetaSyncState.Success -> metaState.meta.associateBy { it.questionId }
                            else -> emptyMap()
                        }

                        val userId = repo.currentUserId()

                        val filteredQuestions = questionState.questions.filter { q ->
                            filter.matches(q.status)
                        }
                        Log.d("UI_STATE_ITEMS", "Before map: ${filteredQuestions.count()}")

                        val items = filteredQuestions.map { q ->
                            val meta = metaMap[q.id]
                            val lastSeen = meta?.lastSeenAnswerCount ?: 0L
                            val isEngaged = userId != null && (q.authorId == userId || meta != null)
                            val unread =
                                if (isEngaged) (q.answerCount - lastSeen).coerceAtLeast(0L) else 0
                            val preview = q.lastMessagePreview.ifBlank { q.body }

                            QuestionListItem(
                                id = q.id,
                                title = q.title,
                                bodyPreview = preview,
                                authorLabel = q.authorLabel,
                                authorPhotoUrl = q.authorPhotoUrl,
                                lastActivityAt = q.lastActivityAt,
                                unreadCount = unread,
                                status = q.status
                            )
                        }

                        uiState.update {
                            it.copy(
                                questions = items,
                                isLoading = false,
                                errorMsg = null,
                                isSignedOut = false,
                                filter = filter
                            )
                        }

                        Log.d("UI_STATE_ITEMS", items.count().toString())
                    }

                    is QuestionsSyncState.Error -> uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = questionState.message,
                            filter = filter
                        )
                    }

                    is QuestionsSyncState.SignedOut -> uiState.update {
                        it.copy(
                            questions = emptyList(),
                            isLoading = false,
                            errorMsg = "You need to sign in to view messages",
                            isSignedOut = true,
                            filter = filter
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: QuestionsEvent) {
        when (event) {
            is QuestionsEvent.TitleChanged -> uiState.update { it.copy(newTitle = event.v) }
            is QuestionsEvent.BodyChanged -> uiState.update { it.copy(newBody = event.v) }
            is QuestionsEvent.CreateQuestion -> {
                createQuestion()
                uiState.update { it.copy(showCreateDialog = false) }
            }

            is QuestionsEvent.ShowCreateDialog -> uiState.update { it.copy(showCreateDialog = event.show) }
            is QuestionsEvent.FilterChanged -> {
                filterState.value = event.filter
                uiState.update { it.copy(filter = event.filter) }
            }
        }
    }

    fun createQuestion() {
        if (uiState.value.newTitle.isBlank()) {
            uiState.update { it.copy(errorMsg = "Title is required") }
            return
        }

        viewModelScope.launch {
            val result = repo.createQuestion(
                channelId = channelId,
                title = uiState.value.newTitle,
                body = uiState.value.newBody
            )
            uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        newTitle = "",
                        newBody = "",
                        errorMsg = "",
                        isSaving = false,
                        resultMsg = "Question posted"
                    )
                } else {
                    it.copy(
                        isSaving = false,
                        resultMsg = "Posting failed. Try again."
                    )
                }
            }
        }
    }

}