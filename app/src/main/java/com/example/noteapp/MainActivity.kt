package com.example.noteapp

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prisonapp.R
import com.example.noteapp.database.Todo
import com.example.noteapp.database.TodoDatabase
import com.example.noteapp.database.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TodoAdapter
    private lateinit var viewModel: MainActivityData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = TodoRepository(TodoDatabase.getInstance(this))
        val recyclerView: RecyclerView = findViewById(R.id.rvTodoList)
        viewModel = ViewModelProvider(this)[MainActivityData::class.java]
        viewModel.data.observe(this) { todos ->
            adapter = TodoAdapter(todos, repository, viewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.getAllTodoItems()
            runOnUiThread {
                viewModel.setData(data)
            }
        }

        val btnAddItem: Button = findViewById(R.id.btnAddTodo)

        btnAddItem.setOnClickListener {
            displayDialog(repository)
        }
    }

    private fun displayDialog(repository: TodoRepository, itemToEdit: Todo? = null) {
        val builder = AlertDialog.Builder(this@MainActivity)

        val title = if (itemToEdit == null) "Enter New Notes:" else "Edit Item"
        builder.setTitle(title)
        builder.setMessage("Enter the item below:")

        val input = EditText(this@MainActivity)
        itemToEdit?.let { input.setText(it.item) }
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val newItem = input.text.toString()
            if (newItem.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (itemToEdit == null) {
                        repository.insert(Todo(newItem))
                    } else {
                        val updatedItem = itemToEdit.copy(item = newItem)
                        repository.update(updatedItem)
                    }
                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main) {
                        viewModel.setData(data)
                        val message = if (itemToEdit == null) "Item Added" else "Item Updated"
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this@MainActivity, "Please enter a valid item", Toast.LENGTH_LONG)
                    .show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

}

