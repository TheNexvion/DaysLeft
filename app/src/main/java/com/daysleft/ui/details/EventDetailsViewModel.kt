package com.daysleft.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.daysleft.DaysLeftApplication
import com.daysleft.data.local.Event
import com.daysleft.data.repository.EventRepository
import com.daysleft.reminder.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailsUiState(
    val event: Event? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false
)

/**
 * ViewModel for observing single event details and handling deletion.
 */
class EventDetailsViewModel(
    private val repository: EventRepository,
    private val eventId: Long,
    private val reminderScheduler: ReminderScheduler? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

    init {
        observeEvent()
    }

    private fun observeEvent() {
        viewModelScope.launch {
            repository.getEventById(eventId).collect { event ->
                _uiState.update {
                    it.copy(
                        event = event,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
            reminderScheduler?.cancelEventReminders(eventId)
            repository.deleteEventById(eventId)
            _uiState.update { it.copy(isDeleted = true) }
        }
    }

    companion object {
        fun factory(eventId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val application =
                        extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DaysLeftApplication
                    return EventDetailsViewModel(
                        repository = application.container.repository,
                        eventId = eventId,
                        reminderScheduler = application.container.reminderScheduler
                    ) as T
                }
            }
    }
}
