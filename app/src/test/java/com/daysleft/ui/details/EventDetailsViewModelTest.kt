package com.daysleft.ui.details

import com.daysleft.MainDispatcherRule
import com.daysleft.data.FakeEventDao
import com.daysleft.data.local.Event
import com.daysleft.data.repository.EventRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class EventDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeEventDao
    private lateinit var repository: EventRepository
    private var eventId: Long = 0L

    @Before
    fun setup() = runTest {
        fakeDao = FakeEventDao()
        repository = EventRepository(fakeDao)
        eventId = repository.insertEvent(
            Event(title = "Graduation", date = LocalDate.of(2026, 6, 15))
        )
    }

    @Test
    fun loadsEventDetails() = runTest {
        val viewModel = EventDetailsViewModel(repository, eventId)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNotNull(state.event)
        assertEquals("Graduation", state.event?.title)
        assertEquals(LocalDate.of(2026, 6, 15), state.event?.date)
    }

    @Test
    fun deleteEvent_removesFromRepositoryAndSetsDeleted() = runTest {
        val viewModel = EventDetailsViewModel(repository, eventId)
        viewModel.deleteEvent()

        assertTrue(viewModel.uiState.value.isDeleted)

        val deletedEvent = repository.getEventById(eventId).first()
        assertNull(deletedEvent)
    }
}
