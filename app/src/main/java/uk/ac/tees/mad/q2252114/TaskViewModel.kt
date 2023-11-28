package uk.ac.tees.mad.q2252114
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class TaskViewModel(application: Application) : AndroidViewModel(application) {



    private val dbHelper = TaskDbHelper(application.applicationContext)

    private val repository = TaskRepository(dbHelper)


    val allTasks: LiveData<List<Task>> = repository.getAllTasks()

    fun insert(task: Task) {
        repository.insert(task)
    }

    fun delete(task: Task) {
        repository.delete(task)
    }
    // Inside TaskViewModel class
//    fun searchTasks(query: String) {
//        val searchQuery = if (query.isBlank()) null else query.trim()
//        allTasks.value = repository.getAllTaskss(searchQuery).value
//    }


//    fun getAllTasks(): Any {
//
//    }
}
