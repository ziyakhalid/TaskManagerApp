package uk.ac.tees.mad.q2252114

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateTask : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeTextView: TextView
    private lateinit var descriptionEditText: EditText
    private lateinit var tagsEditText: EditText

    private lateinit var taskViewModel: TaskViewModel

    private var selectedCalendar: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        titleEditText = findViewById(R.id.create_task_title)
        dateEditText = findViewById(R.id.create_task_date)
        timeTextView = findViewById(R.id.create_task_reminder_time)
        descriptionEditText = findViewById(R.id.create_task_description)
        tagsEditText = findViewById(R.id.create_task_tags)

        // Handle date selection when clicking on dateEditText
        dateEditText.setOnClickListener {
            showDatePicker()
        }

        // Handle time selection when clicking on timeTextView
        timeTextView.setOnClickListener {
            showTimePicker()
        }

        // Create a TaskViewModel instance
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        // Handle "Create Task" button click
        val createTaskButton = findViewById<Button>(R.id.create_task_button)
        createTaskButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val date = dateEditText.text.toString()
            val time = timeTextView.text.toString()
            val description = descriptionEditText.text.toString()
            val tags = tagsEditText.text.toString()

            // Validate input
            if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insert the task into the database
            val task = Task(
                id = 0, // SQLite will auto-generate the ID
                title = title,
                description = description,
                dueDate = calculateAlarmTime(date, time),
                isCompleted = false
            )

            // Insert task into SQLite database
            GlobalScope.launch {
                taskViewModel.insert(task)
            }

            // Schedule the alarm
            scheduleAlarm(task.dueDate, task.title, task.description)

            // Show a success message to the user
            Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show()

            // Finish the activity or navigate to another screen
            finish()
        }
    }

    // Function to calculate the alarm time in milliseconds
    private fun calculateAlarmTime(date: String, time: String): Long {
        val dateTimeString = "$date $time"
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateTime = format.parse(dateTimeString)
        return dateTime?.time ?: 0
    }

    // Function to schedule the alarm using AlarmManager
    private fun scheduleAlarm(alarmTimeInMillis: Long, title: String, description: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            // Pass necessary data to the receiver, e.g., title and description
            putExtra("title", title)
            putExtra("description", description)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Schedule the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent)
    }

    // Function to show the date picker dialog
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedCalendar = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            dateEditText.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar?.time))
        }, year, month, day)

        datePickerDialog.show()
    }

    // Function to show the time picker dialog
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedCalendar?.apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
            }
            timeTextView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCalendar?.time)
        }, hour, minute, true)

        timePickerDialog.show()
    }
}
