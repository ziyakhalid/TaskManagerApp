package uk.ac.tees.mad.q2252114

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateTask : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeTextView: TextView
    private lateinit var descriptionEditText: EditText
    private lateinit var tagsEditText: EditText
    private lateinit var locationButton: ImageButton
    private lateinit var locationTextView: TextView
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var locationManager: LocationManager
    private var selectedCalendar: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        titleEditText = findViewById(R.id.create_task_title)
        dateEditText = findViewById(R.id.create_task_date)
        timeTextView = findViewById(R.id.create_task_reminder_time)
        descriptionEditText = findViewById(R.id.create_task_description)
        tagsEditText = findViewById(R.id.create_task_tags)
        locationButton = findViewById(R.id.loc_btn)
        locationTextView = findViewById(R.id.loc_text)

        // Handle date selection when clicking on dateEditText
        dateEditText.setOnClickListener {
            showDatePicker()
        }

        // Handle time selection when clicking on timeTextView
        timeTextView.setOnClickListener {
            showTimePicker()
        }

        // Handle location button click
        locationButton.setOnClickListener {
            requestLocation()
        }

        // Create a TaskViewModel instance
        val taskRepository = TaskRepository(TaskDbHelper(this))
        val taskViewModelFactory = TaskViewModelFactory(taskRepository)
        taskViewModel = ViewModelProvider(this, taskViewModelFactory).get(TaskViewModel::class.java)

        // Handle "Create Task" button click
        val createTaskButton = findViewById<Button>(R.id.create_task_button)
        createTaskButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val date = dateEditText.text.toString()
            val time = timeTextView.text.toString()
            val description = descriptionEditText.text.toString()
            val tags = tagsEditText.text.toString()
            val location = locationTextView.text.toString()

            // Validate input
            if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insert the task into the database
            val task = Task(
                id = 0,
                title = title,
                description = description,
                dueDate = calculateAlarmTime(date, time),
                isCompleted = false,
                location = location
            )

            // Insert task into SQLite database
            GlobalScope.launch {
                taskViewModel.insert(task)
            }


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

            try {
                // Try to format the date and set it to the timeTextView
                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCalendar?.time)
                timeTextView.text = formattedTime
            } catch (e: Exception) {
                // Handle the exception and show a toast to the user
                Toast.makeText(this, "Error formatting time", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

        }, hour, minute, true)

        timePickerDialog.show()
    }

    // Function to request location updates
    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            checkLocationEnabled()
        }
    }

    // Function to check if location services are enabled
    private fun checkLocationEnabled() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // If location services are not enabled, prompt the user to enable them
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            // Request location updates
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
        }
    }

    // LocationListener to handle location updates
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Perform reverse geocoding in a background thread
            GlobalScope.launch {
                val address = getAddressFromLocation(location)
                withContext(Dispatchers.Main) {
                    // Update the UI on the main thread with the obtained address
                    locationTextView.text = address
                }
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    // Function to perform reverse geocoding and get the address from the location
    private suspend fun getAddressFromLocation(location: Location): String {
        val geocoder = Geocoder(this@CreateTask, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                addresses[0]?.getAddressLine(0) ?: "Address line not available"
            } else {
                "Address not found"
            }
        } catch (e: IOException) {
            "Error fetching address"
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
