package com.example.todolist

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.todolist.adapter.lvAdapter
import com.example.todolist.model.TaskHelper
import kotlinx.android.synthetic.main.activity_main.*

class DoneTask : AppCompatActivity() {
    val Done = 1
    val notDone = 0
    var itemList = ArrayList<Task>()

    lateinit var taskHelper: TaskHelper
    lateinit var TaskAdapter: lvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done_task)
        //đăng kí context menu cho listview
        registerForContextMenu(lvCongViec)
        //khởi tạo đối tượng thao tác với database
        taskHelper = TaskHelper(this)
        //lấy tất cả Task đã xong
        itemList = taskHelper.getAllTodoItems(Done)
        //khởi tạo adapter cho listview
        TaskAdapter = lvAdapter(this, itemList)
        lvCongViec.adapter = TaskAdapter




        //mở context menu khi click vào mỗi hàng listview
        //hoàn tác Task khi click, set status = 0 : notDone
        lvCongViec.setOnItemClickListener { parent, view, position, id ->
            var alert = AlertDialog.Builder(this)
            alert.setTitle("Hoàn Tác Nhiệm Vụ ?")
            alert.setMessage("Bạn Có Chắc Muốn Hoàn Tác? Nhấn Yes Để Xác nhận !")
            alert.setCancelable(true)
            alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                taskHelper.updateStatus(itemList[position],notDone)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.notdone -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
        val menuItem = menu?.findItem(R.id.edit)
        menuItem?.isVisible = false
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        //lấy thông tin dòng kích hoạt contextmenu
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position


        return when (item.itemId) {


            R.id.done -> {

                // TODO:  chức năng xóa ở đây
                var alert = AlertDialog.Builder(this)
                alert.setTitle("Xóa Task ?")
                alert.setMessage("Bạn Có Muốn Xóa ? Nhấn Yes Để Xác nhận !")
                alert.setCancelable(true)
                alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    //xóa item đã chọn trong database
                    taskHelper.deleteTodoItem(itemList[position])
                    //xóa item trên list ảo
                    itemList.removeAt(position)
                    TaskAdapter.notifyDataSetChanged()


                })
                alert.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
                alert.create()
                alert.show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

}