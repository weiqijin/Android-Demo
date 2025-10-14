package com.example.myapplication.ui.theme

import org.json.JSONObject

data class Message (
    val content: String,
    val isSent: Boolean){
    fun toJson(): String = JSONObject().apply {
        put("content", content)
        put("isSent", isSent)
    }.toString()

    companion object {
        fun fromJson(json: String): Message {
            val obj = JSONObject(json)
            return Message(obj.getString("content"), obj.getBoolean("isSent"))
        }
    }
}