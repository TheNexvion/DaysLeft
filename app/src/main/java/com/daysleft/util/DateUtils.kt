package com.daysleft.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.abs

enum class EventStatus {
    URGENT,
    UPCOMING,
    TODAY,
    PASSED
}

object DateUtils {

    private val displayFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    private val shortDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())

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
            days == -1L -> "1 day ago"
            else -> "${abs(days)} days ago"
        }
    }

    /**
     * Returns the status category of an event.
     * URGENT: 1 or 2 days remaining (< 3 days)
     * UPCOMING: 3 or more days remaining
     * TODAY: 0 days remaining
     * PASSED: less than 0 days
     */
    fun eventStatus(eventDate: LocalDate, today: LocalDate = LocalDate.now()): EventStatus {
        val days = daysUntil(eventDate, today)
        return when {
            days in 1..2 -> EventStatus.URGENT
            days >= 3 -> EventStatus.UPCOMING
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

    /**
     * Formats a LocalDate with day of week (e.g. "Tuesday, September 15, 2026").
     */
    fun formatDateWithDay(date: LocalDate): String {
        return date.format(dayOfWeekFormatter)
    }

    /**
     * Formats a LocalDate compactly (e.g. "15 Sep 2026").
     */
    fun formatShortDate(date: LocalDate): String {
        return date.format(shortDateFormatter)
    }

    /**
     * Returns a detailed breakdown for the event details screen (e.g. "3 weeks, 2 days" or "1 month, 4 days").
     */
    fun getDetailedCountdown(eventDate: LocalDate, today: LocalDate = LocalDate.now()): String {
        val days = daysUntil(eventDate, today)
        if (days == 0L) {
            return "Happening today"
        }

        val isFuture = days > 0
        val start = if (isFuture) today else eventDate
        val end = if (isFuture) eventDate else today
        val totalDays = abs(days)

        val parts = mutableListOf<String>()

        if (totalDays < 7) {
            val unit = if (totalDays == 1L) "1 day" else "$totalDays days"
            return if (isFuture) "$unit remaining" else "$unit ago"
        }

        val period = Period.between(start, end)
        val months = period.years * 12 + period.months
        val remainingDays = period.days

        if (months > 0) {
            parts.add(if (months == 1) "1 month" else "$months months")
        }

        val weeks = remainingDays / 7
        val daysAfterWeeks = remainingDays % 7

        if (weeks > 0) {
            parts.add(if (weeks == 1) "1 week" else "$weeks weeks")
        }
        if (daysAfterWeeks > 0) {
            parts.add(if (daysAfterWeeks == 1) "1 day" else "$daysAfterWeeks days")
        }

        val breakdown = if (parts.isEmpty()) {
            val unit = if (totalDays == 1L) "1 day" else "$totalDays days"
            unit
        } else {
            parts.joinToString(", ")
        }

        return if (isFuture) "$breakdown left" else "$breakdown ago"
    }

    /**
     * Date Preset Helpers for Quick Selection
     */
    fun presetTomorrow(today: LocalDate = LocalDate.now()): LocalDate = today.plusDays(1)

    fun presetThisWeekend(today: LocalDate = LocalDate.now()): LocalDate {
        return when (today.dayOfWeek) {
            DayOfWeek.SATURDAY -> today.plusDays(1) // Sunday
            DayOfWeek.SUNDAY -> today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)) // Next Saturday
            else -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)) // Upcoming Saturday
        }
    }

    fun presetInOneWeek(today: LocalDate = LocalDate.now()): LocalDate = today.plusWeeks(1)

    fun presetInOneMonth(today: LocalDate = LocalDate.now()): LocalDate = today.plusMonths(1)
}
