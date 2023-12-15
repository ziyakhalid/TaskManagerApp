package uk.ac.tees.mad.q2252114
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth


class Search : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val taskRepository = TaskRepository(TaskDbHelper(requireContext(), uid), uid)
        val taskViewModelFactory = TaskViewModelFactory(taskRepository)
        taskViewModel = ViewModelProvider(this, taskViewModelFactory).get(TaskViewModel::class.java)

        tasksAdapter = TasksAdapter { /* handle item click if needed */ }
        val searchResultsRecyclerView: RecyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        val searchEditText: TextView = view.findViewById(R.id.searchEditText)


        // Setup RecyclerView
        searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tasksAdapter
        }

        // Setup TextWatcher for search input
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Trigger search when text changes
                searchTasks(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Observe changes in the search results and update the adapter
        taskViewModel.searchResults.observe(viewLifecycleOwner, Observer { tasks ->
            tasksAdapter.submitList(tasks)
        })
    }

    private fun searchTasks(query: String) {
        // Trigger the search in the ViewModel
        taskViewModel.searchTasks(query)
    }
}
