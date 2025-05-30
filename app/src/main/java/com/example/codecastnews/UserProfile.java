package com.example.codecastnews;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

// Import statements for your dialogs from the new package
import com.example.codecastnews.dialogs.ChangePasswordDialogFragment;
import com.example.codecastnews.dialogs.EditEmailDialogFragment;
import com.example.codecastnews.dialogs.EditNameDialogFragment;
import com.example.codecastnews.dialogs.EditPhoneNumberDialogFragment;
import com.example.codecastnews.dialogs.SignOutDialogFragment;

public class UserProfile extends AppCompatActivity implements
        EditNameDialogFragment.EditNameDialogListener,
        EditPhoneNumberDialogFragment.EditPhoneNumberDialogListener,
        EditEmailDialogFragment.EditEmailDialogListener,
        ChangePasswordDialogFragment.ChangePasswordDialogListener,
        SignOutDialogFragment.SignOutDialogListener {

    private EditText nameEditText;
    private EditText phoneNumberEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private MaterialButton signOutButton;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signOutButton = findViewById(R.id.signOutButton);
        backArrow = findViewById(R.id.backArrow);

        // Set OnClickListener for nameEditText
        nameEditText.setOnClickListener(v -> showEditNameDialog());

        // Set OnClickListener for phoneNumberEditText
        phoneNumberEditText.setOnClickListener(v -> showEditPhoneNumberDialog());

        // Set OnClickListener for emailEditText
        emailEditText.setOnClickListener(v -> showEditEmailDialog());

        // Set OnClickListener for passwordEditText to show Change Password dialog
        passwordEditText.setOnClickListener(v -> showChangePasswordDialog());

        // Set OnClickListener for signOutButton
        signOutButton.setOnClickListener(v -> showSignOutDialog());

        // Optional: Add a click listener for the back arrow
        backArrow.setOnClickListener(v -> onBackPressed());
    }

    // --- Dialog Display Methods ---
    private void showEditNameDialog() {
        String currentName = nameEditText.getText().toString();
        EditNameDialogFragment dialog = EditNameDialogFragment.newInstance(currentName);
        dialog.setEditNameDialogListener(this);
        dialog.show(getSupportFragmentManager(), "EditNameDialog");
    }

    private void showEditPhoneNumberDialog() {
        String currentPhoneNumber = phoneNumberEditText.getText().toString();
        EditPhoneNumberDialogFragment dialog = EditPhoneNumberDialogFragment.newInstance(currentPhoneNumber);
        dialog.setEditPhoneNumberDialogListener(this);
        dialog.show(getSupportFragmentManager(), "EditPhoneNumberDialog");
    }

    private void showEditEmailDialog() {
        String currentEmail = emailEditText.getText().toString();
        EditEmailDialogFragment dialog = EditEmailDialogFragment.newInstance(currentEmail);
        dialog.setEditEmailDialogListener(this);
        dialog.show(getSupportFragmentManager(), "EditEmailDialog");
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialogFragment dialog = new ChangePasswordDialogFragment();
        dialog.setChangePasswordDialogListener(this);
        dialog.show(getSupportFragmentManager(), "ChangePasswordDialog");
    }

    private void showSignOutDialog() {
        SignOutDialogFragment dialog = new SignOutDialogFragment();
        dialog.setSignOutDialogListener(this);
        dialog.show(getSupportFragmentManager(), "SignOutDialog");
    }

    // --- Dialog Callback Implementations ---

    @Override
    public void onNameSave(String newName) {
        if (!newName.isEmpty()) {
            nameEditText.setText(newName);
            Toast.makeText(this, "Name updated to: " + newName, Toast.LENGTH_SHORT).show();
            // Save to persistent storage (e.g., SharedPreferences, database)
        } else {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPhoneNumberSave(String newPhoneNumber) {
        if (!newPhoneNumber.isEmpty()) {
            phoneNumberEditText.setText(newPhoneNumber);
            Toast.makeText(this, "Phone Number updated to: " + newPhoneNumber, Toast.LENGTH_SHORT).show();
            // Save to persistent storage
        } else {
            Toast.makeText(this, "Phone Number cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEmailSave(String newEmail) {
        if (!newEmail.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            emailEditText.setText(newEmail);
            Toast.makeText(this, "Email updated to: " + newEmail, Toast.LENGTH_SHORT).show();
            // Save to persistent storage
        } else {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChangePassword(String currentPassword, String newPassword, String confirmNewPassword) {
        // In a real app, you'd verify currentPassword with your backend/auth service
        // and then update the password.
        if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "New password fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            // Simulate successful password change
            Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            // You might want to clear the passwordEditText or keep it showing "**********"
        }
    }

    @Override
    public void onSignOutConfirm() {
        Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
        // Implement your actual sign-out logic here (clear session, navigate to login)
        finish(); // For demonstration, simply finishes the current activity
    }
}