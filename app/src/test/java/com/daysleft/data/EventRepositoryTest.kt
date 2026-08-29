package com.daysleft.data

import com.daysleft.data.local.Event
import com.daysleft.data.repository.EventRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class EventRepositoryTest {

    private lateinit var fakeDao: FakeEventDao
    private lateinit var repository: EventRepository

    @Before
    fun setup() {
        fakeDao = FakeEventDao()
        repository = EventRepository(fakeDao)
    }

    @Test
    fun insertAndGetAllEvents() = runTest {
        val event = Event(title = "Launch Day", date = LocalDate.of(2026, 10, 1))
        val id = repository.insertEvent(event)

        val list = repository.getAllEvents().first()
        assertEquals(1, list.size)
        assertEquals(id, list[0].id)
        assertEquals("Launch Day", list[0].title)
    }

    @Test
    fun getEventById() = runTest {
        val event = Event(title = "Conference", date = LocalDate.of(2026, 11, 15))
        val id = repository.insertEvent(event)

        val fetched = repository.getEventById(id).first()
        assertNotNull(fetched)
        assertEquals("Conference", fetched?.title)
        assertEquals(LocalDate.of(2026, 11, 15), fetched?.date)
    }

    @Test
    fun updateEvent() = runTest {
        val id = repository.insertEvent(Event(title = "Old Name", date = LocalDate.of(2026, 9, 1)))
        repository.updateEvent(Event(id = id, title = "New Name", date = LocalDate.of(2026, 9, 5)))

        val updated = repository.getEventById(id).first()
        assertEquals("New Name", updated?.title)
        assertEquals(LocalDate.of(2026, 9, 5), updated?.date)
    }

    @Test
    fun deleteEvent() = runTest {
        val id = repository.insertEvent(Event(title = "To Delete", date = LocalDate.of(2026, 9, 1)))
        repository.deleteEvent(Event(id = id, title = "To Delete", date = LocalDate.of(2026, 9, 1)))

        val list = repository.getAllEvents().first()
        assertEquals(0, list.size)
    }

    @Test
    fun deleteEventById() = runTest {
        val id = repository.insertEvent(Event(title = "To Delete By ID", date = LocalDate.of(2026, 9, 1)))
        repository.deleteEventById(id)

        val fetched = repository.getEventById(id).first()
        assertNull(fetched)
    }
}
