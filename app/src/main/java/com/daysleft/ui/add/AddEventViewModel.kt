package com.daysleft.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.daysleft.DaysLeftApplication
import com.daysleft.data.local.Event
import com.daysleft.data.repository.EventRepository
import com.daysleft.reminder.ReminderScheduler
import com.daysleft.util.EventValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddEventUiState(
    val title: String = "",
    val date: LocalDate? = null,
    val remindersEnabled: Boolean = true,
    val remindSevenDaysBefore: Boolean = true,
    val remindOneDayBefore: Boolean = true,
    val remindOnDay: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val titleError: String? = null,
    val dateError: String? = null,
    val isSaved: Boolean = false
)

/**
 * ViewModel for creating and persisting new countdown events.
 */
class AddEventViewModel(
    private val repository: EventRepository,
    private val reminderScheduler: ReminderScheduler? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState: StateFlow<AddEventUiState> = _uiState.asStateFlow()

    fun setInitialTitle(title: String) {
        if (_uiState.value.title.isBlank() && title.isNotBlank()) {
            _uiState.update {
                it.copy(
                    title = title,
                    titleError = null
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                titleError = null
            )
        }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update {
            it.copy(
                date = date,
                dateError = null
            )
        }
    }

    fun updateRemindersEnabled(enabled: Boolean) {
        _uiState.update { it.copy(remindersEnabled = enabled) }
    }

    fun updateRemindSevenDaysBefore(enabled: Boolean) {
        _uiState.update { it.copy(remindSevenDaysBefore = enabled) }
    }

    fun updateRemindOneDayBefore(enabled: Boolean) {
        _uiState.update { it.copy(remindOneDayBefore = enabled) }
    }

    fun updateRemindOnDay(enabled: Boolean) {
        _uiState.update { it.copy(remindOnDay = enabled) }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(
                reminderHour = hour,
                reminderMinute = minute
            )
        }
    }

    fun saveEvent() {
        val state = _uiState.value
        val validation = EventValidator.validate(state.title, state.date)

        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    titleError = validation.titleError,
                    dateError = validation.dateError
                )
            }
            return
        }

        viewModelScope.launch {
            val eventToSave = Event(
                title = state.title.trim(),
                date = state.date!!,
                remindersEnabled = state.remindersEnabled,
                remindSevenDaysBefore = state.remindSevenDaysBefore,
                remindOneDayBefore = state.remindOneDayBefore,
                remindOnDay = state.remindOnDay,
                reminderHour = state.reminderHour,
                reminderMinute = state.reminderMinute
            )

            val insertedId = repository.insertEvent(eventToSave)
            val savedEventWithId = eventToSave.copy(id = insertedId)

            reminderScheduler?.scheduleEventReminders(savedEventWithId)

            _uiState.update { it.copy(isSaved = true) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DaysLeftApplication
                return AddEventViewModel(
                    repository = application.container.repository,
                    reminderScheduler = application.container.reminderScheduler
                ) as T
            }
        }
    }
}
