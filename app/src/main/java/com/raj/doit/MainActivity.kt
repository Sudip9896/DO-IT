package com.raj.doit

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.raj.doit.Activity.Completed_Task_Activity
import com.raj.doit.Database.TaskDatabase
import com.raj.doit.adapter.TaskAdapter
import com.raj.doit.databinding.ActivityMainBinding
import com.raj.doit.factory.TaskViewModelFactory
import com.raj.doit.model.Task
import com.raj.doit.viewmodel.TaskViewModel
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var database: TaskDatabase
    lateinit var taskViewModel: TaskViewModel
    lateinit var recycleAdpater: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        database = TaskDatabase(this)
        taskViewModel = ViewModelProvider(this, TaskViewModelFactory(database)).get(TaskViewModel::class.java)

        createNotificationChannel()

        //  recyclerview adapter
        recycleAdpater = TaskAdapter(mutableListOf()) { task ->
            showTaskDetailsDialog(task)
        }
        taskViewModel.getTaskCount().observe(this, Observer { count ->
            if (count != null && count == 0) {
                //if empty
                binding.taskRecycleView.visibility = View.GONE
                binding.tvNoTasks.visibility = View.VISIBLE

            } else {
                // databasea is not empty
                binding.taskRecycleView.visibility = View.VISIBLE
                binding.tvNoTasks.visibility = View.GONE

            }
        })

        binding.taskRecycleView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = recycleAdpater
        }

        taskViewModel.allTasks.observe(this) { tasks ->
            Log.d("MainActivity", "Updating task inRecyclerView")
            recycleAdpater.updateTasks(tasks)
        }

        binding.btnAddTask.setOnClickListener {
            Log.d("MainActivity", "Add task button   clicked")
            opendialogbox()
        }

        setContentView(binding.root)
    }

    // option menu
    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
            // option Menu  created here
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.Complete_Task -> {
                val intent = Intent(this, Completed_Task_Activity::class.java)
                startActivity(intent)
                true
            }
            R.id.Delete_All_Task -> {
                val deletebox = AlertDialog.Builder(this)
                deletebox.setTitle("Delete Task")
                deletebox.setMessage("Are you sure you want to delete all tasks?")
                deletebox.setPositiveButton("Yes") { dialog, _ ->
                    taskViewModel.deleteAllTask()
                    dialog.dismiss()
                }
                deletebox.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                deletebox.create().show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun opendialogbox() {
        val builder = AlertDialog.Builder(this)
        val customView = layoutInflater.inflate(R.layout.customdailogbox_view, null)
        builder.setView(customView)
        builder.setTitle("Enter Task")

        builder.setPositiveButton("Ok") { dialog, _ ->
            val TaskTitle = customView.findViewById<EditText>(R.id.EtTitle)
            val Taskdesc = customView.findViewById<EditText>(R.id.EtDescription)
            val titledata = TaskTitle.text.toString()
            val descdata = Taskdesc.text.toString()

            Log.d("MainActivity", "Inserting task: $titledata")
            taskViewModel.insertTask(titledata, descdata)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showTaskDetailsDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        val customView = layoutInflater.inflate(R.layout.detail_dailogbox, null)
        builder.setView(customView)

        customView.findViewById<Button>(R.id.btnEdit).setOnClickListener {
            openEditTaskDialog(task)
            builder.create().dismiss()
        }

        customView.findViewById<Button>(R.id.btnSetReminder).setOnClickListener {
            Log.d("MainActivity", "Setting reminder for task: ${task.title}")
            setReminderForTask(task)
            builder.create().dismiss()
        }

        customView.findViewById<Button>(R.id.btnCompleteTask).setOnClickListener {
            completeTask(task)
            builder.create().dismiss()
        }

        customView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            taskViewModel.deleteTask(task)
            builder.create().dismiss()
        }


        builder.show()
    }

    private fun openEditTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        val customView = layoutInflater.inflate(R.layout.customdailogbox_view, null)
        builder.setView(customView)
        builder.setTitle("Edit Task")

        val taskTitle = customView.findViewById<EditText>(R.id.EtTitle)
        val taskDesc = customView.findViewById<EditText>(R.id.EtDescription)

        taskTitle.setText(task.title)
        taskDesc.setText(task.desc)

        builder.setPositiveButton("Save") { dialog, _ ->
            val updatedTitle = taskTitle.text.toString()
            val updatedDesc = taskDesc.text.toString()
            taskViewModel.updateTask(task.copy(title = updatedTitle, desc = updatedDesc))
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun completeTask(task: Task) {
        taskViewModel.updateTask(task.copy(task_completed = true))
        Toast.makeText(this, "Task Complete Congrats !!", Toast.LENGTH_SHORT).show()
    }

    private fun setReminderForTask(task: Task) {
        Log.d("MainActivity", "Creating reminder for task: ${task.title}")
        createReminder(task)
    }

    private fun createReminder(task: Task) {
        val builder = AlertDialog.Builder(this)
        val remainder_view = layoutInflater.inflate(R.layout.remindarview, null)
        builder.setView(remainder_view)
        val Timepicker: TimePicker = remainder_view.findViewById(R.id.timepicker)

        builder.setPositiveButton("Pick") { dialog, _ ->
            val hour = Timepicker.hour
            val minute = Timepicker.minute
            Log.d("MainActivity", "Reminder set for ${task.title} at $hour:$minute")
            setReminderForTask(task, hour, minute)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun setReminderForTask(task: Task, hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                showExactAlarmPermissionDialog()
                return
            }
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("taskTitle", task.title)
            putExtra("taskId",task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, task.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            val formattedTime = String.format("%02d:%02d", hour, minute)
            Log.d("MainActivity", "Alarm set successfully for: ${task.title} at $formattedTime")
            Toast.makeText(this, "Reminder set for: ${task.title} at $formattedTime", Toast.LENGTH_SHORT).show()

        } catch (e: SecurityException) {
            Log.e("MainActivity", "SecurityException: ${e.message}")
            Toast.makeText(this, "Unable to schedule exact alarm. Please grant permission.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showExactAlarmPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
        builder.setMessage("To set exact reminders, allow exact alarm permissions in settings.")
        builder.setPositiveButton("Go to Settings") { dialog, _ ->
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TaskReminderChannel"
            val descriptionText = "Channel for Task Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("taskReminderChannel", name, importance)
            channel.description = descriptionText

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }




}// last
