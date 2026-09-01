package com.daysleft.reminder

enum class ReminderType(
    val idOffset: Int,
    val daysBefore: Long,
    val label: String
) {
    SEVEN_DAYS_BEFORE(
        idOffset = 1,
        daysBefore = 7,
        label = "7 days before"
    ),
    ONE_DAY_BEFORE(
        idOffset = 2,
        daysBefore = 1,
        label = "1 day before"
    ),
    ON_THE_DAY(
        idOffset = 3,
        daysBefore = 0,
        label = "On the day"
    );

    fun getNotificationTitle(): String = when (this) {
        SEVEN_DAYS_BEFORE -> "7 days left"
        ONE_DAY_BEFORE -> "Tomorrow"
        ON_THE_DAY -> "Today is the day 🎉"
    }

    fun getNotificationBody(eventTitle: String): String = when (this) {
        SEVEN_DAYS_BEFORE -> "$eventTitle is coming up in 7 days."
        ONE_DAY_BEFORE -> "$eventTitle is tomorrow."
        ON_THE_DAY -> "$eventTitle is today."
    }

    companion object {
        fun fromIdOffset(offset: Int): ReminderType? {
            return entries.firstOrNull { it.idOffset == offset }
        }
    }
}
