package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class VideoPlay : ComponentActivity() {
    private lateinit var videoView: VideoView
    private lateinit var inputUrl: EditText
    private lateinit var playButton: Button
    private lateinit var selectButton: Button
    private val PICK_VIDEO_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_play)

        videoView = findViewById(R.id.videoView)
        inputUrl = findViewById(R.id.urlInput)
        playButton = findViewById(R.id.playButton)
        selectButton = findViewById(R.id.selectButton)

        playButton.setOnClickListener {
            val url = inputUrl.text.toString()
            if (url.isNotEmpty()) {
                playVideo(url.toUri())
            }
        }

        selectButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "video/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                playVideo(uri)
            }
        }
    }

    private fun playVideo(url: Uri) {
        videoView.setVideoURI(url)
        videoView.start()
    }

}