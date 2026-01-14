package com.rampu.erasmapp.main

import kotlinx.serialization.Serializable

@Serializable object HomeRoute
@Serializable object ScheduleRoute
@Serializable object EventCalendarRoute
@Serializable object MapRoute
@Serializable object ProfileRoute
@Serializable object AdminRoute
@Serializable object AdminEventsRoute
@Serializable object AdminRoomsRoute
@Serializable object AdminNewsRoute
@Serializable object ChannelsRoute
@Serializable object NavigationRoute
@Serializable object FOIRoute
@Serializable data class QuestionsRoute(val channelId: String, val channelTitle: String)
@Serializable data class ThreadRoute(val channelId: String, val channelTitle: String, val questionId: String)