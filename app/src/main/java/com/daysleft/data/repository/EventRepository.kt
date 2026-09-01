package com.daysleft.data.repository

import com.daysleft.data.local.Event
import com.daysleft.data.local.EventDao
import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture repository interface for managing Event data.
 * Abstracts local database operations from ViewModels and domain components.
 */
interface EventRepository {

    /**
     * Observes all events sorted chronologically by target date.
     */
    fun getAllEvents(): Flow<List<Event>>

    /**
     * Fetches all events directly in a one-shot query.
     */
    suspend fun getAllEventsDirect(): List<Event>

    /**
     * Observes a single event by its unique ID.
     */
    fun getEventById(eventId: Long): Flow<Event?>

    /**
     * Fetches a single event directly in a one-shot query.
     */
    suspend fun getEventByIdDirect(eventId: Long): Event?

    /**
     * Inserts a new event or replaces an existing one, returning the generated row ID.
     */
    suspend fun insertEvent(event: Event): Long

    /**
     * Updates an existing event.
     */
    suspend fun updateEvent(event: Event)

    /**
     * Deletes an event by instance.
     */
    suspend fun deleteEvent(event: Event)

    /**
     * Deletes an event by its unique ID.
     */
    suspend fun deleteEventById(eventId: Long)

    companion object {
        /**
         * Factory function to create default implementation.
         */
        operator fun invoke(eventDao: EventDao): EventRepository = EventRepositoryImpl(eventDao)
    }
}

/**
 * Default implementation of [EventRepository] backed by Room [EventDao].
 */
class EventRepositoryImpl(
    private val eventDao: EventDao
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    override suspend fun getAllEventsDirect(): List<Event> = eventDao.getAllEventsDirect()

    override fun getEventById(eventId: Long): Flow<Event?> = eventDao.getEventById(eventId)

    override suspend fun getEventByIdDirect(eventId: Long): Event? = eventDao.getEventByIdDirect(eventId)

    override suspend fun insertEvent(event: Event): Long = eventDao.insertEvent(event)

    override suspend fun updateEvent(event: Event) = eventDao.updateEvent(event)

    override suspend fun deleteEvent(event: Event) = eventDao.deleteEvent(event)

    override suspend fun deleteEventById(eventId: Long) = eventDao.deleteEventById(eventId)
}
