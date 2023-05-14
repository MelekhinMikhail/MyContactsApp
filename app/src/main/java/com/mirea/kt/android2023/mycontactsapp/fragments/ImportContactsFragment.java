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
public class ImportContactsFragment extends Fragment {


    public ImportContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_import_contacts, container, false);
    }
}