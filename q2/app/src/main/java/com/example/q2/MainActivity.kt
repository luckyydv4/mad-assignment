package com.example.q2

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var videoView: VideoView
    private var audioUri: Uri? = null

    private val selectAudioLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            audioUri = result.data?.data
            Toast.makeText(this, "Audio file selected", Toast.LENGTH_SHORT).show()
            stopAudio() // Stop current audio if any
            playAudio()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Audio controls
        findViewById<Button>(R.id.btnOpenFile).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "audio/*"
            }
            selectAudioLauncher.launch(intent)
        }

        findViewById<Button>(R.id.btnAudioPlay).setOnClickListener { playAudio() }
        findViewById<Button>(R.id.btnAudioPause).setOnClickListener { pauseAudio() }
        findViewById<Button>(R.id.btnAudioStop).setOnClickListener { stopAudio() }
        findViewById<Button>(R.id.btnAudioRestart).setOnClickListener { restartAudio() }

        // Video controls
        videoView = findViewById(R.id.videoView)
        val etVideoUrl = findViewById<EditText>(R.id.etVideoUrl)

        findViewById<Button>(R.id.btnOpenUrl).setOnClickListener {
            val url = etVideoUrl.text.toString()
            if (url.isNotEmpty()) {
                videoView.setVideoURI(Uri.parse(url))
                videoView.start()
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnVideoPlay).setOnClickListener { videoView.start() }
        findViewById<Button>(R.id.btnVideoPause).setOnClickListener { videoView.pause() }
        findViewById<Button>(R.id.btnVideoStop).setOnClickListener { 
            videoView.stopPlayback() 
            // VideoView.stopPlayback() clears the video, so we might need to re-set the URI if we want to "stop" like a player
        }
        findViewById<Button>(R.id.btnVideoRestart).setOnClickListener {
            videoView.seekTo(0)
            videoView.start()
        }
    }

    private fun playAudio() {
        if (audioUri == null) {
            Toast.makeText(this, "Select an audio file first", Toast.LENGTH_SHORT).show()
            return
        }
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, audioUri)
        }
        mediaPlayer?.start()
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
    }

    private fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun restartAudio() {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}