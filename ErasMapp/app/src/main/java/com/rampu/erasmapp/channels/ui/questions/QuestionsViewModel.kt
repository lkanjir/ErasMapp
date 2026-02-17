package com.rampu.erasmapp.channels.ui.questions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.channels.domian.IChannelRepository
import com.rampu.erasmapp.channels.domian.QuestionMetaSyncState
import com.rampu.erasmapp.channels.domian.QuestionStatus
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
    private var didAutoSwitchFilter = false
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
                            totalCount = 0,
                            openCount = 0,
                            answeredCount = 0,
                            filter = filter
                        )
                    }

                    is QuestionsSyncState.Success -> {
                        val metaMap = when (metaState) {
                            is QuestionMetaSyncState.Success -> metaState.meta.associateBy { it.questionId }
                            else -> emptyMap()
                        }

                        val userId = repo.currentUserId()

                        val totalCount = questionState.questions.size
                        val openCount = questionState.questions.count { it.status == QuestionStatus.OPEN }
                        val answeredCount = questionState.questions.count { it.status != QuestionStatus.OPEN }
                        val shouldAutoSwitch =
                            !didAutoSwitchFilter &&
                                filter == QuestionFilter.OPEN &&
                                openCount == 0 &&
                                answeredCount > 0
                        val effectiveFilter = if (shouldAutoSwitch) QuestionFilter.ANSWERED else filter

                        if (shouldAutoSwitch) {
                            didAutoSwitchFilter = true
                            filterState.value = QuestionFilter.ANSWERED
                        }

                        val filteredQuestions = questionState.questions.filter { q ->
                            effectiveFilter.matches(q.status)
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
                                totalCount = totalCount,
                                openCount = openCount,
                                answeredCount = answeredCount,
                                filter = effectiveFilter
                            )
                        }

                        Log.d("UI_STATE_ITEMS", items.count().toString())
                    }

                    is QuestionsSyncState.Error -> uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = questionState.message,
                            totalCount = 0,
                            openCount = 0,
                            answeredCount = 0,
                            filter = filter
                        )
                    }

                    is QuestionsSyncState.SignedOut -> uiState.update {
                        it.copy(
                            questions = emptyList(),
                            isLoading = false,
                            errorMsg = "You need to sign in to view messages",
                            isSignedOut = true,
                            totalCount = 0,
                            openCount = 0,
                            answeredCount = 0,
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
