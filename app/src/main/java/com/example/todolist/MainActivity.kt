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
import android.view.animation.AnimationUtils
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

        // Đăng kí context menu cho ListView
        registerForContextMenu(lvCongViec)

        // Khởi tạo đối tượng thao tác với cơ sở dữ liệu
        taskHelper = TaskHelper(this)

        // Lấy tất cả Task
        itemList = taskHelper.getAllTodoItems(notDone)

        // Khởi tạo adapter cho ListView
        TaskAdapter = lvAdapter(this, itemList)
        lvCongViec.adapter = TaskAdapter

        // Nút thêm task
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener{
            // Tạo alertDialog
            val formAdd_dialog = AlertDialog.Builder(this)

            // Tạo đối tượng view để nhập task - formAdd
            val formAdd = layoutInflater.inflate(R.layout.add_task_dialog, null)

            // Các đối tượng trong formAdd
            val edtTitle = formAdd.findViewById<EditText>(R.id.edttitle)
            val edtDate = formAdd.findViewById<EditText>(R.id.edtDate)
            val edtLocation = formAdd.findViewById<EditText>(R.id.edtLocation)
            val edtDes = formAdd.findViewById<EditText>(R.id.edtdes)

            // Nút thêm trong formAdd
            val add = formAdd.findViewById<Button>(R.id.btnAdd)

            // Thêm animation cho nút thêm
            val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim)
            add.startAnimation(rotateAnimation)

            add.setOnClickListener {
                if(!edtTitle.text.toString().isEmpty() &&
                    !edtDate.text.toString().isEmpty() &&
                    !edtLocation.text.toString().isEmpty() &&
                    !edtDes.text.toString().isEmpty() ) {
                    Toast.makeText(this, "Nhập thành công",Toast.LENGTH_SHORT).show()

                    // Add task thành công và thoát dialog
                    val Item = Task(0, edtTitle.text.toString(),
                        edtDes.text.toString(),
                        edtDate.text.toString(),
                        edtLocation.text.toString(),
                        notDone)

                    // Thêm vào list ảo
                    itemList.add(Item)
                    edtTitle.setText("")
                    edtDes.setText("")
                    edtDate.setText("")
                    edtLocation.setText("")

                    // Thêm vào database
                    taskHelper.addTodoItem(Item)
                    TaskAdapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(this, "Nhập thiếu",Toast.LENGTH_SHORT).show()
                }

            }

            // Set animation cho dialog
            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            formAdd.startAnimation(fadeIn)

            formAdd_dialog.setNegativeButton("Hủy", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })

            formAdd_dialog.setView(formAdd).create().show()
        }

        // Mở context menu khi click vào mỗi hàng ListView
        lvCongViec.setOnItemClickListener { parent, view, position, id ->
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Hoàn Thành")
            alert.setMessage("Bạn có chắc đã xong? Nhấn Yes để xác nhận!")
            alert.setCancelable(true)
            alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                taskHelper.updateStatus(itemList[position], Done)
                itemList.removeAt(position)
                TaskAdapter.notifyDataSetChanged()
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
            R.id.done -> {
                val intent = Intent(this, DoneTask::class.java)
                startActivity(intent)
                true
            }
            R.id.notdone -> {
                // Xử lý khi click vào Menu Item 2
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
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Lấy thông tin dòng kích hoạt contextmenu
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position

        return when (item.itemId) {
            R.id.edit -> {
                // TODO: Chức năng sửa ở đây
                val selectedItem = itemList[position]
                val formEdit_dialog = AlertDialog.Builder(this)

                // Tạo đối tượng view để nhập task - formAdd
                val formEdit = layoutInflater.inflate(R.layout.add_task_dialog, null)

                // Các đối tượng trong formEdit
                val edtTitle = formEdit.findViewById<EditText>(R.id.edttitle)
                val edtDate = formEdit.findViewById<EditText>(R.id.edtDate)
                val edtLocation = formEdit.findViewById<EditText>(R.id.edtLocation)
                val edtDes = formEdit.findViewById<EditText>(R.id.edtdes)

                // Nút sửa trong formEdit
                val Edit = formEdit.findViewById<Button>(R.id.btnAdd)
                Edit.setText("OK")

                // Gắn nội dung cũ vào form
                edtTitle.setText(selectedItem.title)
                edtDate.setText(selectedItem.date)
                edtLocation.setText(selectedItem.location)
                edtDes.setText(selectedItem.description)

                // Set animation cho dialog
                val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                formEdit.startAnimation(fadeIn)

                Edit.setOnClickListener {
                    if (!edtTitle.text.toString().isEmpty() &&
                        !edtDate.text.toString().isEmpty() &&
                        !edtLocation.text.toString().isEmpty() &&
                        !edtDes.text.toString().isEmpty()
                    ) {
                        Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show()

                        // Sửa task thành công và thoát dialog
                        selectedItem.title = edtTitle.text.toString()
                        selectedItem.date = edtDate.text.toString()
                        selectedItem.location = edtLocation.text.toString()
                        selectedItem.description = edtDes.text.toString()

                        // Sửa vào database
                        taskHelper.updateTodoItem(selectedItem)
                        TaskAdapter.notifyDataSetChanged()
                    }
                    Toast.makeText(
                        this,
                        "Edited item at position ${info?.position}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                formEdit_dialog.setNegativeButton("Hủy", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })

                formEdit_dialog.setView(formEdit).create().show()
                true
            }
            R.id.done -> {
                // TODO: Chức năng xóa ở đây
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Xóa Task ?")
                alert.setMessage("Bạn có muốn xóa ? Nhấn Yes để xác nhận !")
                alert.setCancelable(true)
                alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    // Xóa item đã chọn trong database
                    taskHelper.deleteTodoItem(itemList[position])
                    // Xóa item trên list ảo
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
