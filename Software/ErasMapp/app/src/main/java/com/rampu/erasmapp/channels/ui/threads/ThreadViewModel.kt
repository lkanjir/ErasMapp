package com.rampu.erasmapp.channels.ui.threads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.channels.domian.AnswerSyncState
import com.rampu.erasmapp.channels.domian.IChannelRepository
import com.rampu.erasmapp.channels.domian.QuestionDetailSyncState
import com.rampu.erasmapp.channels.domian.QuestionStatus
import com.rampu.erasmapp.user.domain.IUserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThreadViewModel(
    private val channelId: String,
    private val channelTitle: String,
    private val questionId: String,
    private val repo: IChannelRepository,
    private val userRepo: IUserRepository
) : ViewModel() {
    var uiState = MutableStateFlow(
        ThreadUiState(
            channelId = channelId,
            channelTitle = channelTitle,
            questionId = questionId
        )
    )
        private set

    private var adminJob: Job? = null
    private var questionJob: Job? = null
    private var answerJob: Job? = null

    init {
        uiState.update { it.copy(currentUserId = repo.currentUserId()) }
        observeAdminStatus()
        observeQuestion()
        observeAnswers()
    }

    private fun observeAdminStatus() {
        adminJob?.cancel()
        adminJob = viewModelScope.launch {
            userRepo.observeAdminStatus()
                .collect { isAdmin -> uiState.update { it.copy(isAdmin = isAdmin) } }
        }
    }

    private fun observeQuestion() {
        questionJob?.cancel()
        questionJob = viewModelScope.launch {
            repo.observeSingleQuestion(channelId, questionId).collect { syncState ->
                when (syncState) {
                    is QuestionDetailSyncState.Loading -> uiState.update {
                        it.copy(
                            isLoading = true,
                            errorMsg = null,
                            canSendAnswer = false
                        )
                    }

                    is QuestionDetailSyncState.Success -> {
                        val question = syncState.question
                        val canSend = canSend(
                            question.status,
                            uiState.value.newAnswer,
                            uiState.value.isSaving
                        )
                        uiState.update {
                            it.copy(
                                question = question,
                                isLoading = false,
                                errorMsg = null,
                                isSignedOut = false,
                                showMessageBox = question.status != QuestionStatus.LOCKED,
                                canSendAnswer = canSend
                            )
                        }
                        viewModelScope.launch {
                            repo.updateQuestionMeta(
                                question.id,
                                question.answerCount
                            )
                        }
                    }

                    is QuestionDetailSyncState.Error -> uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = syncState.message,
                            isSignedOut = false,
                            canSendAnswer = false
                        )
                    }

                    is QuestionDetailSyncState.SignedOut -> uiState.update {
                        it.copy(
                            question = null,
                            isLoading = false,
                            errorMsg = "Sign in to view threads",
                            isSignedOut = true,
                            canSendAnswer = false
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
        val body = uiState.value.newAnswer.trim()

        if (body.isBlank()) {
            uiState.update { it.copy(canSendAnswer = false) }
            return
        }

        if (question.status == QuestionStatus.LOCKED) {
            uiState.update { it.copy(errorMsg = "Thread is locked", canSendAnswer = false) }
            return
        }

        viewModelScope.launch {
            uiState.update { it.copy(isSaving = true, canSendAnswer = false) }
            val result = repo.createAnswer(
                channelId = channelId,
                questionId = questionId,
                body = body
            )

            uiState.update {
                val canSend = canSend(question.status, if (result.isSuccess) "" else body, false)
                if (result.isSuccess) {
                    it.copy(
                        newAnswer = "",
                        isSaving = false,
                        canSendAnswer = canSend,
                        errorMsg = null
                    )
                } else {
                    it.copy(
                        isSaving = false,
                        errorMsg = "Unable to post your answer. Try again.",
                        canSendAnswer = canSend
                    )
                }
            }
            if (result.isSuccess) repo.updateQuestionMeta(question.id, question.answerCount + 1)
        }
    }

    private fun canSend(
        status: QuestionStatus?,
        body: String,
        isSaving: Boolean
    ): Boolean {
        val locked = status == QuestionStatus.LOCKED
        return !body.isBlank() && !locked && !isSaving
    }

    fun onEvent(event: ThreadEvent) {
        when (event) {
            is ThreadEvent.PostAnswer -> createAnswer()
            is ThreadEvent.BodyChanged -> {
                val canSend =
                    canSend(uiState.value.question?.status, event.v, uiState.value.isSaving)
                uiState.update { it.copy(newAnswer = event.v, canSendAnswer = canSend) }
            }

            is ThreadEvent.AcceptAnswer -> acceptAnswer(event.answerId)
            is ThreadEvent.ToggleLock -> toggleLock()
        }
    }

    private fun toggleLock() {
        if (!uiState.value.isAdmin) {
            uiState.update { it.copy(errorMsg = "Only staff can lock or unlock threads") }
            return
        }
        val question = uiState.value.question ?: return
        if (question.status == QuestionStatus.OPEN) return

        val newStatus =
            if (question.status == QuestionStatus.LOCKED) QuestionStatus.ANSWERED else QuestionStatus.LOCKED
        viewModelScope.launch {
            val result = repo.setQuestionStatus(channelId, questionId, newStatus)
            if (result.isFailure) uiState.update { it.copy(errorMsg = "Unable to update thread status") }
            else {
                val msg =
                    if (newStatus == QuestionStatus.LOCKED) "Thread locked" else "Thread unlocked"
                uiState.update { it.copy(toastMsg = msg) }
            }
        }
    }

    private fun acceptAnswer(answerId: String) {
        if (!uiState.value.isAdmin) {
            uiState.update { it.copy(errorMsg = "Only staff can mark answers as accepted") }
            return
        }
        val question = uiState.value.question ?: return
        if (question.status == QuestionStatus.LOCKED) {
            uiState.update { it.copy(errorMsg = "Thread is locked") }
            return
        }

        viewModelScope.launch {
            val result = repo.acceptAnswer(channelId, questionId, answerId)
            if (result.isFailure) uiState.update { it.copy(errorMsg = "Unable to mark answer as accepted") }
        }
    }


}
