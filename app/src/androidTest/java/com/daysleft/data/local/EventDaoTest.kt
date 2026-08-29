package com.daysleft.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class EventDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: EventDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.eventDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAllEvents() = runTest {
        val event = Event(title = "Birthday", date = LocalDate.of(2026, 12, 25))
        dao.insertEvent(event)

        val events = dao.getAllEvents().first()
        assertEquals(1, events.size)
        assertEquals("Birthday", events[0].title)
        assertEquals(LocalDate.of(2026, 12, 25), events[0].date)
    }

    @Test
    fun insertAndGetById() = runTest {
        val id = dao.insertEvent(Event(title = "New Year", date = LocalDate.of(2027, 1, 1)))

        val event = dao.getEventById(id).first()
        assertNotNull(event)
        assertEquals("New Year", event!!.title)
        assertEquals(LocalDate.of(2027, 1, 1), event.date)
    }

    @Test
    fun updateEvent() = runTest {
        val id = dao.insertEvent(Event(title = "Original", date = LocalDate.of(2026, 10, 1)))

        dao.updateEvent(Event(id = id, title = "Updated", date = LocalDate.of(2026, 11, 1)))

        val event = dao.getEventById(id).first()
        assertNotNull(event)
        assertEquals("Updated", event!!.title)
        assertEquals(LocalDate.of(2026, 11, 1), event.date)
    }

    @Test
    fun deleteEvent() = runTest {
        val event = Event(title = "ToDelete", date = LocalDate.of(2026, 10, 1))
        val id = dao.insertEvent(event)

        dao.deleteEvent(Event(id = id, title = "ToDelete", date = LocalDate.of(2026, 10, 1)))

        val events = dao.getAllEvents().first()
        assertTrue(events.isEmpty())
    }

    @Test
    fun deleteEventById() = runTest {
        val id = dao.insertEvent(Event(title = "ToDelete", date = LocalDate.of(2026, 10, 1)))

        dao.deleteEventById(id)

        val event = dao.getEventById(id).first()
        assertNull(event)
    }

    @Test
    fun getAllEventsOrderedByDate() = runTest {
        dao.insertEvent(Event(title = "Later", date = LocalDate.of(2026, 12, 1)))
        dao.insertEvent(Event(title = "Earlier", date = LocalDate.of(2026, 10, 1)))
        dao.insertEvent(Event(title = "Middle", date = LocalDate.of(2026, 11, 1)))

        val events = dao.getAllEvents().first()
        assertEquals(3, events.size)
        assertEquals("Earlier", events[0].title)
        assertEquals("Middle", events[1].title)
        assertEquals("Later", events[2].title)
    }

    @Test
    fun multipleInsertAndDelete() = runTest {
        val id1 = dao.insertEvent(Event(title = "Event 1", date = LocalDate.of(2026, 10, 1)))
        val id2 = dao.insertEvent(Event(title = "Event 2", date = LocalDate.of(2026, 11, 1)))
        val id3 = dao.insertEvent(Event(title = "Event 3", date = LocalDate.of(2026, 12, 1)))

        dao.deleteEventById(id2)

        val events = dao.getAllEvents().first()
        assertEquals(2, events.size)
        assertEquals("Event 1", events[0].title)
        assertEquals("Event 3", events[1].title)
    }
}
