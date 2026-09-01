package com.daysleft.reminder

import com.daysleft.data.local.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class ScheduledReminder(
    val eventId: Long,
    val eventTitle: String,
    val type: ReminderType,
    val triggerAtMillis: Long,
    val requestCode: Int
)

object ReminderTimeCalculator {

    /**
     * Deterministic unique alarm request code derived from event ID and reminder type.
     * Guaranteed unique per event and reminder interval.
     */
    fun generateRequestCode(eventId: Long, type: ReminderType): Int {
        return (eventId * 10 + type.idOffset).toInt()
    }

    /**
     * Calculates all active future reminders for an event using calendar-aware date/time calculations.
     * Skips any reminders that have already passed in time.
     */
    fun calculateReminders(
        event: Event,
        nowMillis: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): List<ScheduledReminder> {
        if (!event.remindersEnabled) return emptyList()

        val reminders = mutableListOf<ScheduledReminder>()

        val reminderTypesToCheck = buildList {
            if (event.remindSevenDaysBefore) add(ReminderType.SEVEN_DAYS_BEFORE)
            if (event.remindOneDayBefore) add(ReminderType.ONE_DAY_BEFORE)
            if (event.remindOnDay) add(ReminderType.ON_THE_DAY)
        }

        val reminderTime = try {
            LocalTime.of(event.reminderHour, event.reminderMinute)
        } catch (_: Exception) {
            LocalTime.of(9, 0)
        }

        for (type in reminderTypesToCheck) {
            val targetDate = event.date.minusDays(type.daysBefore)
            val targetLocalDateTime = LocalDateTime.of(targetDate, reminderTime)
            val targetZonedDateTime = targetLocalDateTime.atZone(zoneId)
            val triggerMillis = targetZonedDateTime.toInstant().toEpochMilli()

            if (triggerMillis > nowMillis) {
                reminders.add(
                    ScheduledReminder(
                        eventId = event.id,
                        eventTitle = event.title,
                        type = type,
                        triggerAtMillis = triggerMillis,
                        requestCode = generateRequestCode(event.id, type)
                    )
                )
            }
        }

        return reminders
    }
}
