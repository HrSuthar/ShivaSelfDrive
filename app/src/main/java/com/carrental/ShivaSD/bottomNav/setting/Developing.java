package com.carrental.ShivaSD.bottomNav.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carrental.ShivaSD.R;

public class Developing extends Fragment {
    TextView devText;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_developing, container, false);

        devText = root.findViewById(R.id.devTextView);
        devText.setText("Sorry! We are Developing");
        return root;

    }
}