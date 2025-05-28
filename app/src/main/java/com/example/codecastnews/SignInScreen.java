package com.example.codecastnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignInScreen extends AppCompatActivity {

    private Button btnSignIn; // Declare the Sign In button
    private TextView tvSignUp; // Declare the Sign Up TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the Sign In button from the layout
        btnSignIn = findViewById(R.id.btnSignIn);
        // Set an OnClickListener for the Sign In button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the NewsScreen activity
                Intent intent = new Intent(SignInScreen.this, NewsScreen.class);
                startActivity(intent); // Start the NewsScreen activity
                finish(); // Optional: Close SignInScreen so user can't go back to it
            }
        });

        // Initialize the Sign Up TextView from the layout
        tvSignUp = findViewById(R.id.tvSignUp);
        // Set an OnClickListener for the Sign Up TextView
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SignUpScreen activity
                Intent intent = new Intent(SignInScreen.this, SignUpScreen.class);
                startActivity(intent); // Start the SignUpScreen activity
                // No finish() here, so user can go back to SignInScreen from SignUpScreen
            }
        });
    }
}
