package com.rampu.erasmapp.channels.domian

import kotlinx.coroutines.flow.Flow

interface IChannelRepository {
    fun observeChannels(): Flow<ChannelSyncState>
    suspend fun createChannel(
        title: String,
        topic: String,
        description: String? = null
    ) : Result<Unit>
}

sealed interface ChannelSyncState{
    data object Loading: ChannelSyncState
    data class Success(val channels: List<Channel>): ChannelSyncState
    data class Error (val message: String) : ChannelSyncState
    data object SignedOut: ChannelSyncState
}