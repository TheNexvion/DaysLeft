package com.daysleft.reminder

import com.daysleft.data.local.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class ReminderTimeCalculatorTest {

    private val zoneId = ZoneId.of("UTC")

    @Test
    fun calculateReminders_10DaysAway_allThreeRemindersInFuture() {
        // Base now: 2026-10-01 at 08:00 UTC
        val now = LocalDateTime.of(2026, 10, 1, 8, 0).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 42,
            title = "Vacation",
            date = LocalDate.of(2026, 10, 11), // 10 days away
            remindersEnabled = true,
            remindSevenDaysBefore = true,
            remindOneDayBefore = true,
            remindOnDay = true,
            reminderHour = 9,
            reminderMinute = 0
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertEquals(3, reminders.size)
        assertEquals(ReminderType.SEVEN_DAYS_BEFORE, reminders[0].type)
        assertEquals(ReminderType.ONE_DAY_BEFORE, reminders[1].type)
        assertEquals(ReminderType.ON_THE_DAY, reminders[2].type)

        // 7 days before is Oct 4 at 09:00 UTC
        val expectedTrigger7d = LocalDateTime.of(2026, 10, 4, 9, 0).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals(expectedTrigger7d, reminders[0].triggerAtMillis)

        // 1 day before is Oct 10 at 09:00 UTC
        val expectedTrigger1d = LocalDateTime.of(2026, 10, 10, 9, 0).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals(expectedTrigger1d, reminders[1].triggerAtMillis)

        // On the day is Oct 11 at 09:00 UTC
        val expectedTrigger0d = LocalDateTime.of(2026, 10, 11, 9, 0).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals(expectedTrigger0d, reminders[2].triggerAtMillis)
    }

    @Test
    fun calculateReminders_3DaysAway_skipsSevenDaysBefore() {
        // Base now: 2026-10-01 at 08:00 UTC
        val now = LocalDateTime.of(2026, 10, 1, 8, 0).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 10,
            title = "Exam",
            date = LocalDate.of(2026, 10, 4), // 3 days away (7 days before was Sep 27, which is past)
            remindersEnabled = true,
            remindSevenDaysBefore = true,
            remindOneDayBefore = true,
            remindOnDay = true,
            reminderHour = 9,
            reminderMinute = 0
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertEquals(2, reminders.size)
        assertEquals(ReminderType.ONE_DAY_BEFORE, reminders[0].type)
        assertEquals(ReminderType.ON_THE_DAY, reminders[1].type)
    }

    @Test
    fun calculateReminders_tomorrow_before0900_schedulesOneDayAndOnDay() {
        // Base now: 2026-10-01 at 07:00 UTC
        val now = LocalDateTime.of(2026, 10, 1, 7, 0).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 15,
            title = "Birthday",
            date = LocalDate.of(2026, 10, 2), // tomorrow
            remindersEnabled = true,
            remindSevenDaysBefore = true,
            remindOneDayBefore = true,
            remindOnDay = true,
            reminderHour = 9,
            reminderMinute = 0
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertEquals(2, reminders.size)
        assertEquals(ReminderType.ONE_DAY_BEFORE, reminders[0].type) // today at 09:00 (future)
        assertEquals(ReminderType.ON_THE_DAY, reminders[1].type) // tomorrow at 09:00 (future)
    }

    @Test
    fun calculateReminders_tomorrow_after0900_schedulesOnlyOnDay() {
        // Base now: 2026-10-01 at 10:30 UTC
        val now = LocalDateTime.of(2026, 10, 1, 10, 30).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 15,
            title = "Birthday",
            date = LocalDate.of(2026, 10, 2), // tomorrow
            remindersEnabled = true,
            remindSevenDaysBefore = true,
            remindOneDayBefore = true,
            remindOnDay = true,
            reminderHour = 9,
            reminderMinute = 0
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertEquals(1, reminders.size)
        assertEquals(ReminderType.ON_THE_DAY, reminders[0].type)
    }

    @Test
    fun calculateReminders_today_before0900_schedulesOnlyOnDay() {
        // Base now: 2026-10-01 at 06:00 UTC
        val now = LocalDateTime.of(2026, 10, 1, 6, 0).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 20,
            title = "Conference",
            date = LocalDate.of(2026, 10, 1), // today
            remindersEnabled = true,
            remindSevenDaysBefore = true,
            remindOneDayBefore = true,
            remindOnDay = true,
            reminderHour = 9,
            reminderMinute = 0
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertEquals(1, reminders.size)
        assertEquals(ReminderType.ON_THE_DAY, reminders[0].type)
    }

    @Test
    fun calculateReminders_today_after0900_schedulesNothing() {
        // Base now: 2026-10-01 at 12:00 UTC
        val now = LocalDateTime.of(2026, 10, 1, 12, 0).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 20,
            title = "Conference",
            date = LocalDate.of(2026, 10, 1), // today
            remindersEnabled = true,
            remindSevenDaysBefore = true,
            remindOneDayBefore = true,
            remindOnDay = true,
            reminderHour = 9,
            reminderMinute = 0
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun calculateReminders_passedEvent_returnsEmptyList() {
        val now = LocalDateTime.of(2026, 10, 1, 12, 0).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 25,
            title = "Past Anniversary",
            date = LocalDate.of(2026, 9, 20),
            remindersEnabled = true
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun calculateReminders_remindersDisabled_returnsEmptyList() {
        val now = LocalDateTime.of(2026, 10, 1, 8, 0).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 30,
            title = "Future Trip",
            date = LocalDate.of(2026, 11, 1),
            remindersEnabled = false
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun calculateReminders_individualTogglesRespected() {
        val now = LocalDateTime.of(2026, 10, 1, 8, 0).atZone(zoneId).toInstant().toEpochMilli()
        val event = Event(
            id = 35,
            title = "Project Launch",
            date = LocalDate.of(2026, 10, 20),
            remindersEnabled = true,
            remindSevenDaysBefore = false,
            remindOneDayBefore = true,
            remindOnDay = false,
            reminderHour = 14,
            reminderMinute = 30
        )

        val reminders = ReminderTimeCalculator.calculateReminders(event, now, zoneId)

        assertEquals(1, reminders.size)
        assertEquals(ReminderType.ONE_DAY_BEFORE, reminders[0].type)

        val expectedTrigger = LocalDateTime.of(2026, 10, 19, 14, 30).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals(expectedTrigger, reminders[0].triggerAtMillis)
    }

    @Test
    fun generateRequestCode_isDeterministicAndUnique() {
        val codeEvent1_7d = ReminderTimeCalculator.generateRequestCode(1, ReminderType.SEVEN_DAYS_BEFORE)
        val codeEvent1_1d = ReminderTimeCalculator.generateRequestCode(1, ReminderType.ONE_DAY_BEFORE)
        val codeEvent1_0d = ReminderTimeCalculator.generateRequestCode(1, ReminderType.ON_THE_DAY)
        val codeEvent2_7d = ReminderTimeCalculator.generateRequestCode(2, ReminderType.SEVEN_DAYS_BEFORE)

        assertEquals(11, codeEvent1_7d)
        assertEquals(12, codeEvent1_1d)
        assertEquals(13, codeEvent1_0d)
        assertEquals(21, codeEvent2_7d)

        assertNotEquals(codeEvent1_7d, codeEvent1_1d)
        assertNotEquals(codeEvent1_7d, codeEvent2_7d)
    }

    @Test
    fun notificationContent_matchesExpectedFormulas() {
        val title = "Mom's Birthday"
        assertEquals("7 days left", ReminderType.SEVEN_DAYS_BEFORE.getNotificationTitle())
        assertEquals("Mom's Birthday is coming up in 7 days.", ReminderType.SEVEN_DAYS_BEFORE.getNotificationBody(title))

        assertEquals("Tomorrow", ReminderType.ONE_DAY_BEFORE.getNotificationTitle())
        assertEquals("Mom's Birthday is tomorrow.", ReminderType.ONE_DAY_BEFORE.getNotificationBody(title))

        assertEquals("Today is the day 🎉", ReminderType.ON_THE_DAY.getNotificationTitle())
        assertEquals("Mom's Birthday is today.", ReminderType.ON_THE_DAY.getNotificationBody(title))
    }
}
