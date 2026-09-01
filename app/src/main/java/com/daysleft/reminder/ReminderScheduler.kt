package com.daysleft.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.daysleft.data.local.AppDatabase
import com.daysleft.data.local.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages scheduling and cancelling deterministic exact/inexact alarms via [AlarmManager].
 */
class ReminderScheduler(private val context: Context) {

    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    /**
     * Schedules all active future reminders for the given event.
     * Guaranteed to be idempotent: cancels any previous alarms for this event first.
     */
    fun scheduleEventReminders(event: Event) {
        if (alarmManager == null) {
            Log.w(TAG, "AlarmManager is not available on this device")
            return
        }

        // Always cancel previous alarms for this event ID to prevent duplicate alarms
        cancelEventReminders(event.id)

        if (!event.remindersEnabled) return

        val reminders = ReminderTimeCalculator.calculateReminders(event)

        for (reminder in reminders) {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(ReminderReceiver.EXTRA_EVENT_ID, reminder.eventId)
                putExtra(ReminderReceiver.EXTRA_EVENT_TITLE, reminder.eventTitle)
                putExtra(ReminderReceiver.EXTRA_REMINDER_TYPE_OFFSET, reminder.type.idOffset)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.triggerAtMillis,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                Log.w(TAG, "Exact alarm permission not granted, falling back to inexact alarm", e)
                try {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.triggerAtMillis,
                        pendingIntent
                    )
                } catch (fallbackEx: Exception) {
                    Log.e(TAG, "Failed to schedule fallback reminder alarm", fallbackEx)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error scheduling reminder alarm", e)
            }
        }
    }

    /**
     * Cancels all scheduled reminder alarms for the given event ID.
     */
    fun cancelEventReminders(eventId: Long) {
        if (alarmManager == null) return

        for (type in ReminderType.entries) {
            val requestCode = ReminderTimeCalculator.generateRequestCode(eventId, type)
            val intent = Intent(context, ReminderReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                try {
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to cancel PendingIntent for request code $requestCode", e)
                }
            }
        }
    }

    /**
     * Loads all events from Room and reconciles/reschedules all future alarms.
     * Typically called after reboot, time zone change, or app update.
     */
    suspend fun rescheduleAllEvents() = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getInstance(context)
            val events = database.eventDao().getAllEventsDirect()
            for (event in events) {
                scheduleEventReminders(event)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reconciling event alarms in background", e)
        }
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.canScheduleExactAlarms() == true
        } else {
            true
        }
    }

    companion object {
        private const val TAG = "ReminderScheduler"
    }
}
