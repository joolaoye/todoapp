package com.example.todo_app

import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class MainActivity : AppCompatActivity() {
    private lateinit var todoTitle : EditText
    private lateinit var addTodo: FloatingActionButton
    private lateinit var todoDialog : AlertDialog

    private lateinit var todos : MutableList<Todo>

    private lateinit var rvTodo : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addTodo = findViewById(R.id.addTodo)
        rvTodo = findViewById(R.id.recyclerView)

        todos = mutableListOf()


        val adapter = TodoAdapter(todos)
        rvTodo.adapter = adapter
        rvTodo.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                // move item in `fromPos` to `toPos` in adapter.
                val temp = todos[fromPos]
                todos.removeAt(fromPos)
                todos.add(toPos, temp)
                adapter.notifyItemMoved(fromPos, toPos)
                return true // true if moved, false otherwise
            }
            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val deletedItem = todos.get(pos).description
                todos.removeAt(pos)
                // remove from adapter
                adapter.notifyItemRemoved(pos)

                class MyUndoListener : View.OnClickListener {

                    override fun onClick(v: View) {
                        todos.add(pos, Todo(deletedItem, false))
                        adapter.notifyItemInserted(pos)
                    }
                }

                Snackbar.make(rvTodo, deletedItem, Snackbar.LENGTH_LONG)
                    .setAction("Undo", MyUndoListener()).show()
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.my_background
                        )
                    )
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(rvTodo)

        val view = layoutInflater.inflate(R.layout.todo_dialog, null)
        todoTitle = view.findViewById(R.id.titleField)

        todoDialog = AlertDialog.Builder(this)
            .setTitle("Enter a todo")
            .setView(view)
            .setNeutralButton("Cancel") { dialog, which -> }
            .setPositiveButton("Add") { dialog, which ->
                todos.add(Todo(todoTitle.text.toString(), false))
                todoTitle.text.clear()
                adapter.notifyItemInserted(todos.size - 1)
                Toast.makeText(this, "Added item to list", Toast.LENGTH_SHORT).show()
            }
            .create()


        addTodo.setOnClickListener {
            todoDialog.show()
            updateButtonState()
        }


        todoTitle.doOnTextChanged { _, _, _, _ ->
            updateButtonState()
        }

    }

    private fun updateButtonState() {
        val isEnabled = todoTitle.text.isNotBlank()
        todoDialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = isEnabled
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val edit = v
                val outR = Rect()
                edit.getGlobalVisibleRect(outR)
                val isKeyboardOpen = !outR.contains(ev.rawX.toInt(), ev.rawY.toInt())
                if (isKeyboardOpen) {
                    edit.clearFocus()
                }

                edit.isCursorVisible = !isKeyboardOpen
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}