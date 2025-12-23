package com.rampu.erasmapp

import android.app.Application
import com.rampu.erasmapp.auth.authModule
import com.rampu.erasmapp.channels.channelsModule
import com.rampu.erasmapp.eventCalendar.eventCalendarModule
import com.rampu.erasmapp.schedule.scheduleModule
import com.rampu.erasmapp.session.sessionModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                authModule,
                sessionModule,
                scheduleModule,
                eventCalendarModule,
                channelsModule
            )
        }

    }
}
