package com.mirea.kt.android2023.mycontactsapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mirea.kt.android2023.mycontactsapp.LoginActivity;
import com.mirea.kt.android2023.mycontactsapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogoutFragment extends Fragment {

    public LogoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        Button button = view.findViewById(R.id.buttonLogoutFragment);
        button.setOnClickListener(x -> {
            SharedPreferences preferences = view.getContext().getSharedPreferences(
                    PreferenceManager.getDefaultSharedPreferencesName(view.getContext().getApplicationContext()), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first_start", true);
            editor.apply();
            startActivity(new Intent(view.getContext(), LoginActivity.class));
        });

        return view;
    }
}