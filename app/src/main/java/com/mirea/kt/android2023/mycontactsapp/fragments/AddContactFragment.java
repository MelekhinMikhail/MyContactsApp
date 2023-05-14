package com.mirea.kt.android2023.mycontactsapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ConfigRealm;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment {

    public AddContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);

        EditText etContactName = view.findViewById(R.id.etAddContactName);
        EditText etContactNumber = view.findViewById(R.id.etAddContactNumber);
        Button button = view.findViewById(R.id.buttonAddContactSubmit);
        TextView tvErrors = view.findViewById(R.id.tvAddContactErrors);
        ContactDatabaseOperations operations = new ContactDatabaseOperations(ConfigRealm.getRealmConfiguration());

        button.setOnClickListener(x -> {
            String name = etContactName.getText().toString();
            String number = etContactNumber.getText().toString();

            if (name.isEmpty() || number.isEmpty()) {
                tvErrors.setText("Все поля должны быть заполнены!");
                return;
            }

            Contact contact = new Contact(name, "none");

            PhoneNumber phoneNumber = new PhoneNumber(number, NumberType.NONE);

            RealmList<PhoneNumber> realmList = new RealmList<>(phoneNumber);

            contact.setNumbers(realmList);

            operations.insertContact(contact);

            Toast.makeText(view.getContext(), "Контакт добавлен!", Toast.LENGTH_LONG).show();

            etContactName.setText("");
            etContactNumber.setText("");
        });

        return view;
    }
}