package com.example.prisonapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(todo:Todo)
    @Update
    suspend fun update(todo: Todo)
    @Delete
    suspend fun delete(todo:Todo)
    @Query("SELECT * FROM Todo")
    fun getAllTodoItems():List<Todo>
    @Query("SELECT * FROM Todo WHERE id=:id")
    fun getone(id:Int):Todo
    @Query("UPDATE Todo SET item=:newItem WHERE id=:id")
    suspend fun updateItem(id: Int, newItem: String)
}