package uk.ac.tees.mad.q2252114

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _allTasks = MutableLiveData<List<Task>>()
    val allTasks: LiveData<List<Task>> get() = _allTasks

    private val _searchResults = MutableLiveData<List<Task>>()
    val searchResults: LiveData<List<Task>> get() = _searchResults

    init {
        // Initialize the ViewModel by loading all tasks
        loadAllTasks()
    }

    fun loadAllTasks() {
        viewModelScope.launch {
            // Use repository function to get tasks and observe changes
            repository.getAllTasks().observeForever { tasks ->
                _allTasks.value = tasks
            }
        }
    }

    fun insert(task: Task) {
        viewModelScope.launch {
            repository.insert(task)
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    // Function to search tasks based on the query
    fun searchTasks(query: String) {
        val allTasksList = _allTasks.value ?: emptyList()
        val searchResultsList = allTasksList.filter { task ->
            task.title.contains(query, ignoreCase = true) ||
                    task.description?.contains(query, ignoreCase = true) == true
        }
        _searchResults.value = searchResultsList
    }
}
