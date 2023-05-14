package com.mirea.kt.android2023.mycontactsapp.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mirea.kt.android2023.mycontactsapp.ContactInfoActivity;
import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.adapters.ContactAdapter;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ConfigRealm;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        Realm.init(view.getContext());

        FloatingActionButton addButton = view.findViewById(R.id.floatingActionButtonAddContact);

        RecyclerView contactsList = view.findViewById(R.id.rvContacts);
        ContactDatabaseOperations operations = new ContactDatabaseOperations(ConfigRealm.getRealmConfiguration());

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        contactsList.setLayoutManager(layoutManager);
        contactsList.setHasFixedSize(true);

        ContactAdapter contactAdapter = new ContactAdapter(view.getContext(), operations.getAllContacts());
        contactsList.setAdapter(contactAdapter);

        addButton.setOnClickListener(x -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
            View addNumberView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);

            EditText etName = addNumberView.findViewById(R.id.etDialogNameAddContact);
            EditText etPhoneNumber = addNumberView.findViewById(R.id.etDialogNumberAddContact);
            Spinner spinner = addNumberView.findViewById(R.id.spinnerAddContact);
            Button button = addNumberView.findViewById(R.id.buttonAddContact);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.phone_type,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            dialogBuilder.setView(addNumberView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            button.setOnClickListener(z -> {
                String name = etName.getText().toString();
                String number = etPhoneNumber.getText().toString();

                if (number.isEmpty() || name.isEmpty()) {
                    Toast.makeText(view.getContext(), "Все поля должны быть заполнены!", Toast.LENGTH_LONG).show();
                    return;
                }

                Contact contact = new Contact(name, "none");
                contact.setNumbers(new RealmList<>());

                operations.insertContact(contact);

                String type = spinner.getSelectedItem().toString();
                NumberType numberType;

                switch (type) {
                    case "ДОМАШНИЙ":
                        numberType = NumberType.HOME;
                        break;
                    case "РАБОЧИЙ":
                        numberType = NumberType.WORKER;
                        break;
                    case "СОТОВЫЙ":
                        numberType = NumberType.CELLULAR;
                        break;
                    default:
                        numberType = NumberType.NONE;
                        break;
                }

                PhoneNumber phoneNumber = new PhoneNumber(number, numberType);
                operations.addNumber(contact.getId(), phoneNumber);

                ContactAdapter newAdapter = new ContactAdapter(view.getContext(), operations.getAllContacts());
                contactsList.setAdapter(newAdapter);
                newAdapter.notifyDataSetChanged();

                dialog.cancel();

                Toast.makeText(view.getContext(), "Контакт добавлен!", Toast.LENGTH_LONG).show();
            });
        });

        return view;
    }
}