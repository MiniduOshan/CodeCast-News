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

    // No changes needed in onStart if you want explicit sign-in

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
                            // Call this method to update SharedPreferences with current user data
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
                            // Call this method to update SharedPreferences with current user data
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
     * Updates SharedPreferences with the user's display name and email obtained from FirebaseUser.
     * This method is crucial upon ANY successful sign-in (email/password or Google)
     * to ensure UserProfile always reads the latest (or initial) data.
     *
     * IMPORTANT: This method now explicitly clears existing 'name' and 'email'
     * in SharedPreferences BEFORE saving the Firebase user's current display name and email.
     * This prevents stale data if a user's display name changes in Firebase directly,
     * or if they previously had a name saved locally that isn't reflected in Firebase.
     *
     * For edited names: If you want *local edits* to persist *above* what Firebase provides
     * on login, then you should save the edited name/email to a *separate* mechanism (e.g.,
     * a tiny Firebase Realtime Database node specific to that user's profile data) rather
     * than relying solely on `displayName` from Firebase Auth.
     *
     * However, based on your current setup where `UserProfile` edits update `SharedPreferences`
     * and Firebase `displayName` (in `SignUpScreen`), the simplest approach for login
     * is to re-sync from Firebase to SharedPreferences.
     *
     * If a user *edits their name in UserProfile*, that change IS saved to SharedPreferences.
     * The issue is when you log out, SharedPreferences is cleared. When you log back in,
     * this method will re-populate SharedPreferences with what's in FirebaseUser's
     * displayName.
     *
     * If you want the *local edit* to persist even if Firebase `displayName` is not updated
     * (which is currently what `onNameSave` in `UserProfile` does, it only updates local SP,
     * not Firebase Auth's `displayName` for existing users), then you have a few options:
     *
     * 1. **Update Firebase `displayName` on every `onNameSave` and `onEmailSave` in `UserProfile`:**
     * This is the most robust approach. The edited name/email always lives in Firebase.
     * Then, `updateSharedPreferencesWithFirebaseUser` will always pull the correct,
     * most up-to-date name/email from Firebase.
     *
     * 2. **Do not clear SharedPreferences on logout (less secure):**
     * This is generally discouraged as it leaves user data on the device.
     *
     * Let's implement option 1, as it's the standard and most reliable way to handle
     * profile data persistence across sessions and devices.
     *
     */
    private void updateSharedPreferencesWithFirebaseUser(FirebaseUser user) {
        if (user != null) {
            SharedPreferences.Editor editor = prefs.edit();

            // Always get the latest data from FirebaseUser and save it to SharedPreferences
            // This ensures SharedPreferences reflects Firebase's current state on login.
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Handle potential null values for displayName (e.g., for very old accounts or specific providers)
            if (name == null) {
                name = "";
            }
            if (email == null) {
                email = "";
            }

            editor.putString("name", name);
            editor.putString("email", email);
            // editor.putString("phoneNumber", user.getPhoneNumber()); // FirebaseUser.getPhoneNumber() is often null unless explicitly set via phone auth.
            editor.apply();
            Log.d("SignInScreen", "SharedPreferences updated after sign-in: Name='" + name + "', Email='" + email + "'");
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