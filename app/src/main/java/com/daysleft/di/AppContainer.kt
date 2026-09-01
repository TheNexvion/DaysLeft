package com.daysleft.di

import android.content.Context
import com.daysleft.data.local.AppDatabase
import com.daysleft.data.repository.EventRepository
import com.daysleft.data.repository.EventRepositoryImpl
import com.daysleft.reminder.ReminderScheduler

/**
 * Dependency Injection container interface for the application.
 */
interface AppContainer {
    val database: AppDatabase
    val repository: EventRepository
    val reminderScheduler: ReminderScheduler
}

/**
 * Production implementation of [AppContainer] managing application singletons.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val repository: EventRepository by lazy {
        EventRepositoryImpl(database.eventDao())
    }

    override val reminderScheduler: ReminderScheduler by lazy {
        ReminderScheduler(context)
    }
}
