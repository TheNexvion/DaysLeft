package com.daysleft.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.daysleft.data.local.AppDatabase
import com.daysleft.data.local.Event
import com.daysleft.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventDetailsUiState(
    val event: Event? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false
)

class EventDetailsViewModel(
    private val repository: EventRepository,
    private val eventId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

    init {
        observeEvent()
    }

    private fun observeEvent() {
        viewModelScope.launch {
            repository.getEventById(eventId).collect { event ->
                _uiState.value = _uiState.value.copy(
                    event = event,
                    isLoading = false
                )
            }
        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
            repository.deleteEventById(eventId)
            _uiState.value = _uiState.value.copy(isDeleted = true)
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
                        extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                    val database = AppDatabase.getInstance(application)
                    val repository = EventRepository(database.eventDao())
                    return EventDetailsViewModel(repository, eventId) as T
                }
            }
    }
}
