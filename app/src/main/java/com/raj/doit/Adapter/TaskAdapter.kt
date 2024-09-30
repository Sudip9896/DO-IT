package com.raj.doit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.doit.R
import com.raj.doit.model.Task

class TaskAdapter(private val tasks: List<Task> , private val  onClick : (Task)-> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val description: TextView = itemView.findViewById(R.id.tvTaskDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.description.text = task.desc


        // click listener to show dalog box
        holder.itemView.setOnClickListener {
            onClick(task)
        }
    }


//    // Method to update the tasks in the adapter
    fun updateTasks(newTasks: List<Task>) {
        (tasks as MutableList).clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
