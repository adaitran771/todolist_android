package com.example.todolist

data class Task(
    val id: Int,
    var title: String,
    var description: String,
    var date: String,
    var location: String,
    val status: Int
)