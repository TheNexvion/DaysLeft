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
}
