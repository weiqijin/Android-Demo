package com.example.myapplication

import android.content.Context
import com.example.myapplication.ui.theme.ChatMessage
import com.google.gson.Gson
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.ByteBuffer

class ChatServer(private val context: Context, port: Int) : WebSocketServer(InetSocketAddress(port)) {
    private val gson = Gson()
    private val connections = mutableMapOf<String, WebSocket>()
    private val chatHistory = mutableListOf<ChatMessage>()

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val userId = handshake.getFieldValue("user-id")
        if (userId.isNotEmpty()) {
            connections[userId] = conn
            println("用户 $userId 已连接")

            // 发送历史消息给新连接的用户
            val userHistory = chatHistory.filter { it.from == userId || it.to == userId }
            userHistory.forEach { message ->
                conn.send(gson.toJson(message))
            }
        }
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        connections.entries.find { it.value == conn }?.let { entry ->
            connections.remove(entry.key)
            println("用户 ${entry.key} 已断开连接")
        }
    }

    override fun onMessage(conn: WebSocket, message: String) {
        try {
            val chatMessage = gson.fromJson(message, ChatMessage::class.java)
            chatHistory.add(chatMessage)

            // 保存到本地存储
            saveMessageToStorage(chatMessage)

            // 转发消息给目标用户
            connections[chatMessage.to]?.send(gson.toJson(chatMessage))
            // 也发回给发送者（用于确认）
            connections[chatMessage.from]?.send(gson.toJson(chatMessage))

            println("消息从 ${chatMessage.from} 发送到 ${chatMessage.to}: ${chatMessage.content}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMessage(conn: WebSocket, message: ByteBuffer) {
        println("收到二进制消息")
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        println("WebSocket错误: ${ex.message}")
        ex.printStackTrace()
    }

    override fun onStart() {
        println("WebSocket服务器已启动在端口: $port")
        loadHistoryFromStorage()
    }

    private fun saveMessageToStorage(message: ChatMessage) {
        val sharedPref = context.getSharedPreferences("chat_history", Context.MODE_PRIVATE)
        val historyJson = sharedPref.getString("messages", "[]")
        val historyList = gson.fromJson(historyJson, Array<ChatMessage>::class.java).toMutableList()
        historyList.add(message)

        with(sharedPref.edit()) {
            putString("messages", gson.toJson(historyList))
            apply()
        }
    }

    private fun loadHistoryFromStorage() {
        val sharedPref = context.getSharedPreferences("chat_history", Context.MODE_PRIVATE)
        val historyJson = sharedPref.getString("messages", "[]")
        val loadedHistory = gson.fromJson(historyJson, Array<ChatMessage>::class.java)
        chatHistory.clear()
        chatHistory.addAll(loadedHistory)
    }

    fun getHistoryForUsers(user1: String, user2: String): List<ChatMessage> {
        return chatHistory.filter {
            (it.from == user1 && it.to == user2) || (it.from == user2 && it.to == user1)
        }.sortedBy { it.timestamp }
    }
}

