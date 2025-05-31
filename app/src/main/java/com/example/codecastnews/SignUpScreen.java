package com.example.codecastnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
// Add this import statement:
import android.widget.EditText; // <-- ADD THIS LINE
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpScreen extends AppCompatActivity {

    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonSignUp;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("UserProfilePrefs", MODE_PRIVATE);

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(v -> registerUser());

        findViewById(R.id.textViewSignIn).setOnClickListener(v -> {
            Intent intent = new Intent(SignUpScreen.this, SignInScreen.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String name = editTextUserName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextUserName.setError("User Name is required.");
            editTextUserName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required.");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required.");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters.");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Confirm Password is required.");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match.");
            editTextConfirmPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignUpScreen.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                // Profile updated in Firebase Auth
                                            }
                                        });

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("name", name);
                                editor.putString("email", email);

                                // --- Phone Number Handling Logic ---
                                String firebasePhoneNumber = user.getPhoneNumber(); // Get phone number from FirebaseUser
                                String storedPhoneNumber = prefs.getString("phoneNumber", ""); // Get existing local number
                                String storedCountryCode = prefs.getString("countryCode", "+1"); // Get existing local code

                                if (firebasePhoneNumber != null && !firebasePhoneNumber.isEmpty()) {
                                    boolean foundCode = false;
                                    String[] countryCodes = {"+1", "+44", "+91", "+94", "+86", "+33", "+61", "+81", "+49", "+27"};
                                    for (String code : countryCodes) {
                                        if (firebasePhoneNumber.startsWith(code)) {
                                            storedCountryCode = code;
                                            storedPhoneNumber = firebasePhoneNumber.substring(code.length());
                                            foundCode = true;
                                            break;
                                        }
                                    }
                                    if (!foundCode) {
                                        storedPhoneNumber = firebasePhoneNumber;
                                    }
                                }
                                // If firebasePhoneNumber is null/empty, 'storedPhoneNumber' and 'storedCountryCode'
                                // remain as initialized from prefs.getString(), ensuring local data persists.

                                editor.putString("phoneNumber", storedPhoneNumber);
                                editor.putString("countryCode", storedCountryCode);
                                // --- End Phone Number Handling ---

                                editor.apply();

                                Intent intent = new Intent(SignUpScreen.this, NewsScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(SignUpScreen.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}