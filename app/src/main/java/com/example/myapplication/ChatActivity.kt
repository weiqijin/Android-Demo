package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.theme.Message
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView



class ChatActivity : ComponentActivity() {
    private lateinit var webSocket: WebSocket
    private val messageList = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)

        // Initialize WebSocket connection
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder().url("wss://10.10.157.17:8888").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    messageList.add(Message(text, false))
                    adapter.notifyItemInserted(messageList.size - 1)
                    saveMessage()
                }

            }
        })

        // Set up RecyclerView and Adapter
        adapter = MessageAdapter(messageList)
        findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter
        findViewById<Button>(R.id.send_button).setOnClickListener {
            sendMessage()
        }
        loadMessages()
    }

    private fun loadMessages() {
        val sharedPref = getSharedPreferences("chat_prefs", MODE_PRIVATE)
        val saveMessages = sharedPref.getStringSet("messages", setOf()) ?: setOf()
        messageList.addAll(saveMessages.map { Message.fromJson(it) })
        adapter.notifyDataSetChanged()
    }

    private fun sendMessage() {
        val message = findViewById<EditText>(R.id.chat_edit_text).text.toString()
        if (message.isNotEmpty()) {
            webSocket.send(message)
            messageList.add(Message(message, true))
            adapter.notifyItemInserted(messageList.size - 1)
            findViewById<EditText>(R.id.chat_edit_text).text.clear()
            saveMessage()
        }
    }

    private fun saveMessage() {
        // Save message to database or server
        val sharedPref = getSharedPreferences("chat_prefs", MODE_PRIVATE)
        sharedPref.edit {
            putStringSet("messages", messageList.map { it.toJson() }.toSet())
        }
    }
}