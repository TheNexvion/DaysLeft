package com.daysleft.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EventValidatorTest {

    @Test
    fun validate_validInput_returnsValidResult() {
        val result = EventValidator.validate("Birthday", LocalDate.of(2026, 12, 25))
        assertTrue(result.isValid)
        assertNull(result.titleError)
        assertNull(result.dateError)
    }

    @Test
    fun validate_blankTitle_returnsTitleError() {
        val result = EventValidator.validate("   ", LocalDate.of(2026, 12, 25))
        assertFalse(result.isValid)
        assertEquals("Please enter an event name", result.titleError)
        assertNull(result.dateError)
    }

    @Test
    fun validate_nullDate_returnsDateError() {
        val result = EventValidator.validate("Concert", null)
        assertFalse(result.isValid)
        assertNull(result.titleError)
        assertEquals("Please select a target date", result.dateError)
    }

    @Test
    fun validate_emptyTitleAndNullDate_returnsBothErrors() {
        val result = EventValidator.validate("", null)
        assertFalse(result.isValid)
        assertEquals("Please enter an event name", result.titleError)
        assertEquals("Please select a target date", result.dateError)
    }
}
