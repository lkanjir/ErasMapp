package com.rampu.erasmapp

import android.app.Application
import android.content.pm.PackageManager
import com.google.android.libraries.places.api.Places
import com.rampu.erasmapp.auth.authModule
import com.rampu.erasmapp.channels.channelsModule
import com.rampu.erasmapp.eventCalendar.eventCalendarModule
import com.rampu.erasmapp.main.homeModule
import com.rampu.erasmapp.news.newsModule
import com.rampu.erasmapp.schedule.scheduleModule
import com.rampu.erasmapp.session.sessionModule
import com.rampu.erasmapp.user.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application(){
    override fun onCreate() {
        super.onCreate()

        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val placesApiKey = appInfo.metaData?.getString("com.google.android.geo.API_KEY").orEmpty()
        if (placesApiKey.isNotBlank() && !Places.isInitialized()) {
            Places.initialize(applicationContext, placesApiKey)
        }

        startKoin {
            androidContext(this@App)
            modules(
                authModule,
                sessionModule,
                userModule,
                scheduleModule,
                homeModule,
                eventCalendarModule,
                channelsModule,
                newsModule
            )
        }

    }
}
