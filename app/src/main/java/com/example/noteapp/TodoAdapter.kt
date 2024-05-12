package com.example.noteapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.database.Todo
import com.example.noteapp.database.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.AlertDialog
import android.widget.EditText
import com.example.prisonapp.R

class TodoAdapter(items:List<Todo>, repository: TodoRepository,
                  viewModel: MainActivityData
): RecyclerView.Adapter<ToDoViewHolder>() {
    var context:Context? = null
    val items = items
    val repository = repository
    val viewModel = viewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item,parent,false)
        context=parent.context
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.cbTodo.text = items.get(position).item
        holder.ivDelete.setOnClickListener {
            val isChecked = holder.cbTodo.isChecked
            if (isChecked) {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.delete(items.get(position))
                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main) {
                        viewModel.setData(data)
                    }
                }
                Toast.makeText(context, "Item Deleted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Select the item to delete", Toast.LENGTH_LONG).show()
            }
        }
        holder.ivEdit.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Edit Item")

            val input = EditText(context)
            input.setText(items[position].item)
            builder.setView(input)

            builder.setPositiveButton("OK") { dialog, which ->
                val newItem = input.text.toString()
                if (newItem.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val updatedItem = items[position].copy(item = newItem)
                        repository.update(updatedItem)
                        val data = repository.getAllTodoItems()
                        withContext(Dispatchers.Main) {
                            viewModel.setData(data)
                            Toast.makeText(context, "Item Updated", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please enter a valid item", Toast.LENGTH_LONG).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

            builder.show()
        }
    }

        override fun getItemCount(): Int {
        return items.size
    }

}
