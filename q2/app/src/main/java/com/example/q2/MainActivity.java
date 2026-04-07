package com.example.q2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private VideoView videoView;
    private Uri audioUri;
    private EditText etVideoUrl;

    private final ActivityResultLauncher<Intent> selectAudioLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    audioUri = result.getData().getData();
                    Toast.makeText(this, "Audio file selected", Toast.LENGTH_SHORT).show();
                    stopAudio();
                    playAudio();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Audio controls
        findViewById(R.id.btnOpenFile).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            selectAudioLauncher.launch(intent);
        });

        findViewById(R.id.btnAudioPlay).setOnClickListener(v -> playAudio());
        findViewById(R.id.btnAudioPause).setOnClickListener(v -> pauseAudio());
        findViewById(R.id.btnAudioStop).setOnClickListener(v -> stopAudio());
        findViewById(R.id.btnAudioRestart).setOnClickListener(v -> restartAudio());

        // Video controls
        videoView = findViewById(R.id.videoView);
        etVideoUrl = findViewById(R.id.etVideoUrl);

        findViewById(R.id.btnOpenUrl).setOnClickListener(v -> {
            String url = etVideoUrl.getText().toString();
            if (!url.isEmpty()) {
                videoView.setVideoURI(Uri.parse(url));
                videoView.start();
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnVideoPlay).setOnClickListener(v -> videoView.start());
        findViewById(R.id.btnVideoPause).setOnClickListener(v -> videoView.pause());
        findViewById(R.id.btnVideoStop).setOnClickListener(v -> {
            videoView.stopPlayback();
            String url = etVideoUrl.getText().toString();
            if (!url.isEmpty()) {
                videoView.setVideoURI(Uri.parse(url));
            }
        });
        findViewById(R.id.btnVideoRestart).setOnClickListener(v -> {
            videoView.seekTo(0);
            videoView.start();
        });
    }

    private void playAudio() {
        if (audioUri == null) {
            Toast.makeText(this, "Select an audio file first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error preparing audio", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mediaPlayer.start();
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void restartAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } else {
            playAudio();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
