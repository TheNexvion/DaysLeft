package com.daysleft.ui.edit

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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class EditEventUiState(
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
    val isLoading: Boolean = true,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val isDirty: Boolean = false,
    val originalTitle: String = "",
    val originalDate: LocalDate? = null,
    val originalRemindersEnabled: Boolean = true,
    val originalRemindSevenDaysBefore: Boolean = true,
    val originalRemindOneDayBefore: Boolean = true,
    val originalRemindOnDay: Boolean = true,
    val originalReminderHour: Int = 9,
    val originalReminderMinute: Int = 0
)

/**
 * ViewModel for viewing, updating, and deleting an existing countdown event.
 */
class EditEventViewModel(
    private val repository: EventRepository,
    private val eventId: Long,
    private val reminderScheduler: ReminderScheduler? = null
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
                    remindersEnabled = event.remindersEnabled,
                    remindSevenDaysBefore = event.remindSevenDaysBefore,
                    remindOneDayBefore = event.remindOneDayBefore,
                    remindOnDay = event.remindOnDay,
                    reminderHour = event.reminderHour,
                    reminderMinute = event.reminderMinute,
                    isLoading = false,
                    originalTitle = event.title,
                    originalDate = event.date,
                    originalRemindersEnabled = event.remindersEnabled,
                    originalRemindSevenDaysBefore = event.remindSevenDaysBefore,
                    originalRemindOneDayBefore = event.remindOneDayBefore,
                    originalRemindOnDay = event.remindOnDay,
                    originalReminderHour = event.reminderHour,
                    originalReminderMinute = event.reminderMinute
                )
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkDirty(state: EditEventUiState): Boolean {
        return state.title != state.originalTitle ||
                state.date != state.originalDate ||
                state.remindersEnabled != state.originalRemindersEnabled ||
                state.remindSevenDaysBefore != state.originalRemindSevenDaysBefore ||
                state.remindOneDayBefore != state.originalRemindOneDayBefore ||
                state.remindOnDay != state.originalRemindOnDay ||
                state.reminderHour != state.originalReminderHour ||
                state.reminderMinute != state.originalReminderMinute
    }

    fun updateTitle(title: String) {
        _uiState.update { state ->
            val updated = state.copy(title = title, titleError = null)
            updated.copy(isDirty = checkDirty(updated))
        }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update { state ->
            val updated = state.copy(date = date, dateError = null)
            updated.copy(isDirty = checkDirty(updated))
        }
    }

    fun updateRemindersEnabled(enabled: Boolean) {
        _uiState.update { state ->
            val updated = state.copy(remindersEnabled = enabled)
            updated.copy(isDirty = checkDirty(updated))
        }
    }

    fun updateRemindSevenDaysBefore(enabled: Boolean) {
        _uiState.update { state ->
            val updated = state.copy(remindSevenDaysBefore = enabled)
            updated.copy(isDirty = checkDirty(updated))
        }
    }

    fun updateRemindOneDayBefore(enabled: Boolean) {
        _uiState.update { state ->
            val updated = state.copy(remindOneDayBefore = enabled)
            updated.copy(isDirty = checkDirty(updated))
        }
    }

    fun updateRemindOnDay(enabled: Boolean) {
        _uiState.update { state ->
            val updated = state.copy(remindOnDay = enabled)
            updated.copy(isDirty = checkDirty(updated))
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        _uiState.update { state ->
            val updated = state.copy(reminderHour = hour, reminderMinute = minute)
            updated.copy(isDirty = checkDirty(updated))
        }
    }

    fun updateEvent() {
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
            val updatedEvent = Event(
                id = eventId,
                title = state.title.trim(),
                date = state.date!!,
                remindersEnabled = state.remindersEnabled,
                remindSevenDaysBefore = state.remindSevenDaysBefore,
                remindOneDayBefore = state.remindOneDayBefore,
                remindOnDay = state.remindOnDay,
                reminderHour = state.reminderHour,
                reminderMinute = state.reminderMinute
            )

            repository.updateEvent(updatedEvent)
            reminderScheduler?.scheduleEventReminders(updatedEvent)

            _uiState.update { it.copy(isUpdated = true) }
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
                    return EditEventViewModel(
                        repository = application.container.repository,
                        eventId = eventId,
                        reminderScheduler = application.container.reminderScheduler
                    ) as T
                }
            }
    }
}
