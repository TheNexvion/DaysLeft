package com.daysleft.data

import com.daysleft.data.local.Event
import com.daysleft.data.local.EventDao
import com.daysleft.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeEventDao : EventDao {
    private val eventsMap = mutableMapOf<Long, Event>()
    private val eventsFlow = MutableStateFlow<List<Event>>(emptyList())
    private var currentId = 1L

    private fun updateFlow() {
        eventsFlow.value = eventsMap.values.sortedBy { it.date }
    }

    override fun getAllEvents(): Flow<List<Event>> = eventsFlow

    override fun getEventById(eventId: Long): Flow<Event?> = eventsFlow.map { list ->
        list.find { it.id == eventId }
    }

    override suspend fun insertEvent(event: Event): Long {
        val id = if (event.id == 0L) currentId++ else event.id
        val newEvent = event.copy(id = id)
        eventsMap[id] = newEvent
        updateFlow()
        return id
    }

    override suspend fun updateEvent(event: Event) {
        eventsMap[event.id] = event
        updateFlow()
    }

    override suspend fun deleteEvent(event: Event) {
        eventsMap.remove(event.id)
        updateFlow()
    }

    override suspend fun deleteEventById(eventId: Long) {
        eventsMap.remove(eventId)
        updateFlow()
    }
}
