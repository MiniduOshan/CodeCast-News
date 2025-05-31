package com.example.codecastnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull; // <--- ADD THIS IMPORT

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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
    private ImageView profileImage;
    private ConstraintLayout editPhotoContainer;
    private Spinner countryCodeSpinner;
    private String selectedCountryCode = "+1"; // Default country code
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<String> mGetContent;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        prefs = getSharedPreferences("UserProfilePrefs", MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signOutButton = findViewById(R.id.signOutButton);
        backArrow = findViewById(R.id.backArrow);
        profileImage = findViewById(R.id.profileImage);
        editPhotoContainer = findViewById(R.id.editPhotoContainer);
        countryCodeSpinner = findViewById(R.id.countryCodeSpinner);

        String[] countryCodes = {"+1", "+44", "+91", "+94", "+86", "+33", "+61", "+81", "+49", "+27"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSpinner.setAdapter(adapter);
        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountryCode = countryCodes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCountryCode = "+1"; // Default
            }
        });

        // Restore saved data from SharedPreferences
        String savedName = prefs.getString("name", "");
        nameEditText.setText(savedName);

        String savedEmail = prefs.getString("email", "");
        emailEditText.setText(savedEmail);

        String savedPhoneNumber = prefs.getString("phoneNumber", "");
        String savedCountryCode = prefs.getString("countryCode", "+1");
        phoneNumberEditText.setText(savedPhoneNumber);
        selectedCountryCode = savedCountryCode;
        int spinnerPosition = adapter.getPosition(savedCountryCode);
        if (spinnerPosition >= 0) {
            countryCodeSpinner.setSelection(spinnerPosition);
        } else {
            countryCodeSpinner.setSelection(0);
        }

        nameEditText.setOnClickListener(v -> showEditNameDialog());
        phoneNumberEditText.setOnClickListener(v -> showEditPhoneNumberDialog());
        emailEditText.setOnClickListener(v -> showEditEmailDialog());
        passwordEditText.setOnClickListener(v -> showChangePasswordDialog());
        signOutButton.setOnClickListener(v -> showSignOutDialog());
        editPhotoContainer.setOnClickListener(v -> pickImage());
        backArrow.setOnClickListener(v -> onBackPressed());

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        profileImage.setImageURI(uri);
                        Toast.makeText(UserProfile.this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserProfile.this, "No image selected.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditNameDialog() {
        String currentName = nameEditText.getText().toString();
        EditNameDialogFragment dialog = EditNameDialogFragment.newInstance(currentName);
        dialog.setEditNameDialogListener(this);
        dialog.show(getSupportFragmentManager(), "EditNameDialog");
    }

    private void showEditPhoneNumberDialog() {
        String currentPhoneNumber = phoneNumberEditText.getText().toString();
        EditPhoneNumberDialogFragment dialog = EditPhoneNumberDialogFragment.newInstance(currentPhoneNumber, selectedCountryCode);
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

    private void pickImage() {
        mGetContent.launch("image/*");
    }

    @Override
    public void onNameSave(String newName) {
        if (!newName.isEmpty()) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Update successful in Firebase
                                    nameEditText.setText(newName);
                                    // Also save to SharedPreferences for immediate local consistency
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("name", newName);
                                    editor.apply();
                                    Toast.makeText(UserProfile.this, "Name updated in Firebase and locally!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserProfile.this, "Failed to update name in Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "No authenticated user to update name.", Toast.LENGTH_SHORT).show();
                nameEditText.setText(newName); // Still update locally if no Firebase user
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("name", newName);
                editor.apply();
            }
        } else {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPhoneNumberSave(String newPhoneNumber, String countryCode) {
        if (!newPhoneNumber.isEmpty()) {
            phoneNumberEditText.setText(newPhoneNumber);
            selectedCountryCode = countryCode != null ? countryCode : "+1";
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) countryCodeSpinner.getAdapter();
            if (adapter != null) {
                int spinnerPosition = adapter.getPosition(selectedCountryCode);
                if (spinnerPosition >= 0) {
                    countryCodeSpinner.setSelection(spinnerPosition);
                } else {
                    countryCodeSpinner.setSelection(0);
                }
            }
            // Save to SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("phoneNumber", newPhoneNumber);
            editor.putString("countryCode", selectedCountryCode);
            editor.apply();
            Toast.makeText(this, "Phone Number updated to: " + selectedCountryCode + newPhoneNumber, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Phone Number cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEmailSave(String newEmail) {
        if (!newEmail.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.updateEmail(newEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Email updated in Firebase
                                    emailEditText.setText(newEmail);
                                    // Also save to SharedPreferences for immediate local consistency
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("email", newEmail);
                                    editor.apply();
                                    Toast.makeText(UserProfile.this, "Email updated in Firebase and locally!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserProfile.this, "Failed to update email in Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "No authenticated user to update email.", Toast.LENGTH_SHORT).show();
                emailEditText.setText(newEmail); // Still update locally if no Firebase user
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", newEmail);
                editor.apply();
            }
        } else {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChangePassword(String currentPassword, String newPassword, String confirmNewPassword) {
        if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "New password fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfile.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserProfile.this, "Failed to change password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "No authenticated user to change password.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSignOutConfirm() {
        Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
        if (mAuth != null) {
            mAuth.signOut();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(UserProfile.this, SignInScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}