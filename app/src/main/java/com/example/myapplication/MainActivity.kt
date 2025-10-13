package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

//        findViewById<Button>(R.id.appListButton).setOnClickListener {
//            // Handle button click
//            startActivity(Intent(this, AppList::class.java))
//        }
//
//        findViewById<Button>(R.id.videoPlayerButton).setOnClickListener {
//            // Handle button click
//            startActivity(Intent(this, VideoPlay::class.java))
//        }
    }
}