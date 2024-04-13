package com.example.todolist.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todolist.Task

class TaskHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TodoDatabase.db"
        private const val TABLE_NAME = "todos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_DATE TEXT, " +
                "$COLUMN_STATUS INT, " +
                "$COLUMN_LOCATION TEXT)")
        db.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTodoItem(todoItem: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, todoItem.title)
        values.put(COLUMN_DESCRIPTION, todoItem.description)
        values.put(COLUMN_DATE, todoItem.date)
        values.put(COLUMN_LOCATION, todoItem.location)
        values.put(COLUMN_STATUS, todoItem.status)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getAllTodoItems(status: Int): ArrayList<Task> {
        val todoItems = ArrayList<Task>()
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE status = $status"
        val db = this.readableDatabase

        db.rawQuery(selectQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    val description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                    val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                    val location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION))
                    val status = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))
                    val todoItem = Task(id, title, description, date, location, status)
                    todoItems.add(todoItem)
                } while (cursor.moveToNext())
            }
        }

        return todoItems
    }

    fun updateTodoItem(todoItem: Task): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, todoItem.title)
        values.put(COLUMN_DESCRIPTION, todoItem.description)
        values.put(COLUMN_DATE, todoItem.date)
        values.put(COLUMN_LOCATION, todoItem.location)
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(todoItem.id.toString()))
    }
    fun updateStatus(todoItem: Task, newStatus: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS, newStatus)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(todoItem.id.toString()))
    }


    fun deleteTodoItem(todoItem: Task): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(todoItem.id.toString()))
    }
}
