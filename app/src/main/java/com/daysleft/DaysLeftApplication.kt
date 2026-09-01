package com.daysleft

import android.app.Application
import com.daysleft.di.AppContainer
import com.daysleft.di.DefaultAppContainer
import com.daysleft.reminder.ReminderReceiver

/**
 * Application class initializing global application container and notification channels.
 */
class DaysLeftApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        ReminderReceiver.createNotificationChannel(this)
    }
}
