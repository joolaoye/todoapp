package com.example.todo_app

import android.content.DialogInterface
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


class TodoAdapter(private val todos : List<Todo>) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        val todoTitle = itemView.findViewById<TextView>(R.id.todoTitle)
        val todoRow = itemView.findViewById<ConstraintLayout>(R.id.todoRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val todoView = inflater.inflate(R.layout.todo_item, parent, false)
        return ViewHolder(todoView)
    }

    override fun getItemCount() = todos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todos[position]

        holder.todoTitle.text = todo.description
        holder.checkBox.setChecked(todo.completed)

        holder.checkBox.setOnClickListener {
            if (holder.checkBox.isChecked) {
                holder.todoTitle.paintFlags = holder.todoTitle.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
            }
            else {
                holder.todoTitle.paintFlags = holder.todoTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        holder.todoRow.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.todoRow.setBackgroundResource(R.drawable.item_pressed)
                    true // Consume the event to prevent further propagation
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Touch released or canceled: Restore original background
                    holder.todoRow.setBackgroundResource(R.drawable.input_field)
                    true // Consume the event to prevent further propagation
                }
                else -> false // Return false for unhandled events
            }
        }
    }
}