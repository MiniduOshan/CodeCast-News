package com.example.codecastnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private EditText etEmail, etPassword;
    private Button btnSignIn, btnSignInGoogle;
    private TextView tvSignUp, tvForgotPassword;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences prefs;

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

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("UserProfilePrefs", MODE_PRIVATE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInScreen.this, SignUpScreen.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInScreen.this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required.");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required.");
            etPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SignInScreen", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignInScreen.this, "Authentication Successful.", Toast.LENGTH_SHORT).show();
                            updateSharedPreferencesWithFirebaseUser(user);
                            updateUI(user);
                        } else {
                            Log.w("SignInScreen", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInScreen.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("SignInScreen", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("SignInScreen", "Google sign in failed", e);
                Toast.makeText(this, "Google Sign In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SignInScreen", "firebaseAuthWithGoogle:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignInScreen.this, "Google Sign-In Successful.", Toast.LENGTH_SHORT).show();
                            updateSharedPreferencesWithFirebaseUser(user);
                            updateUI(user);
                        } else {
                            Log.w("SignInScreen", "firebaseAuthWithGoogle:failure", task.getException());
                            Toast.makeText(SignInScreen.this, "Google Sign-In failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Updates SharedPreferences with the user's display name, email, and phone number
     * obtained from FirebaseUser if available. Prioritizes Firebase phone number.
     */
    private void updateSharedPreferencesWithFirebaseUser(FirebaseUser user) {
        if (user != null) {
            SharedPreferences.Editor editor = prefs.edit();

            String name = user.getDisplayName();
            String email = user.getEmail();
            String firebasePhoneNumber = user.getPhoneNumber(); // Get phone number from FirebaseUser

            // Handle potential null values for name and email
            if (name == null) {
                name = "";
            }
            if (email == null) {
                email = "";
            }

            editor.putString("name", name);
            editor.putString("email", email);

            // --- Phone Number Handling Logic ---
            String storedPhoneNumber = prefs.getString("phoneNumber", ""); // Get existing local number
            String storedCountryCode = prefs.getString("countryCode", "+1"); // Get existing local code

            if (firebasePhoneNumber != null && !firebasePhoneNumber.isEmpty()) {
                // If Firebase has a phone number, use it and attempt to parse country code.
                boolean foundCode = false;
                // Add more country codes if your spinner list has more
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
                    // If no known country code prefix found, use the whole number as is.
                    // Keep the current/default country code as it's likely set by UserProfile.
                    storedPhoneNumber = firebasePhoneNumber;
                }
            }
            // ELSE: If firebasePhoneNumber is null/empty, we keep the 'storedPhoneNumber'
            // and 'storedCountryCode' which were initialized from prefs.getString().
            // This ensures locally entered numbers persist if Firebase doesn't have one.

            editor.putString("phoneNumber", storedPhoneNumber);
            editor.putString("countryCode", storedCountryCode);
            // --- End Phone Number Handling ---

            editor.apply();
            Log.d("SignInScreen", "SharedPreferences updated after sign-in: Name='" + name + "', Email='" + email + "', Phone='" + storedCountryCode + storedPhoneNumber + "'");
        }
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(SignInScreen.this, NewsScreen.class);
            startActivity(intent);
            finish();
        } else {
            // User is signed out or sign-in failed, remain on SignInScreen
        }
    }
}