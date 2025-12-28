package com.rampu.erasmapp.user.domain

import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun observeAdminStatus() : Flow<Boolean>
    suspend fun getCurrentUserLabel(): String
}