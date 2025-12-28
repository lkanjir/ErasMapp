package com.rampu.erasmapp.user

import com.rampu.erasmapp.user.data.FirestoreIUserRepository
import com.rampu.erasmapp.user.domain.IUserRepository
import org.koin.dsl.module

val userModule = module { single<IUserRepository> { FirestoreIUserRepository(get(), get()) } }