package com.raj.doit.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raj.doit.Database.TaskDatabase
import com.raj.doit.viewmodel.TaskViewModel

class TaskViewModelFactory(private val database: TaskDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
