package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ChatBinding

class NewChatActivity : ComponentActivity() {
    private lateinit var binding: ChatBinding
    private lateinit var chatClient: ChatClient
    private lateinit var messageAdapter: MessageAdapter
    private val connectionHandler = Handler(Looper.getMainLooper())
    private var connectionCheckRunnable: Runnable? = null

    private var currentUser: String = ""
    private var targetUser: String = ""
    private var serverPort: Int = 8887

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 防止点击输入框时布局变化导致返回
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        initializeData()
        setupUI()
        setupWebSocketClientWithRetry()
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
            sendMessageSafely()
        }

        binding.etMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.rvMessages.postDelayed({
                    if (messageAdapter.itemCount > 0) {
                        binding.rvMessages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }, 200)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        // 添加触摸监听，防止意外操作
        binding.root.setOnTouchListener { _, _ ->
            // 消耗触摸事件，防止传递到其他组件
            false
        }
    }

    private fun setupWebSocketClientWithRetry() {
        val serverUri = "ws://127.0.0.1:$serverPort" // 使用模拟器本地地址

        chatClient = ChatClient(this, serverUri, currentUser).apply {
            onMessageReceived = { message ->
                runOnUiThread {
                    messageAdapter.addMessage(message)
                    if (messageAdapter.itemCount > 0) {
                        binding.rvMessages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }

            onConnectionStatusChanged = { connected ->
                runOnUiThread {
                    updateConnectionUI(connected)
                }
            }
        }

        chatClient.connect()
        // 启动连接状态监控
        startConnectionMonitoring()
    }

    private fun updateConnectionUI(connected: Boolean){
        binding.tvConnectionStatus.text = if (connected) "已连接" else "未连接"
        binding.tvConnectionStatus.setTextColor(
            if (connected) getColor(android.R.color.holo_green_dark)
            else getColor(android.R.color.holo_red_dark)
        )
        // 更新发送按钮状态
        binding.btnSend.isEnabled = connected
        binding.btnSend.alpha = if (connected) 1.0f else 0.5f
    }

    private fun sendMessageSafely() {
        val messageText = binding.etMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val success = chatClient.sendMessage(targetUser, messageText)
            if (success) {
                binding.etMessage.text.clear()
                // 发送后隐藏键盘，避免布局变化
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etMessage.windowToken, 0)
            } else {
                // 发送失败，显示提示
                android.widget.Toast.makeText(
                    this,
                    "消息发送失败，请检查连接状态",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun startConnectionMonitoring() {
        connectionCheckRunnable = object : Runnable {
            override fun run() {
                val isConnected = chatClient.isConnected()
                updateConnectionUI(isConnected)
                // 继续监控
                connectionHandler.postDelayed(this, 3000)
            }
        }
        connectionCheckRunnable?.let { connectionHandler.post(it) }
    }

    private fun stopConnectionMonitoring() {
        connectionCheckRunnable?.let { connectionHandler.removeCallbacks(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopConnectionMonitoring()
        chatClient.disconnectSafely()
    }
}
