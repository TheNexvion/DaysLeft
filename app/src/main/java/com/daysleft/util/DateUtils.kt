package com.daysleft.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

enum class EventStatus {
    UPCOMING,
    TODAY,
    PASSED
}

object DateUtils {

    private val displayFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())

    /**
     * Calculates the number of days between today and the given event date.
     * Positive values mean the event is in the future.
     * Zero means the event is today.
     * Negative values mean the event has passed.
     */
    fun daysUntil(eventDate: LocalDate, today: LocalDate = LocalDate.now()): Long {
        return ChronoUnit.DAYS.between(today, eventDate)
    }

    /**
     * Returns a human-readable countdown string.
     */
    fun formatCountdown(eventDate: LocalDate, today: LocalDate = LocalDate.now()): String {
        val days = daysUntil(eventDate, today)
        return when {
            days > 1 -> "$days days left"
            days == 1L -> "1 day left"
            days == 0L -> "Today"
            else -> "Event passed"
        }
    }

    /**
     * Returns the status category of an event.
     */
    fun eventStatus(eventDate: LocalDate, today: LocalDate = LocalDate.now()): EventStatus {
        val days = daysUntil(eventDate, today)
        return when {
            days > 0 -> EventStatus.UPCOMING
            days == 0L -> EventStatus.TODAY
            else -> EventStatus.PASSED
        }
    }

    /**
     * Formats a LocalDate for display (e.g. "September 15, 2026").
     */
    fun formatDate(date: LocalDate): String {
        return date.format(displayFormatter)
    }
}
