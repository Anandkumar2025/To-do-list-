package com.example.todolist

data class Task(
    var id: Int = 0,
    val title: String,
    val description: String,
    val datetime: String,
    var status: Boolean = true
)