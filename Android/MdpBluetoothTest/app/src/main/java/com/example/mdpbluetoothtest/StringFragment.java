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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class StringFragment extends DialogFragment {
    private static final String TAG = "StringFragment";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView imageLabel, p1Label, p2Label;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_string, container, false);
        super.onCreate(savedInstanceState);
        getDialog().setTitle("Strings");

        imageLabel = rootView.findViewById(R.id.imageLabel);
        p1Label = rootView.findViewById(R.id.p1Label);
        p2Label = rootView.findViewById(R.id.p2Label);

        sharedPreferences = getActivity().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.contains("IMAGE")) {
            imageLabel.setText(sharedPreferences.getString("IMAGE", ""));
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }
        if (sharedPreferences.contains("P1")) {
            p1Label.setText(sharedPreferences.getString("P1", ""));
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        if (sharedPreferences.contains("P2")) {
            p2Label.setText(sharedPreferences.getString("P2", ""));
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }


        return rootView;
    }

}

