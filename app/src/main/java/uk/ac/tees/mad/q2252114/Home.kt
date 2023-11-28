// Home.kt
package uk.ac.tees.mad.q2252114

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Home : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var inProgressTasksAdapter: TasksAdapter
    private lateinit var taskCardAdapter: TaskCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize ViewModel
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        // Initialize RecyclerView for in-progress tasks using TasksAdapter
        val inProgressRecyclerView: RecyclerView = view.findViewById(R.id.progres_task_vertical)
        inProgressRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        inProgressTasksAdapter = TasksAdapter { selectedTask ->
            onTaskItemClick(selectedTask)
        }
        inProgressRecyclerView.adapter = inProgressTasksAdapter

        // Observe changes in the list of in-progress tasks
        taskViewModel.allTasks.observe(viewLifecycleOwner) { inProgressTasks ->
            inProgressTasksAdapter.submitList(inProgressTasks)
        }

        // Swipe-to-delete functionality for in-progress tasks
        val inProgressItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
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
                val deletedTask = inProgressTasksAdapter.currentList[position]
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

        inProgressItemTouchHelper.attachToRecyclerView(inProgressRecyclerView)

        // Initialize RecyclerView for cards using TaskCardAdapter
        val cardRecyclerView: RecyclerView = view.findViewById(R.id.Home_tasks_cards)
        cardRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        taskCardAdapter = TaskCardAdapter()
        cardRecyclerView.adapter = taskCardAdapter

        // Observe changes in the list of tasks for cards
        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            taskCardAdapter.submitList(tasks)
        }

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
