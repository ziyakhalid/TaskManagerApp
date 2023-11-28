package uk.ac.tees.mad.q2252114

import android.provider.BaseColumns

object TaskContract {
    // Define the table schema
    object TaskEntry : BaseColumns {
        const val TABLE_NAME = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DUE_DATE = "dueDate"
        const val COLUMN_COMPLETED = "completed"
    }
}
