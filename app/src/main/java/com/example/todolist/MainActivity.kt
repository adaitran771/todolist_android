package com.example.todolist

import android.app.Activity
import android.app.Dialog
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

import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.todolist.adapter.lvAdapter
import com.example.todolist.model.TaskHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_task_dialog.*
import kotlinx.android.synthetic.main.lv_item.*
import kotlinx.android.synthetic.main.lv_item.view.*
import java.io.Serializable


class MainActivity : AppCompatActivity() {
    val Done = 1
    val notDone = 0
    var itemList = ArrayList<Task>()

    lateinit var taskHelper: TaskHelper
    lateinit var TaskAdapter: lvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //đăng kí context menu cho listview
        registerForContextMenu(lvCongViec)
        //khởi tạo đối tượng thao tác với database
        taskHelper = TaskHelper(this)
        //lấy tất cả Task
        itemList = taskHelper.getAllTodoItems(notDone)
        //khởi tạo adapter cho listview
        TaskAdapter = lvAdapter(this, itemList)
        lvCongViec.adapter = TaskAdapter

        // nút thêm task
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener{
            //tạo alertDialog
            val formAdd_dialog = AlertDialog.Builder(this)
            // tạo đối tượng view để nhập task - formAdd
            val formAdd = layoutInflater.inflate(R.layout.add_task_dialog, null)
            // các đối tượng trong formAdd
            val edtTitle = formAdd.findViewById<EditText>(R.id.edttitle)
            val edtDate = formAdd.findViewById<EditText>(R.id.edtDate)
            val edtLocation = formAdd.findViewById<EditText>(R.id.edtLocation)
            val edtDes = formAdd.findViewById<EditText>(R.id.edtdes)
            //nút thêm trong formAdd
            val add = formAdd.findViewById<Button>(R.id.btnAdd)

            add.setOnClickListener {
                if(!edtTitle.text.toString().isEmpty() &&
                    !edtDate.text.toString().isEmpty() &&
                    !edtLocation.text.toString().isEmpty() &&
                    !edtDes.text.toString().isEmpty() ) {
                    Toast.makeText(this, "nhap thanh cong",Toast.LENGTH_SHORT).show()
                    // add task thành công và thoát dialog
                    val Item = Task(0, edtTitle.text.toString(),
                                          edtDes.text.toString(),
                                          edtDate.text.toString(),
                                          edtLocation.text.toString(),
                                          notDone)
                    //thêm vào list ảo
                    itemList.add(Item)
                    edtTitle.setText("")
                    edtDes.setText("")
                    edtDate.setText("")
                    edtLocation.setText("")
                    //thêm vào database
                    taskHelper.addTodoItem(Item)
                    TaskAdapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(this, "nhap thieu",Toast.LENGTH_SHORT).show()
                }

            }

            //////
            formAdd_dialog.setNegativeButton("Hủy", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            formAdd_dialog.setView(formAdd).create().show()

        }

        //mở context menu khi click vào mỗi hàng listview
        lvCongViec.setOnItemClickListener { parent, view, position, id ->
            var alert = AlertDialog.Builder(this)
            alert.setTitle("Hoàn Thành")
            alert.setMessage("Bạn Có Chắc Đã Xong? Nhấn Yes Để Xác nhận !")
            alert.setCancelable(true)
            alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                taskHelper.updateStatus(itemList[position],Done)
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

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        //lấy thông tin dòng kích hoạt contextmenu
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position
        val selectedItem = TaskAdapter.getItem(position) as? Task

        return when (item.itemId) {
            R.id.edit -> {
                // TODO:  chức năng sửa ở đây
                selectedItem?.let {
                    editItem(it)
                    Toast.makeText(this, "Edit item at position ${info?.position}", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.done -> {

                // TODO:  chức năng xóa ở đây
                selectedItem?.let {
                    TaskAdapter.remove(it)
                    TaskAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Done item at position $position", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
    private fun editItem(item: Task) {
        val intent = Intent(this, EditTaskActivity::class.java)
        intent.putExtra("taskToEdit", item as Serializable) // Truyền Task cần sửa thông qua Intent
        startActivityForResult(intent, EDIT_TASK_REQUEST_CODE) // Khởi chạy EditTaskActivity
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Nhận Task đã sửa từ Intent
            val editedTask = data?.getSerializableExtra("editedTask") as? Task

            // Kiểm tra nếu Task không null và thực hiện cập nhật
            editedTask?.let {
                updateTaskInList(it)
                Toast.makeText(this, "Task edited: ${it.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTaskInList(editedTask: Task) {
        for (i in itemList.indices) {
            if (itemList[i].id == editedTask.id) {
                itemList[i] = editedTask
                TaskAdapter.notifyDataSetChanged()
                break
            }
        }
    }

    companion object {
        private const val EDIT_TASK_REQUEST_CODE = 101
    }
}