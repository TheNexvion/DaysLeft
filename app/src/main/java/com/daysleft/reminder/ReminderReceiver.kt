package com.daysleft.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.daysleft.MainActivity
import com.daysleft.R

/**
 * BroadcastReceiver triggered by AlarmManager to post reminder notifications.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        val eventTitle = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: run {
            Log.w(TAG, "Received reminder intent missing event title")
            return
        }
        val typeOffset = intent.getIntExtra(EXTRA_REMINDER_TYPE_OFFSET, -1)
        val reminderType = ReminderType.fromIdOffset(typeOffset) ?: run {
            Log.w(TAG, "Received reminder intent with unknown type offset: $typeOffset")
            return
        }

        if (eventId <= 0) {
            Log.w(TAG, "Received reminder intent with invalid event ID: $eventId")
            return
        }

        createNotificationChannel(context)

        val notificationId = ReminderTimeCalculator.generateRequestCode(eventId, reminderType)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(MainActivity.EXTRA_EVENT_ID, eventId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_splash_icon)
            .setColor(0xFF2D52C8.toInt()) // Brand primary color
            .setContentTitle(reminderType.getNotificationTitle())
            .setContentText(reminderType.getNotificationBody(eventTitle))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(reminderType.getNotificationBody(eventTitle))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "Notification permission POST_NOTIFICATIONS is not granted")
                return
            }
        }

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException while displaying notification", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error displaying reminder notification", e)
        }
    }

    companion object {
        private const val TAG = "ReminderReceiver"

        const val CHANNEL_ID = "days_left_reminders"
        const val CHANNEL_NAME = "Days Left Reminders"
        const val CHANNEL_DESCRIPTION = "Notifications for your upcoming countdowns"

        const val EXTRA_EVENT_ID = "com.daysleft.reminder.EXTRA_EVENT_ID"
        const val EXTRA_EVENT_TITLE = "com.daysleft.reminder.EXTRA_EVENT_TITLE"
        const val EXTRA_REMINDER_TYPE_OFFSET = "com.daysleft.reminder.EXTRA_REMINDER_TYPE_OFFSET"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANNEL_DESCRIPTION
                    enableLights(true)
                    lightColor = 0xFF2D52C8.toInt()
                }

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.createNotificationChannel(channel)
            }
        }
    }
}
