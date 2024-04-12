package com.example.todolist

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText

import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.example.todolist.adapter.lvAdapter
import com.example.todolist.model.TaskHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var itemList = ArrayList<Task>()
    var fileHelper = FileHelper()
    lateinit var taskHelper: TaskHelper
    lateinit var TaskAdapter: lvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        itemList = fileHelper.readData(this)
//        var arrayAdapter = ArrayAdapter(this,
//        android.R.layout.simple_list_item_1,
//        android.R.id.text1,
//        itemList)
//        lvCongViec.adapter = arrayAdapter
        taskHelper = TaskHelper(this)
        val lvTask = findViewById<ListView>(R.id.lvCongViec)
        itemList = taskHelper.getAllTodoItems()
        TaskAdapter = lvAdapter(this, itemList)
        lvTask.adapter = TaskAdapter

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val edtTitle = findViewById<EditText>(R.id.edttitle)
        val edtDate = findViewById<EditText>(R.id.edtDate)
        val edtLocation = findViewById<EditText>(R.id.edtLocation)
        val edtDes = findViewById<EditText>(R.id.edtdes)


        btnAdd.setOnClickListener{
            val title = edtTitle.text.toString()
            val date = edtDate.text.toString()
            val location = edtLocation.text.toString()
            val description = edtDes.text.toString()

            val Item = Task(0, title, description, date, location)
            //thêm vào list ảo
            itemList.add(Item)
            edttitle.setText("")
            edtdes.setText("")
            edtDate.setText("")
            edtLocation.setText("")
            //thêm vào database
            taskHelper.addTodoItem(Item)
            TaskAdapter.notifyDataSetChanged()
        }

        //xóa item công việc
        lvCongViec.setOnItemClickListener { parent, view, position, id ->
            var alert = AlertDialog.Builder(this)
            alert.setTitle("Delete")
            alert.setMessage("Xong r ha em?  nhấn YES để xóa !")
            alert.setCancelable(true)
            alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                taskHelper.deleteTodoItem(itemList[position])
                itemList.removeAt(position)
                TaskAdapter.notifyDataSetChanged()
                //xóa item đã chọn trong database

            })
            alert.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            alert.create()
            alert.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
}