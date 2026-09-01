package com.daysleft.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: LocalDate,
    val remindersEnabled: Boolean = true,
    val remindSevenDaysBefore: Boolean = true,
    val remindOneDayBefore: Boolean = true,
    val remindOnDay: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0
)
