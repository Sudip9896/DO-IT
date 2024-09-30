package com.raj.doit.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "task")
@Parcelize
data class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title :String,
    val desc :String,
    val task_completed:Boolean
):Parcelable