package com.daysleft.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.daysleft.data.local.AppDatabase
import com.daysleft.data.local.Event
import com.daysleft.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val repository: EventRepository) : ViewModel() {

    val events: Flow<List<Event>> = repository.getAllEvents()

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val database = AppDatabase.getInstance(application)
                val repository = EventRepository(database.eventDao())
                return HomeViewModel(repository) as T
            }
        }
    }
}
