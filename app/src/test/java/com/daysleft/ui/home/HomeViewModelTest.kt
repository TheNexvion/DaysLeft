package com.daysleft.ui.home

import com.daysleft.MainDispatcherRule
import com.daysleft.data.FakeEventDao
import com.daysleft.data.local.Event
import com.daysleft.data.repository.EventRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeEventDao
    private lateinit var repository: EventRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        fakeDao = FakeEventDao()
        repository = EventRepository(fakeDao)
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun initialEvents_empty() = runTest {
        val events = viewModel.events.first()
        assertTrue(events.isEmpty())
    }

    @Test
    fun events_reflectRepositoryUpdates() = runTest {
        repository.insertEvent(Event(title = "Vacation", date = LocalDate.of(2026, 12, 20)))
        val events = viewModel.events.first()
        assertEquals(1, events.size)
        assertEquals("Vacation", events[0].title)
    }

    @Test
    fun events_orderedChronologicallyByDate() = runTest {
        repository.insertEvent(Event(title = "Later Event", date = LocalDate.of(2026, 12, 25)))
        repository.insertEvent(Event(title = "Earlier Event", date = LocalDate.of(2026, 10, 5)))
        repository.insertEvent(Event(title = "Middle Event", date = LocalDate.of(2026, 11, 15)))

        val events = viewModel.events.first()
        assertEquals(3, events.size)
        assertEquals("Earlier Event", events[0].title)
        assertEquals("Middle Event", events[1].title)
        assertEquals("Later Event", events[2].title)
    }
}
