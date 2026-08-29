package com.daysleft.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: LocalDate
)
