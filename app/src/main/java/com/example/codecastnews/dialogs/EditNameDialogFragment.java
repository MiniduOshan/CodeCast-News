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

public class EditNameDialogFragment extends DialogFragment {

    public interface EditNameDialogListener {
        void onNameSave(String newName);
    }

    private EditNameDialogListener listener;
    private String currentName;

    public static EditNameDialogFragment newInstance(String currentName) {
        EditNameDialogFragment fragment = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putString("currentName", currentName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentName = getArguments().getString("currentName");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_name, null);

        EditText newNameEditText = view.findViewById(R.id.newNameEditText);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        if (currentName != null) {
            newNameEditText.setText(currentName);
            newNameEditText.setSelection(currentName.length());
        }

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            String newName = newNameEditText.getText().toString().trim();
            if (listener != null) {
                listener.onNameSave(newName);
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

    public void setEditNameDialogListener(EditNameDialogListener listener) {
        this.listener = listener;
    }
}