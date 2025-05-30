package com.example.codecastnews.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color; // Added import
import android.graphics.drawable.ColorDrawable; // Added import
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.codecastnews.R;

public class ChangePasswordDialogFragment extends DialogFragment {

    public interface ChangePasswordDialogListener {
        void onChangePassword(String currentPassword, String newPassword, String confirmNewPassword);
    }

    private ChangePasswordDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        EditText currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText);
        EditText newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        EditText confirmNewPasswordEditText = view.findViewById(R.id.confirmNewPasswordEditText);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

            if (listener != null) {
                listener.onChangePassword(currentPassword, newPassword, confirmNewPassword);
            }
            dismiss();
        });

        builder.setView(view);

        // --- START FIX FOR ROUNDED CORNERS ---
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
        // --- END FIX FOR ROUNDED CORNERS ---
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    getResources().getDimensionPixelSize(R.dimen.dialog_width),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    public void setChangePasswordDialogListener(ChangePasswordDialogListener listener) {
        this.listener = listener;
    }
}