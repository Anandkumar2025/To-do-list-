package com.example.myapplication1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database1(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATETIME = "datetime"
        private const val COLUMN_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_TITLE TEXT,$COLUMN_DESCRIPTION TEXT,$COLUMN_DATETIME TEXT,$COLUMN_STATUS BOOLEAN)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(task: Task): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DATETIME, task.datetime)
            put(COLUMN_STATUS, if (task.status) 1 else 0)
        }
        return db.insert(TABLE_NAME, null, contentValues)

    }

    fun updateTask(task: Task, taskId: Int): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DATETIME, task.datetime)
            put(COLUMN_STATUS, if (task.status) 1 else 0)
        }
        return db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
    }


    fun deleteTask(title: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_TITLE = ?", arrayOf(title))
    }

    fun updateTaskStatus(title: String, status: Boolean): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_STATUS, if (status) 1 else 0)
        }
        return db.update(TABLE_NAME, contentValues, "$COLUMN_TITLE = ?", arrayOf(title))
    }

    fun getAllTasks(): List<Task> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT*FROM $TABLE_NAME",null)
        val tasks = mutableListOf<Task>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val datetime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATETIME))
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)) == 1
            tasks.add(Task(id, title, description, datetime, status))
        }
        cursor.close()
        return tasks
    }

}

