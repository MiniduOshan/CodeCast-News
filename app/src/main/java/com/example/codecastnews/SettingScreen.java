package com.example.codecastnews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View; // Import View

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth; // Added for Firebase Auth
import com.google.firebase.auth.FirebaseUser; // Added for Firebase User

public class SettingScreen extends AppCompatActivity {

    private TextView userNameTextView; // Declared at class level for broader access

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the backButton by its ID
        ImageView backButton = findViewById(R.id.backButton);

        // Set an OnClickListener for the backButton
        backButton.setOnClickListener(v -> {
            // Call finish() to close the current activity and go back to the previous one
            finish();
        });

        // Find the TextViews and ImageView that are part of the profile section
        userNameTextView = findViewById(R.id.userName); // Initialized the TextView
        TextView viewProfileTextView = findViewById(R.id.viewProfile);
        ImageView arrowProfileImageView = findViewById(R.id.arrowProfile);

        // Create an OnClickListener for navigating to UserProfile
        View.OnClickListener profileClickListener = v -> {
            Intent intent = new Intent(SettingScreen.this, UserProfile.class);
            startActivity(intent);
        };

        // Set the click listener on each element of the profile section
        userNameTextView.setOnClickListener(profileClickListener);
        viewProfileTextView.setOnClickListener(profileClickListener);
        arrowProfileImageView.setOnClickListener(profileClickListener);

        // --- NEW CODE FOR ABOUT US SECTION ---
        // *** Critical: You MUST add these IDs in your activity_setting_screen.xml for this to work. ***
        ImageView aboutUsCup = findViewById(R.id.aboutUsCup);
        TextView aboutUsText = findViewById(R.id.aboutUsText);
        ImageView aboutUsArrow = findViewById(R.id.aboutUsArrow);

        View.OnClickListener aboutUsClickListener = v -> {
            Intent intent = new Intent(SettingScreen.this, DevInfoScreen.class);
            startActivity(intent);
        };

        // Set the click listener on each of the "About US" elements
        aboutUsCup.setOnClickListener(aboutUsClickListener);
        aboutUsText.setOnClickListener(aboutUsClickListener);
        aboutUsArrow.setOnClickListener(aboutUsClickListener);
        // --- END NEW CODE ---

        // --- START OF NEW CODE TO DISPLAY USER NAME ---
        displayUserName();
        // --- END OF NEW CODE ---
    }

    // --- NEW METHOD TO DISPLAY USER NAME ---
    private void displayUserName() {
        // Get the current Firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Get the display name set during sign-up
            String userName = currentUser.getDisplayName();
            if (userName != null && !userName.isEmpty()) {
                // Set the fetched name to the TextView
                userNameTextView.setText(userName);
            } else {
                // If display name is not set, use a default
                userNameTextView.setText("User Name"); // Or any other default text
            }
        } else {
            // If no user is logged in
            userNameTextView.setText("Guest User"); // Or handle as per your app's logic
        }
    }
    // --- END OF NEW METHOD ---
}