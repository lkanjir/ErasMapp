package com.rampu.erasmapp.news

import com.rampu.erasmapp.news.data.FirebaseNewsRepository
import com.rampu.erasmapp.news.domain.INewsRepository
import com.rampu.erasmapp.news.ui.NewsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.viewmodel.scope.viewModelScope

val newsModule = module {
    single<INewsRepository> { FirebaseNewsRepository(get(), get()) }
    viewModel { NewsViewModel(get(), get()) }

}