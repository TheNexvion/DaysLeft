package com.daysleft.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events ORDER BY date ASC")
    suspend fun getAllEventsDirect(): List<Event>

    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventById(eventId: Long): Flow<Event?>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventByIdDirect(eventId: Long): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Long)
}
