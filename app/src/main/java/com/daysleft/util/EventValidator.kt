package com.daysleft.util

import java.time.LocalDate

/**
 * Result of event input validation.
 */
data class EventValidationResult(
    val titleError: String? = null,
    val dateError: String? = null
) {
    val isValid: Boolean get() = titleError == null && dateError == null
}

/**
 * Shared validator for event creation and editing forms.
 */
object EventValidator {

    fun validate(title: String, date: LocalDate?): EventValidationResult {
        val titleError = if (title.isBlank()) "Please enter an event name" else null
        val dateError = if (date == null) "Please select a target date" else null
        return EventValidationResult(
            titleError = titleError,
            dateError = dateError
        )
    }
}
