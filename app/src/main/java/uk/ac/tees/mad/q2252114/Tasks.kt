package uk.ac.tees.mad.q2252114

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Tasks : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        val createNewTaskButton: Button = view.findViewById(R.id.Create_new_task)
        createNewTaskButton.setOnClickListener {
            // Start the CreateTaskActivity when the button is clicked
            // Note: Replace CreateTaskActivity::class.java with your actual activity class
            val intent = Intent(activity, CreateTask::class.java)
            startActivity(intent)
        }
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val taskRepository = TaskRepository(TaskDbHelper(requireContext(), uid), uid)
        val taskViewModelFactory = TaskViewModelFactory(taskRepository)
        taskViewModel = ViewModelProvider(this, taskViewModelFactory).get(TaskViewModel::class.java)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.tasks_recycler_tasks)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        tasksAdapter = TasksAdapter { selectedTask ->
            onTaskItemClick(selectedTask)
        }
        recyclerView.adapter = tasksAdapter

        // Observe changes in the list of tasks
        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            tasksAdapter.submitList(tasks)
            Log.d("TaskFragment", "Observed tasks: $tasks")

        }

        // Swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deletedTask = tasksAdapter.currentList[position]
                taskViewModel.delete(deletedTask)

                Snackbar.make(view, "Task deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        GlobalScope.launch {
                            taskViewModel.insert(deletedTask)
                        }
                    }
                    .show()
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    private fun onTaskItemClick(task: Task) {
        // Handle item click (e.g., open task details)
        // You can implement the behavior you need when a task is clicked
        // For example, you can navigate to a TaskDetailFragment
//        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(task.id)
//        findNavController().navigate(action)
    }
}
