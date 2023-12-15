package uk.ac.tees.mad.q2252114

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDbHelper(context: Context, private val uid: String) : SQLiteOpenHelper(context, getDatabaseName(uid), null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME_PREFIX = "tasks_user_"
        const val DATABASE_VERSION = 3 // Increment version to trigger onUpgrade

        fun getDatabaseName(uid: String): String {
            return "$DATABASE_NAME_PREFIX$uid.db"
        }
    }

    // Create the tasks table
    private val SQL_CREATE_ENTRIES = """
        CREATE TABLE ${TaskContract.TaskEntry.TABLE_NAME} (
            ${TaskContract.TaskEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${TaskContract.TaskEntry.COLUMN_UID} TEXT NOT NULL,  
            ${TaskContract.TaskEntry.COLUMN_TITLE} TEXT NOT NULL,
            ${TaskContract.TaskEntry.COLUMN_DESCRIPTION} TEXT,
            ${TaskContract.TaskEntry.COLUMN_DUE_DATE} INTEGER NOT NULL,
            ${TaskContract.TaskEntry.COLUMN_COMPLETED} INTEGER DEFAULT 0,
            ${TaskContract.TaskEntry.COLUMN_LOCATION} TEXT 
        )
    """.trimIndent()

    // Drop the tasks table if it exists
    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TaskContract.TaskEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle upgrades from version 1 to version 2, and future versions if needed
        if (oldVersion < 2) {
            // Alter the existing table to add the new column
            db.execSQL("ALTER TABLE ${TaskContract.TaskEntry.TABLE_NAME} ADD COLUMN ${TaskContract.TaskEntry.COLUMN_LOCATION} TEXT")

            // Add the new column to the CREATE TABLE statement
            db.execSQL("ALTER TABLE ${TaskContract.TaskEntry.TABLE_NAME} ADD COLUMN ${TaskContract.TaskEntry.COLUMN_UID} TEXT NOT NULL DEFAULT ''")
        }

        // Drop the existing table and recreate it
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}
