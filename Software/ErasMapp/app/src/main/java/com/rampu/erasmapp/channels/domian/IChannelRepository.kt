package com.rampu.erasmapp.channels.domian

import kotlinx.coroutines.flow.Flow

interface IChannelRepository {
    fun observeChannels(): Flow<ChannelSyncState>
    fun observeQuestions(channelId: String): Flow<QuestionsSyncState>
    fun observeSingleQuestion(channelId: String, questionId: String): Flow<QuestionDetailSyncState>
    fun observeAnswers(channelId: String, questionId: String): Flow<AnswerSyncState>
    fun observerQuestionMeta(): Flow<QuestionMetaSyncState>
    fun currentUserId(): String?
    suspend fun createChannel(
        title: String,
        topic: String,
        description: String? = null,
        iconKey: String? = null
    ): Result<Unit>

    suspend fun createQuestion(
        channelId: String,
        title: String,
        body: String
    ): Result<Unit>

    suspend fun createAnswer(
        channelId: String,
        questionId: String,
        body: String
    ): Result<Unit>

    suspend fun acceptAnswer(channelId: String, questionId: String, answerId: String): Result<Unit>

    suspend fun setQuestionStatus(
        channelId: String,
        questionsId: String,
        status: QuestionStatus
    ): Result<Unit>

    suspend fun updateQuestionMeta(questionId: String, lastSeenAnswerCount: Long): Result<Unit>
}

sealed interface ChannelSyncState {
    data object Loading : ChannelSyncState
    data class Success(val channels: List<Channel>) : ChannelSyncState
    data class Error(val message: String) : ChannelSyncState
    data object SignedOut : ChannelSyncState
}

sealed interface QuestionsSyncState {
    data object Loading : QuestionsSyncState
    data class Success(val questions: List<Question>) : QuestionsSyncState
    data class Error(val message: String) : QuestionsSyncState
    data object SignedOut : QuestionsSyncState
}

sealed interface QuestionDetailSyncState {
    data object Loading : QuestionDetailSyncState
    data class Success(val question: Question) : QuestionDetailSyncState
    data class Error(val message: String) : QuestionDetailSyncState
    data object SignedOut : QuestionDetailSyncState
}

sealed interface AnswerSyncState {
    data object Loading : AnswerSyncState
    data class Success(val answers: List<Answer>) : AnswerSyncState
    data class Error(val message: String) : AnswerSyncState
    data object SignedOut : AnswerSyncState
}

sealed interface QuestionMetaSyncState {
    data object Loading : QuestionMetaSyncState
    data class Success(val meta: List<QuestionMeta>) : QuestionMetaSyncState
    data class Error(val message: String) : QuestionMetaSyncState
    data object SignedOut : QuestionMetaSyncState
}