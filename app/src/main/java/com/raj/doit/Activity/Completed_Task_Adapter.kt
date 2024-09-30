package com.raj.doit.Activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.doit.R
import com.raj.doit.model.Task

class Completed_Task_Adapter(private val tasks: List<Task>): RecyclerView.Adapter<Completed_Task_Adapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvCompleteTaskTitle)
        val description: TextView = itemView.findViewById(R.id.tvCompleteTaskDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
       val completedView = LayoutInflater.from(parent.context).inflate(R.layout.complete_task,null)
        return  TaskViewHolder(completedView)
    }

    override fun getItemCount(): Int {
            return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.description.text = task.desc

    }

    fun updateTasks(newTasks: List<Task>) {
        (tasks as MutableList).clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}