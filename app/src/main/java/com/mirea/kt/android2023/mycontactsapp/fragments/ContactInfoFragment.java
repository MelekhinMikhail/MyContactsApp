package com.mirea.kt.android2023.mycontactsapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mirea.kt.android2023.mycontactsapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactInfoFragment extends Fragment {

    public ContactInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_info, container, false);
    }
}