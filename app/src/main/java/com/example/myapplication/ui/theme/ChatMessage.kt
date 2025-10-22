package com.example.myapplication.ui.theme

data class ChatMessage (
    val from: String,
    val to: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "message")
