package com.example.myapplication1

data class Task(
    var id: Int = 0,
    val title: String,
    val description: String,
    val datetime: String,
    var status: Boolean = true
)
