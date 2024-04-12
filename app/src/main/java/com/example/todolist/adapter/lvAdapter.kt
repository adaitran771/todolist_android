package com.example.todolist.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.todolist.R
import com.example.todolist.Task
import kotlin.concurrent.timerTask


class lvAdapter(
    val activity: Activity,
    val list: List<Task>

) : ArrayAdapter<Task>(activity, R.layout.lv_item) {
    override fun getCount(): Int {
        return list.size
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = activity.layoutInflater
        val row = context.inflate(R.layout.lv_item, parent, false)
        val title = row.findViewById<TextView>(R.id.title)
        val decription = row.findViewById<TextView>(R.id.description)
        title.text = list[position].title
        decription.text = list[position].description

        return row
    }
}