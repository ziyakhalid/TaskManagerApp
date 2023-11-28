// TaskCardAdapter.kt
package uk.ac.tees.mad.q2252114

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskCardAdapter :
    ListAdapter<Task, TaskCardAdapter.TaskCardViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardview_task, parent, false)
        return TaskCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskCardViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitleTextView: TextView = itemView.findViewById(R.id.cardview_task_title)
        private val taskDateTextView: TextView = itemView.findViewById(R.id.cardview_task_Date)

        fun bind(task: Task) {
            taskTitleTextView.text = task.title
            // Format the date as needed, you might want to use SimpleDateFormat
            taskDateTextView.text = "October 20, 2020" // Update with the actual date from your Task object
        }
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
