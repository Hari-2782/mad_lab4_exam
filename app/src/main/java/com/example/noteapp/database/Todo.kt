package com.example.noteapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Todo(
    var item:String?
){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}
