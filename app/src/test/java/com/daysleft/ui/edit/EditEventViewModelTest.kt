package com.daysleft.ui.edit

import com.daysleft.MainDispatcherRule
import com.daysleft.data.FakeEventDao
import com.daysleft.data.local.Event
import com.daysleft.data.repository.EventRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class EditEventViewModelTest {

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
            Event(
                title = "Original Title",
                date = LocalDate.of(2026, 9, 10),
                remindersEnabled = true,
                reminderHour = 9,
                reminderMinute = 0
            )
        )
    }

    @Test
    fun loadsInitialEventData() = runTest {
        val viewModel = EditEventViewModel(repository, eventId)
        val state = viewModel.uiState.value

        assertEquals("Original Title", state.title)
        assertEquals(LocalDate.of(2026, 9, 10), state.date)
        assertTrue(state.remindersEnabled)
        assertEquals(9, state.reminderHour)
        assertEquals(0, state.reminderMinute)
        assertFalse(state.isLoading)
        assertFalse(state.isDirty)
    }

    @Test
    fun editingTitle_marksDirty() = runTest {
        val viewModel = EditEventViewModel(repository, eventId)
        viewModel.updateTitle("Modified Title")

        assertTrue(viewModel.uiState.value.isDirty)
        assertEquals("Modified Title", viewModel.uiState.value.title)
    }

    @Test
    fun editingReminders_marksDirty() = runTest {
        val viewModel = EditEventViewModel(repository, eventId)
        viewModel.updateRemindersEnabled(false)

        assertTrue(viewModel.uiState.value.isDirty)
        assertFalse(viewModel.uiState.value.remindersEnabled)

        viewModel.updateRemindersEnabled(true)
        assertFalse(viewModel.uiState.value.isDirty)
    }

    @Test
    fun revertingTitle_clearsDirty() = runTest {
        val viewModel = EditEventViewModel(repository, eventId)
        viewModel.updateTitle("Modified Title")
        assertTrue(viewModel.uiState.value.isDirty)

        viewModel.updateTitle("Original Title")
        assertFalse(viewModel.uiState.value.isDirty)
    }

    @Test
    fun updateEvent_successful() = runTest {
        val viewModel = EditEventViewModel(repository, eventId)
        val newDate = LocalDate.of(2026, 12, 25)
        viewModel.updateTitle("Updated Title")
        viewModel.updateDate(newDate)
        viewModel.updateReminderTime(11, 45)
        viewModel.updateEvent()

        assertTrue(viewModel.uiState.value.isUpdated)

        val updated = repository.getEventById(eventId).first()
        assertEquals("Updated Title", updated?.title)
        assertEquals(newDate, updated?.date)
        assertEquals(11, updated?.reminderHour)
        assertEquals(45, updated?.reminderMinute)
    }

    @Test
    fun deleteEvent_successful() = runTest {
        val viewModel = EditEventViewModel(repository, eventId)
        viewModel.deleteEvent()

        assertTrue(viewModel.uiState.value.isDeleted)

        val updated = repository.getEventById(eventId).first()
        assertNull(updated)
    }
}
