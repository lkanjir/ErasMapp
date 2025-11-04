package com.rampu.erasmapp.auth.domain

import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    val authState: Flow<UserAccount?>
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    suspend fun signOut()
}