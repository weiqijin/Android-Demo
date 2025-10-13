package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val appListButton = findViewById<Button>(R.id.appListButton)
        appListButton.setOnClickListener {
            val intent = Intent(this, AppList::class.java)
            startActivity(intent)
        }

        val videoPlayButton = findViewById<Button>(R.id.videoPlayerButton)
        videoPlayButton.setOnClickListener {
            val intent = Intent(this, VideoPlay::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
    }
}