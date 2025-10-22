package com.example.myapplication

import android.content.Context
import com.example.myapplication.ui.theme.ChatMessage
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean

class ChatClient(
    private val context: Context,
    serverUri: String,
    private val userId: String
) : WebSocketClient(URI(serverUri)) {

    private val gson = Gson()
    private val isConnected = AtomicBoolean(false)
    var onMessageReceived: ((ChatMessage) -> Unit)? = null
    var onConnectionStatusChanged: ((Boolean) -> Unit)? = null

    init {
        addHeader("user-id", userId)
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("连接到服务器成功")
        isConnected.set(true)
        onConnectionStatusChanged?.invoke(true)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("WebSocket连接关闭: $reason (代码: $code)")
        isConnected.set(false)
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
        isConnected.set(false)
        onConnectionStatusChanged?.invoke(false)
    }

    fun sendMessage(to: String, content: String): Boolean {
        return if (isConnected.get() && this.isOpen) {
            try {
                val message = ChatMessage(
                    from = userId,
                    to = to,
                    content = content
                )
                send(gson.toJson(message))
                true
            } catch (e: Exception) {
                println("发送消息失败: ${e.message}")
                false
            }
        } else {
            println("WebSocket未连接，无法发送消息")
            false
        }
    }

    fun isConnected(): Boolean {
        return isConnected.get() && this.isOpen
    }

    fun disconnectSafely() {
        close()
    }
}