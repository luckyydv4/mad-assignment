package com.example.question4;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailsActivity extends AppCompatActivity {

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        imagePath = getIntent().getStringExtra("image_path");
        if (imagePath == null) {
            finish();
            return;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView ivDetailImage = findViewById(R.id.ivDetailImage);
        TextView tvImageName = findViewById(R.id.tvImageName);
        TextView tvImagePath = findViewById(R.id.tvImagePath);
        TextView tvImageSize = findViewById(R.id.tvImageSize);
        TextView tvImageDate = findViewById(R.id.tvImageDate);
        Button btnDelete = findViewById(R.id.btnDelete);

        ivDetailImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        tvImageName.setText("Name: " + imageFile.getName());
        tvImagePath.setText("Path: " + imageFile.getAbsolutePath());
        tvImageSize.setText("Size: " + (imageFile.length() / 1024) + " KB");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        tvImageDate.setText("Date Taken: " + sdf.format(new Date(imageFile.lastModified())));

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(imageFile));
    }

    private void showDeleteConfirmationDialog(File file) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (file.delete()) {
                        Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}