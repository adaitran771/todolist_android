package com.example.todolist.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.todolist.R
import com.example.todolist.Task

class lvAdapter(
    val activity: Activity,
    val list: List<Task>
) : ArrayAdapter<Task>(activity, R.layout.lv_item) {

    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            val inflater = activity.layoutInflater
            row = inflater.inflate(R.layout.lv_item, parent, false)
        }

        val titleTextView = row!!.findViewById<TextView>(R.id.title)
        val descriptionTextView = row.findViewById<TextView>(R.id.description)
        val dateTextView = row.findViewById<TextView>(R.id.date) // Thêm TextView cho date
        val locationTextView = row.findViewById<TextView>(R.id.location) // Thêm TextView cho location

        val task = list[position]

        titleTextView.text = task.title
        descriptionTextView.text = task.description
        dateTextView.text = task.date // Gán giá trị của date
        locationTextView.text = task.location // Gán giá trị của location

        return row
    }
}
