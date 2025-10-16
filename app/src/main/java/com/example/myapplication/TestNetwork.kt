package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity


class TestNetwork : ComponentActivity(){
    private val testButton = findViewById<android.widget.Button>(R.id.test_button)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_network)

        //测试按钮监听
//        val testButton = findViewById<android.widget.Button>(R.id.test_button)
        testButton.setOnClickListener {
            testPortAvailability()
        }
    }

    fun testPortAvailability() {
        val showText = findViewById<android.widget.TextView>(R.id.text_result)
        val pcIp = findViewById<android.widget.EditText>(R.id.edit_ip_address).text.toString().trim()
        if (pcIp.isEmpty()) {
            showText.text = "请输入PC的IP地址"
            return
        }
        testButton.isEnabled = false
        testButton.text = "正在测试..."
        showText.text = "正在测试8888端口是否可用，请稍候..."
        //todo 测试端口可用性
    }
}