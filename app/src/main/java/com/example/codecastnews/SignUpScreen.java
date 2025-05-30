package com.example.codecastnews;

import android.content.Intent; // Import Intent for navigation
import android.os.Bundle;
import android.view.View;    // Import View for OnClickListener
import android.widget.Button; // Import Button
import android.widget.TextView; // Import TextView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpScreen extends AppCompatActivity {

    // Declare the main Sign Up button
    private Button buttonSignUp; // Corresponds to android:id="@+id/buttonSignUp" in XML

    // Declare the "SIGN IN" TextView
    private TextView textViewSignIn; // Corresponds to android:id="@+id/textViewSignIn" in XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the main Sign Up button from your layout
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // Set an OnClickListener for the main Sign Up button
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For demonstration, clicking "SIGN UP" will also go to SignInScreen.
                // In a real app, this would typically involve user registration logic
                // and then navigate to a different screen (e.g., NewsScreen or back to SignInScreen).
                Intent intent = new Intent(SignUpScreen.this, SignInScreen.class);
                startActivity(intent);
                // Optional: finish() this activity if you don't want the user to return to SignUpScreen
                // after a successful sign-up (e.g., if they are immediately logged in).
                // finish();
            }
        });

        // Initialize the "SIGN IN" TextView from your layout
        textViewSignIn = findViewById(R.id.textViewSignIn);

        // Set an OnClickListener for the "SIGN IN" TextView
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SignInScreen activity
                Intent intent = new Intent(SignUpScreen.this, SignInScreen.class);
                startActivity(intent); // Start the SignInScreen activity

                // Finish this SignUpScreen so the user cannot go back to it
                // after choosing to sign in from the sign-up page.
                finish();
            }
        });
    }
}
