package com.daysleft.data.repository

import com.daysleft.data.local.Event
import com.daysleft.data.local.EventDao
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {

    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    fun getEventById(eventId: Long): Flow<Event?> = eventDao.getEventById(eventId)

    suspend fun insertEvent(event: Event): Long = eventDao.insertEvent(event)

    suspend fun updateEvent(event: Event) = eventDao.updateEvent(event)

    suspend fun deleteEvent(event: Event) = eventDao.deleteEvent(event)

    suspend fun deleteEventById(eventId: Long) = eventDao.deleteEventById(eventId)
}
