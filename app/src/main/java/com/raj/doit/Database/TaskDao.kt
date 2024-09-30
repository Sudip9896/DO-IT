package com.raj.doit.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.raj.doit.model.Task

@Dao
interface TaskDao {


    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun  insertNote(task :Task)
    @Update
    suspend fun  updateNote(task :Task)
    @Delete
    suspend fun  deleteNote(task :Task)

    @Query("SELECT COUNT(*) FROM Task")
    fun getTaskCount(): LiveData<Int>

    @Query("SELECT * FROM TASK WHERE task_completed = 0 ORDER BY id DESC")
    fun getalltask():LiveData<List<Task>>

    @Query("SELECT * FROM TASK WHERE task_completed = 1 ORDER BY id DESC")
    fun getallCompletedtask():LiveData<List<Task>>

    @Query("DELETE FROM TASK")
    fun deleteAllTask()

}