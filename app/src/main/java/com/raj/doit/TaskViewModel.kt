package com.raj.doit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.doit.Database.TaskDatabase
import com.raj.doit.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(private val database: TaskDatabase) : ViewModel() {

    val allTasks: LiveData<List<Task>> = database.getTaskDao().getalltask()

    fun getTaskCount(): LiveData<Int> {
        return database.getTaskDao().getTaskCount()
        }
    fun insertTask(title: String, desc: String) {
        val task = Task(0, title, desc, false)
        viewModelScope.launch {
            database.getTaskDao().insertNote(task)

        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            database.getTaskDao().updateNote(task)
        }
    }
    fun getAllCompletedTasks(): LiveData<List<Task>> {
        return database.getTaskDao().getallCompletedtask()
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            database.getTaskDao().deleteNote(task)
        }
    }
    fun deleteAllTask() {
        viewModelScope.launch(Dispatchers.IO) {
        database.getTaskDao().deleteAllTask()
    }
    }
}
