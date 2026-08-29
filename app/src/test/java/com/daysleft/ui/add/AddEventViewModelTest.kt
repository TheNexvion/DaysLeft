package com.daysleft.ui.add

import com.daysleft.MainDispatcherRule
import com.daysleft.data.FakeEventDao
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

class AddEventViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeEventDao
    private lateinit var repository: EventRepository
    private lateinit var viewModel: AddEventViewModel

    @Before
    fun setup() {
        fakeDao = FakeEventDao()
        repository = EventRepository(fakeDao)
        viewModel = AddEventViewModel(repository)
    }

    @Test
    fun updateTitle_updatesStateAndClearsError() {
        viewModel.saveEvent() // triggers errors
        assertNotNull(viewModel.uiState.value.titleError)

        viewModel.updateTitle("New Event")
        assertEquals("New Event", viewModel.uiState.value.title)
        assertNull(viewModel.uiState.value.titleError)
    }

    @Test
    fun updateDate_updatesStateAndClearsError() {
        viewModel.saveEvent() // triggers errors
        assertNotNull(viewModel.uiState.value.dateError)

        val date = LocalDate.of(2026, 11, 20)
        viewModel.updateDate(date)
        assertEquals(date, viewModel.uiState.value.date)
        assertNull(viewModel.uiState.value.dateError)
    }

    @Test
    fun saveEvent_withValidationErrors() {
        viewModel.saveEvent()
        val state = viewModel.uiState.value
        assertEquals("Please enter an event name", state.titleError)
        assertEquals("Please select a date", state.dateError)
        assertFalse(state.isSaved)
    }

    @Test
    fun saveEvent_successful_persistsToRepositoryAndSetsSaved() = runTest {
        viewModel.updateTitle("Concert")
        viewModel.updateDate(LocalDate.of(2026, 10, 15))
        viewModel.saveEvent()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)

        val savedEvents = repository.getAllEvents().first()
        assertEquals(1, savedEvents.size)
        assertEquals("Concert", savedEvents[0].title)
        assertEquals(LocalDate.of(2026, 10, 15), savedEvents[0].date)
    }
}
