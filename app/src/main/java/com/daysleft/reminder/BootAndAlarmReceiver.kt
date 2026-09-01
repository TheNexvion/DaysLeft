package com.daysleft.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.daysleft.DaysLeftApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver for reconciling scheduled alarms across system reboots,
 * app package updates, timezone changes, and time modifications.
 */
class BootAndAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            "android.intent.action.TIME_SET",
            Intent.ACTION_DATE_CHANGED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val scheduler = (context.applicationContext as? DaysLeftApplication)?.container?.reminderScheduler
                            ?: ReminderScheduler(context.applicationContext)
                        scheduler.rescheduleAllEvents()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error handling system event $action in BootAndAlarmReceiver", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "BootAndAlarmReceiver"
    }
}
