package com.daysleft.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    // --- daysUntil tests ---

    @Test
    fun daysUntil_futureDate_returnsPositive() {
        val today = LocalDate.of(2026, 9, 1)
        val eventDate = LocalDate.of(2026, 9, 18)
        assertEquals(17L, DateUtils.daysUntil(eventDate, today))
    }

    @Test
    fun daysUntil_tomorrow_returns1() {
        val today = LocalDate.of(2026, 9, 1)
        val eventDate = LocalDate.of(2026, 9, 2)
        assertEquals(1L, DateUtils.daysUntil(eventDate, today))
    }

    @Test
    fun daysUntil_today_returns0() {
        val today = LocalDate.of(2026, 9, 1)
        assertEquals(0L, DateUtils.daysUntil(today, today))
    }

    @Test
    fun daysUntil_yesterday_returnsNegative1() {
        val today = LocalDate.of(2026, 9, 2)
        val eventDate = LocalDate.of(2026, 9, 1)
        assertEquals(-1L, DateUtils.daysUntil(eventDate, today))
    }

    @Test
    fun daysUntil_pastDate_returnsNegative() {
        val today = LocalDate.of(2026, 9, 18)
        val eventDate = LocalDate.of(2026, 9, 1)
        assertEquals(-17L, DateUtils.daysUntil(eventDate, today))
    }

    @Test
    fun daysUntil_leapYear_feb28ToMar1() {
        // 2028 is a leap year
        val today = LocalDate.of(2028, 2, 28)
        val eventDate = LocalDate.of(2028, 3, 1)
        assertEquals(2L, DateUtils.daysUntil(eventDate, today))
    }

    @Test
    fun daysUntil_nonLeapYear_feb28ToMar1() {
        // 2027 is not a leap year
        val today = LocalDate.of(2027, 2, 28)
        val eventDate = LocalDate.of(2027, 3, 1)
        assertEquals(1L, DateUtils.daysUntil(eventDate, today))
    }

    @Test
    fun daysUntil_leapDay() {
        val today = LocalDate.of(2028, 2, 28)
        val eventDate = LocalDate.of(2028, 2, 29)
        assertEquals(1L, DateUtils.daysUntil(eventDate, today))
    }

    @Test
    fun daysUntil_crossYearBoundary() {
        val today = LocalDate.of(2026, 12, 30)
        val eventDate = LocalDate.of(2027, 1, 2)
        assertEquals(3L, DateUtils.daysUntil(eventDate, today))
    }

    @Test
    fun daysUntil_longDistance_oneYear() {
        val today = LocalDate.of(2026, 1, 1)
        val eventDate = LocalDate.of(2027, 1, 1)
        assertEquals(365L, DateUtils.daysUntil(eventDate, today))
    }

    // --- formatCountdown tests ---

    @Test
    fun formatCountdown_17days() {
        val today = LocalDate.of(2026, 9, 1)
        val eventDate = LocalDate.of(2026, 9, 18)
        assertEquals("17 days left", DateUtils.formatCountdown(eventDate, today))
    }

    @Test
    fun formatCountdown_1day() {
        val today = LocalDate.of(2026, 9, 1)
        val eventDate = LocalDate.of(2026, 9, 2)
        assertEquals("1 day left", DateUtils.formatCountdown(eventDate, today))
    }

    @Test
    fun formatCountdown_today() {
        val today = LocalDate.of(2026, 9, 1)
        assertEquals("Today", DateUtils.formatCountdown(today, today))
    }

    @Test
    fun formatCountdown_yesterday() {
        val today = LocalDate.of(2026, 9, 2)
        val eventDate = LocalDate.of(2026, 9, 1)
        assertEquals("Event passed", DateUtils.formatCountdown(eventDate, today))
    }

    @Test
    fun formatCountdown_farPast() {
        val today = LocalDate.of(2026, 9, 18)
        val eventDate = LocalDate.of(2026, 1, 1)
        assertEquals("Event passed", DateUtils.formatCountdown(eventDate, today))
    }

    // --- eventStatus tests ---

    @Test
    fun eventStatus_futureDate_isUpcoming() {
        val today = LocalDate.of(2026, 9, 1)
        val eventDate = LocalDate.of(2026, 9, 18)
        assertEquals(EventStatus.UPCOMING, DateUtils.eventStatus(eventDate, today))
    }

    @Test
    fun eventStatus_today_isToday() {
        val today = LocalDate.of(2026, 9, 1)
        assertEquals(EventStatus.TODAY, DateUtils.eventStatus(today, today))
    }

    @Test
    fun eventStatus_pastDate_isPassed() {
        val today = LocalDate.of(2026, 9, 18)
        val eventDate = LocalDate.of(2026, 9, 1)
        assertEquals(EventStatus.PASSED, DateUtils.eventStatus(eventDate, today))
    }

    // --- formatDate tests ---

    @Test
    fun formatDate_standardDate() {
        val date = LocalDate.of(2026, 9, 15)
        val formatted = DateUtils.formatDate(date)
        // The exact format depends on locale, but should contain the date components
        assert(formatted.contains("15"))
        assert(formatted.contains("2026"))
    }
}
