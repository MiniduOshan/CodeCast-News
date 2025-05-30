package com.example.codecastnews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Ensure this layout contains your ProgressBar

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the ProgressBar
        progressBar = findViewById(R.id.progressBar2);

        // Start the progress bar animation
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1; // Increment progress
                    // Update the progress bar on the UI thread
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for a short period to simulate loading
                        Thread.sleep(30); // Adjust this value to control speed (e.g., 50ms)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // When progress reaches 100%, transition to SignInScreen
                handler.post(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, SignInScreen.class);
                        startActivity(intent);
                        finish(); // Close MainActivity so user can't go back to splash screen
                    }
                });
            }
        }).start(); // Start the thread
    }
}
