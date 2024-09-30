package com.raj.doit.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.raj.doit.Database.TaskDatabase
import com.raj.doit.databinding.ActivityCompletedTaskBinding
import com.raj.doit.factory.TaskViewModelFactory
import com.raj.doit.viewmodel.TaskViewModel

class Completed_Task_Activity : AppCompatActivity() {
    lateinit var binding: ActivityCompletedTaskBinding
    lateinit var database: TaskDatabase
    lateinit var taskViewModel: TaskViewModel
    lateinit var completedatask_recycleAdpater: Completed_Task_Adapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedTaskBinding.inflate(layoutInflater)
        database = TaskDatabase(this)// initialize
        taskViewModel = ViewModelProvider(this, TaskViewModelFactory(database)).get(TaskViewModel::class.java)//initialize


        taskViewModel.getTaskCount().observe(this, Observer { count ->
            if (count != null && count == 0) {
                //if empty
                binding.CompleteTaskRecycleView.visibility = View.GONE
                binding.tvNoTasks.visibility = View.VISIBLE

            } else {
                // databasea is not empty
                binding.CompleteTaskRecycleView.visibility = View.VISIBLE
                binding.tvNoTasks.visibility = View.GONE

            }
        })
        setContentView(binding.root)

        completedatask_recycleAdpater = Completed_Task_Adapter(mutableListOf())


        binding.CompleteTaskRecycleView.apply {
            layoutManager = LinearLayoutManager(this@Completed_Task_Activity)
            adapter = completedatask_recycleAdpater

        }
        taskViewModel.getAllCompletedTasks().observe(this, Observer { completedTask ->
            completedTask?.let {
                completedatask_recycleAdpater.updateTasks(it)
            }

        })


    }
}