package uk.ac.tees.mad.q2252114

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TaskRepository(private val dbHelper: TaskDbHelper, private val uid: String) {

    fun getAllTasks(): LiveData<List<Task>> {
        val tasksLiveData = MutableLiveData<List<Task>>()

        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            TaskContract.TaskEntry.COLUMN_ID,
            TaskContract.TaskEntry.COLUMN_TITLE,
            TaskContract.TaskEntry.COLUMN_DESCRIPTION,
            TaskContract.TaskEntry.COLUMN_DUE_DATE,
            TaskContract.TaskEntry.COLUMN_COMPLETED,
            TaskContract.TaskEntry.COLUMN_LOCATION
        )

        val selection = "${TaskContract.TaskEntry.COLUMN_UID} = ?"
        val selectionArgs = arrayOf(uid)

        val cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            "${TaskContract.TaskEntry.COLUMN_DUE_DATE} ASC"
        )

        val tasks = mutableListOf<Task>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TITLE))
                val description = getString(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DESCRIPTION))
                val dueDate = getLong(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DUE_DATE))
                val completed = getInt(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_COMPLETED)) == 1
                val location = getString(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_LOCATION)) // Retrieve location
                val task = Task(id, title, description, dueDate, completed, location)
                tasks.add(task)
            }
        }
        cursor.close()

        tasksLiveData.value = tasks
        return tasksLiveData
    }

    fun insert(task: Task) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskContract.TaskEntry.COLUMN_TITLE, task.title)
            put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, task.description)
            put(TaskContract.TaskEntry.COLUMN_DUE_DATE, task.dueDate)
            put(TaskContract.TaskEntry.COLUMN_COMPLETED, if (task.isCompleted) 1 else 0)
            put(TaskContract.TaskEntry.COLUMN_LOCATION, task.location) // Insert location
            put(TaskContract.TaskEntry.COLUMN_UID, uid)
        }

        db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values)
        Log.d("TaskRepository", "Task inserted: $task")
    }

    fun delete(task: Task) {
        val db = dbHelper.writableDatabase
        val selection = "${TaskContract.TaskEntry.COLUMN_ID} = ? AND ${TaskContract.TaskEntry.COLUMN_UID} = ?"
        val selectionArgs = arrayOf(task.id.toString(), uid)

        db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs)
    }

    companion object {
        fun getInstance(context: Context, uid: String): TaskRepository {

            val dbHelper = TaskDbHelper(context,uid)
            return TaskRepository(dbHelper, uid)
        }
    }
}
