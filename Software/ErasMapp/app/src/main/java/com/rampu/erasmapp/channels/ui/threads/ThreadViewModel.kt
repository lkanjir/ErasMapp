package com.rampu.erasmapp.channels.ui.threads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.channels.domian.AnswerSyncState
import com.rampu.erasmapp.channels.domian.IChannelRepository
import com.rampu.erasmapp.channels.domian.QuestionDetailSyncState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThreadViewModel(
    private val channelId: String,
    private val channelTitle: String,
    private val questionId: String,
    private val repo: IChannelRepository
) : ViewModel() {
    var uiState = MutableStateFlow(
        ThreadUiState(
            channelId = channelId,
            channelTitle = channelTitle,
            questionId = questionId
        )
    )
        private set

    private var questionJob: Job? = null
    private var answerJob: Job? = null

    init {
        observeQuestion()
        observeAnswers()
    }

    private fun observeQuestion() {
        questionJob?.cancel()
        questionJob = viewModelScope.launch {
            repo.observeSingleQuestion(channelId, questionId).collect { syncState ->
                when (syncState) {
                    is QuestionDetailSyncState.Loading -> uiState.update {
                        it.copy(
                            isLoading = true,
                            errorMsg = null
                        )
                    }

                    is QuestionDetailSyncState.Success -> {
                        uiState.update {
                            it.copy(
                                question = syncState.question,
                                isLoading = false,
                                errorMsg = null,
                                isSignedOut = false
                            )
                        }
                        viewModelScope.launch { repo.updateQuestionMeta(syncState.question.id, syncState.question.answerCount) }
                    }

                    is QuestionDetailSyncState.Error -> uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = syncState.message,
                            isSignedOut = false
                        )
                    }

                    is QuestionDetailSyncState.SignedOut -> uiState.update {
                        it.copy(
                            question = null,
                            isLoading = false,
                            errorMsg = "Sign in to view threads",
                            isSignedOut = true
                        )
                    }
                }
            }
        }
    }

    private fun observeAnswers() {
        answerJob?.cancel()
        answerJob = viewModelScope.launch {
            repo.observeAnswers(channelId, questionId).collect { syncState ->
                when (syncState) {
                    is AnswerSyncState.Loading -> uiState.update {
                        it.copy(
                            isLoading = true,
                            errorMsg = null
                        )
                    }

                    is AnswerSyncState.Success -> uiState.update {
                        it.copy(
                            answers = syncState.answers,
                            isLoading = false,
                            errorMsg = null,
                            isSignedOut = false
                        )
                    }

                    is AnswerSyncState.Error -> uiState.update {
                        it.copy(
                            isLoading = false,
                            isSignedOut = false,
                            errorMsg = syncState.message,
                            answers = emptyList()
                        )
                    }

                    is AnswerSyncState.SignedOut -> uiState.update {
                        it.copy(
                            answers = emptyList(),
                            errorMsg = "Sign in to see the answers",
                            isSignedOut = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun createAnswer() {
        val question = uiState.value.question ?: return

        viewModelScope.launch {
            uiState.update { it.copy(isSaving = true) }
            val result = repo.createAnswer(
                channelId = channelId,
                questionId = questionId,
                body = uiState.value.newAnswer
            )

            uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        newAnswer = "",
                        isSaving = false
                    )
                } else {
                    it.copy(
                        isSaving = false,
                        errorMsg = "Unable to post your answer. Try again."
                    )
                }
            }
        }
    }

    fun onEvent(event: ThreadEvent) {
        when (event) {
            is ThreadEvent.PostAnswer -> {
                createAnswer()
                uiState.update { it.copy(newAnswer = "") }
            }

            is ThreadEvent.BodyChanged -> uiState.update { it.copy(newAnswer = event.v) }
        }
    }
}