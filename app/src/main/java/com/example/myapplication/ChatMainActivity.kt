package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.myapplication.databinding.ChatMainBinding

class ChatMainActivity : ComponentActivity() {
    private lateinit var binding: ChatMainBinding
    private var chatServer: ChatServer? = null

    companion object {
        const val SERVER_PORT = 8887
        const val USER_A = "user_a"
        const val USER_B = "user_b"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 启动本地WebSocket服务器
        startChatServer()

        setupUI()
    }

    private fun startChatServer() {
        try {
            if (chatServer == null){
                chatServer = ChatServer(this, SERVER_PORT)
                chatServer?.start()
            }
        } catch (e: Exception) {
            println("启动WebSocket服务器失败: ${e.message}")
        }

    }

    private fun setupUI() {
        binding.btnUserA.setOnClickListener {
            openChatInterface(USER_A, USER_B)
        }

        binding.btnUserB.setOnClickListener {
            openChatInterface(USER_B, USER_A)
        }

        binding.btnViewHistory.setOnClickListener {
            // 可以添加查看完整历史记录的界面
            showHistoryDialog()
        }
    }

    private fun openChatInterface(currentUser: String, targetUser: String) {
        if (chatServer == null){
            startChatServer()
        }

        val intent = Intent(this, NewChatActivity::class.java).apply {
            putExtra("CURRENT_USER", currentUser)
            putExtra("TARGET_USER", targetUser)
            putExtra("SERVER_PORT", SERVER_PORT)
        }
        startActivity(intent)
    }

    private fun showHistoryDialog() {
        val history = chatServer?.getHistoryForUsers(USER_A, USER_B)
        val historyText = history?.joinToString("\n") { msg ->
            "[${formatTime(msg.timestamp)}] ${msg.from}: ${msg.content}"
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("聊天历史")
            .setMessage(if (historyText?.isEmpty() == true) "暂无聊天记录" else historyText)
            .setPositiveButton("确定", null)
            .show()
    }

    private fun formatTime(timestamp: Long): String {
        return android.text.format.DateFormat.format("HH:mm:ss", timestamp).toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing){
            chatServer?.stopServerSafely()
        }
    }

    override fun onPause() {
        super.onPause()
        chatServer?.stopServerSafely()
    }

    override fun onResume() {
        super.onResume()
        if (chatServer == null){
            startChatServer()
        }
    }

    override fun onStart() {
        super.onStart()
        if (chatServer == null){
            startChatServer()
        }
    }
}