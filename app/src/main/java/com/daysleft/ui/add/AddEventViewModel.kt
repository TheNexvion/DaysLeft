package com.daysleft.ui.add

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
import java.time.LocalDate

data class AddEventUiState(
    val title: String = "",
    val date: LocalDate? = null,
    val titleError: String? = null,
    val dateError: String? = null,
    val isSaved: Boolean = false
)

class AddEventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState: StateFlow<AddEventUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = null
        )
    }

    fun updateDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            date = date,
            dateError = null
        )
    }

    fun saveEvent() {
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
            repository.insertEvent(
                Event(
                    title = state.title.trim(),
                    date = state.date!!
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val database = AppDatabase.getInstance(application)
                val repository = EventRepository(database.eventDao())
                return AddEventViewModel(repository) as T
            }
        }
    }
}
