package com.daysleft.ui.edit

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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

data class EditEventUiState(
    val title: String = "",
    val date: LocalDate? = null,
    val titleError: String? = null,
    val dateError: String? = null,
    val isLoading: Boolean = true,
    val isUpdated: Boolean = false,
    val isDirty: Boolean = false,
    val originalTitle: String = "",
    val originalDate: LocalDate? = null
)

class EditEventViewModel(
    private val repository: EventRepository,
    private val eventId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditEventUiState())
    val uiState: StateFlow<EditEventUiState> = _uiState.asStateFlow()

    init {
        loadEvent()
    }

    private fun loadEvent() {
        viewModelScope.launch {
            val event = repository.getEventById(eventId).firstOrNull()
            if (event != null) {
                _uiState.value = EditEventUiState(
                    title = event.title,
                    date = event.date,
                    isLoading = false,
                    originalTitle = event.title,
                    originalDate = event.date
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateTitle(title: String) {
        val state = _uiState.value
        _uiState.value = state.copy(
            title = title,
            titleError = null,
            isDirty = title != state.originalTitle || state.date != state.originalDate
        )
    }

    fun updateDate(date: LocalDate) {
        val state = _uiState.value
        _uiState.value = state.copy(
            date = date,
            dateError = null,
            isDirty = state.title != state.originalTitle || date != state.originalDate
        )
    }

    fun updateEvent() {
        val state = _uiState.value
        var hasError = false

        if (state.title.isBlank()) {
            _uiState.value = state.copy(titleError = "Please enter an event name")
            hasError = true
        }

        if (state.date == null) {
            _uiState.value = _uiState.value.copy(dateError = "Please select a date")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            repository.updateEvent(
                Event(
                    id = eventId,
                    title = state.title.trim(),
                    date = state.date!!
                )
            )
            _uiState.value = _uiState.value.copy(isUpdated = true)
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
                    return EditEventViewModel(repository, eventId) as T
                }
            }
    }
}
