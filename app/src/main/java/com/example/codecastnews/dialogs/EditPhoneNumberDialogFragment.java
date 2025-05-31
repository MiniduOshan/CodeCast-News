package com.example.codecastnews.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.codecastnews.R;

public class EditPhoneNumberDialogFragment extends DialogFragment {

    public interface EditPhoneNumberDialogListener {
        void onPhoneNumberSave(String newPhoneNumber, String countryCode);
    }

    private EditPhoneNumberDialogListener listener;
    private String currentPhoneNumber;
    private String currentCountryCode;

    public static EditPhoneNumberDialogFragment newInstance(String currentPhoneNumber, String currentCountryCode) {
        EditPhoneNumberDialogFragment fragment = new EditPhoneNumberDialogFragment();
        Bundle args = new Bundle();
        args.putString("currentPhoneNumber", currentPhoneNumber);
        args.putString("currentCountryCode", currentCountryCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPhoneNumber = getArguments().getString("currentPhoneNumber");
            currentCountryCode = getArguments().getString("currentCountryCode", "+1");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_phone_number, null);

        Spinner countryCodeSpinner = view.findViewById(R.id.countryCodeSpinner);
        EditText newPhoneNumberEditText = view.findViewById(R.id.newPhoneNumberEditText);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        // Populate country code spinner
        String[] countryCodes = {"+1", "+44", "+91", "+94", "+86", "+33", "+61", "+81", "+49", "+27"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, countryCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSpinner.setAdapter(adapter);

        // Set current country code
        String codeToSet = currentCountryCode != null ? currentCountryCode : "+1";
        int spinnerPosition = adapter.getPosition(codeToSet);
        if (spinnerPosition >= 0) {
            countryCodeSpinner.setSelection(spinnerPosition);
        } else {
            countryCodeSpinner.setSelection(0); // Default to first item
        }

        if (currentPhoneNumber != null) {
            newPhoneNumberEditText.setText(currentPhoneNumber);
            newPhoneNumberEditText.setSelection(currentPhoneNumber.length());
        }

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            String newPhoneNumber = newPhoneNumberEditText.getText().toString().trim();
            String selectedCountryCode = countryCodeSpinner.getSelectedItem() != null ?
                    countryCodeSpinner.getSelectedItem().toString() : "+1";
            if (listener != null) {
                listener.onPhoneNumberSave(newPhoneNumber, selectedCountryCode);
            }
            dismiss();
        });

        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
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