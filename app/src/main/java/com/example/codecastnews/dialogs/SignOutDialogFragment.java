package com.example.codecastnews.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color; // Added import
import android.graphics.drawable.ColorDrawable; // Added import
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.codecastnews.R;

public class SignOutDialogFragment extends DialogFragment {

    public interface SignOutDialogListener {
        void onSignOutConfirm();
    }

    private SignOutDialogListener listener;

    public void setSignOutDialogListener(SignOutDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sign_out, null);

        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button yesButton = view.findViewById(R.id.yesButton);

        cancelButton.setOnClickListener(v -> dismiss());

        yesButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSignOutConfirm();
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
}