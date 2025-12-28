package com.rampu.erasmapp.channels

import com.rampu.erasmapp.channels.data.FirebaseChannelRepository
import com.rampu.erasmapp.channels.domian.IChannelRepository
import com.rampu.erasmapp.channels.ui.channels.ChannelsViewModel
import com.rampu.erasmapp.channels.ui.questions.QuestionsViewModel
import com.rampu.erasmapp.channels.ui.threads.ThreadViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val channelsModule = module {
    single<IChannelRepository> { FirebaseChannelRepository(get(), get(), get()) }
    viewModel { ChannelsViewModel(get(), get()) }
    viewModel { (channelId: String, channelTitle: String) ->
        QuestionsViewModel(
            channelId,
            channelTitle,
            get()
        )
    }
    viewModel { (channelId: String, channelTitle: String, questionId: String) ->
        ThreadViewModel(
            channelId,
            channelTitle,
            questionId,
            get(),
            get()
        )
    }
}