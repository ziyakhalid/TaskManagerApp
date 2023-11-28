package uk.ac.tees.mad.q2252114

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "tasks.db"
        const val DATABASE_VERSION = 1
    }

    // Create the tasks table
    private val SQL_CREATE_ENTRIES = """
        CREATE TABLE ${TaskContract.TaskEntry.TABLE_NAME} (
            ${TaskContract.TaskEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${TaskContract.TaskEntry.COLUMN_TITLE} TEXT NOT NULL,
            ${TaskContract.TaskEntry.COLUMN_DESCRIPTION} TEXT,
            ${TaskContract.TaskEntry.COLUMN_DUE_DATE} INTEGER NOT NULL,
            ${TaskContract.TaskEntry.COLUMN_COMPLETED} INTEGER DEFAULT 0
        )
    """.trimIndent()

    // Drop the tasks table if it exists
    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TaskContract.TaskEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}
