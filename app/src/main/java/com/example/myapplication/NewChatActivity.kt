package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ChatBinding

class NewChatActivity : ComponentActivity() {
    private lateinit var binding: ChatBinding
    private lateinit var chatClient: ChatClient
    private lateinit var messageAdapter: MessageAdapter

    private var currentUser: String = ""
    private var targetUser: String = ""
    private var serverPort: Int = 8887

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeData()
        setupUI()
        setupWebSocketClient()
    }

    private fun initializeData() {
        currentUser = intent.getStringExtra("CURRENT_USER") ?: ""
        targetUser = intent.getStringExtra("TARGET_USER") ?: ""
        serverPort = intent.getIntExtra("SERVER_PORT", 8887)
    }

    private fun setupUI() {
        binding.tvCurrentUser.text = "当前用户: $currentUser"
        binding.tvTargetUser.text = "正在与 $targetUser 聊天"

        // 设置消息列表
        messageAdapter = MessageAdapter(currentUser)
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@NewChatActivity)
            adapter = messageAdapter
        }

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        binding.etMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.rvMessages.postDelayed({
                    binding.rvMessages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }, 100)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupWebSocketClient() {
        val serverUri = "ws://10.0.2.2:$serverPort" // 使用模拟器本地地址

        chatClient = ChatClient(this, serverUri, currentUser).apply {
            onMessageReceived = { message ->
                runOnUiThread {
                    messageAdapter.addMessage(message)
                    binding.rvMessages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }

            onConnectionStatusChanged = { connected ->
                runOnUiThread {
                    binding.tvConnectionStatus.text = if (connected) "已连接" else "未连接"
                    binding.tvConnectionStatus.setTextColor(
                        if (connected) getColor(android.R.color.holo_green_dark)
                        else getColor(android.R.color.holo_red_dark)
                    )
                }
            }
        }

        chatClient.connect()
    }

    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {
            chatClient.sendMessage(targetUser, messageText)
            binding.etMessage.text.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatClient.disconnectSafely()
    }
}