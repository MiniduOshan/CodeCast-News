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

public class EditPhoneNumberDialogFragment extends DialogFragment {

    public interface EditPhoneNumberDialogListener {
        void onPhoneNumberSave(String newPhoneNumber);
    }

    private EditPhoneNumberDialogListener listener;
    private String currentPhoneNumber;

    public static EditPhoneNumberDialogFragment newInstance(String currentPhoneNumber) {
        EditPhoneNumberDialogFragment fragment = new EditPhoneNumberDialogFragment();
        Bundle args = new Bundle();
        args.putString("currentPhoneNumber", currentPhoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPhoneNumber = getArguments().getString("currentPhoneNumber");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_phone_number, null);

        EditText newPhoneNumberEditText = view.findViewById(R.id.newPhoneNumberEditText);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        if (currentPhoneNumber != null) {
            newPhoneNumberEditText.setText(currentPhoneNumber);
            newPhoneNumberEditText.setSelection(currentPhoneNumber.length());
        }

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            String newPhoneNumber = newPhoneNumberEditText.getText().toString().trim();
            if (listener != null) {
                listener.onPhoneNumberSave(newPhoneNumber);
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

    public void setEditPhoneNumberDialogListener(EditPhoneNumberDialogListener listener) {
        this.listener = listener;
    }
}