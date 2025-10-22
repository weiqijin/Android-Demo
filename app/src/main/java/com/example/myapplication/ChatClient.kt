package com.example.myapplication

import android.content.Context
import com.example.myapplication.ui.theme.ChatMessage
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class ChatClient(
    private val context: Context,
    serverUri: String,
    private val userId: String
) : WebSocketClient(URI(serverUri)) {

    private val gson = Gson()
    var onMessageReceived: ((ChatMessage) -> Unit)? = null
    var onConnectionStatusChanged: ((Boolean) -> Unit)? = null

    init {
        addHeader("user-id", userId)
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("连接到服务器成功")
        onConnectionStatusChanged?.invoke(true)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("连接已关闭: $reason")
        onConnectionStatusChanged?.invoke(false)
    }

    override fun onMessage(message: String?) {
        message?.let {
            try {
                val chatMessage = gson.fromJson(it, ChatMessage::class.java)
                onMessageReceived?.invoke(chatMessage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onError(ex: Exception?) {
        println("连接错误: ${ex?.message}")
        onConnectionStatusChanged?.invoke(false)
    }

    fun sendMessage(to: String, content: String) {
        val message = ChatMessage(
            from = userId,
            to = to,
            content = content
        )
        send(gson.toJson(message))
    }

    fun disconnectSafely() {
        close()
    }
}