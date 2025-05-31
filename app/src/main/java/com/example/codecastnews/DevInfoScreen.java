package com.example.codecastnews;

import android.os.Bundle;
import android.widget.ImageView; // Import ImageView
import android.view.View;       // Import View

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DevInfoScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dev_info_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the backArrow ImageView by its ID
        ImageView backArrow = findViewById(R.id.backArrow);

        // Set an OnClickListener for the backArrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the back arrow is clicked, finish the current activity
                // This will take you back to the previous activity in the stack, which is SettingScreen
                finish();
            }
        });
    }
}