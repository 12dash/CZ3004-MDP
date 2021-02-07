package com.example.mdpbluetoothtest;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ReconfigureFragment extends DialogFragment {
    private static final String TAG = "ReconfigureFragment";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button saveBtn, cancelBtn;
    EditText f1ValueEditText, f2ValueEditText;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_reconfigure, container, false);
        super.onCreate(savedInstanceState);
        getDialog().setTitle("Reconfiguration");

        saveBtn = rootView.findViewById(R.id.saveBtn);
        cancelBtn = rootView.findViewById(R.id.cancelReconfigureBtn);
        f1ValueEditText = rootView.findViewById(R.id.f1ValueEditText);
        f2ValueEditText = rootView.findViewById(R.id.f2ValueEditText);

        sharedPreferences = getActivity().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.contains("F1")) {
            f1ValueEditText.setText(sharedPreferences.getString("F1", ""));
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }
        if (sharedPreferences.contains("F2")) {
            f2ValueEditText.setText(sharedPreferences.getString("F2", ""));
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("F1", f1ValueEditText.getText().toString());
                editor.putString("F2", f2ValueEditText.getText().toString());
                editor.commit();

                Toast.makeText(getActivity(), "Saving F1 / F2 values", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return rootView;
    }

}

