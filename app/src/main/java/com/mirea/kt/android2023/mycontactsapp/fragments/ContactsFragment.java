package com.mirea.kt.android2023.mycontactsapp.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mirea.kt.android2023.mycontactsapp.ContactInfoActivity;
import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.adapters.ContactAdapter;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;
import com.mirea.kt.android2023.mycontactsapp.utils.Validator;

import io.realm.Realm;
import io.realm.RealmList;

public class ContactsFragment extends Fragment implements ContactAdapter.OnContactClickListener {

    private ContactDatabaseOperations databaseOperations;
    private View view;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        Realm.init(view.getContext());

        FloatingActionButton addButton = view.findViewById(R.id.floatingActionButtonAddContact);

        recyclerView = view.findViewById(R.id.rvContacts);
        databaseOperations = new ContactDatabaseOperations();

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        ContactAdapter contactAdapter = new ContactAdapter(databaseOperations.getAllContacts(), this, view.getContext().getFilesDir().toString());
        recyclerView.setAdapter(contactAdapter);

        addButton.setOnClickListener(x -> addContact());

        return view;
    }

    private void addContact() {

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

            number = Validator.validateNumber(number);
            if (number == null) {
                Toast.makeText(view.getContext(), "Неверный формат номера!", Toast.LENGTH_LONG).show();
                return;
            }

            Contact contact = new Contact(name, "none");
            contact.setNumbers(new RealmList<>());

            databaseOperations.insertContact(contact);

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
            databaseOperations.addNumber(contact.getId(), phoneNumber);

            ContactAdapter newAdapter = new ContactAdapter(databaseOperations.getAllContacts(), this, view.getContext().getFilesDir().toString());
            recyclerView.setAdapter(newAdapter);

            dialog.cancel();

            Toast.makeText(view.getContext(), "Контакт добавлен!", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onContactClick(Contact contact, int position) {
        Intent intent = new Intent(view.getContext(), ContactInfoActivity.class);
        intent.putExtra("id", String.valueOf(contact.getId()));
        view.getContext().startActivity(intent);
    }

    @Override
    public void onContactPopupMenuClick(Contact contact, int position, View viewItem) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), viewItem);

        popupMenu.setGravity(Gravity.END);
        popupMenu.getMenu().add(0, 1, Menu.NONE, "Удалить");

        popupMenu.setOnMenuItemClickListener(x -> {
            switch (x.getItemId()) {
                case 1:

                    databaseOperations.deleteContact(contact.getId());
                    recyclerView.setAdapter(new ContactAdapter(databaseOperations.getAllContacts(), this, view.getContext().getFilesDir().toString()));

                    Toast.makeText(view.getContext(), "Контакт удален", Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        });

        popupMenu.show();
    }

    @Override
    public void onContactFavoriteClick(Contact contact, int position, ImageView imageView) {
        contact.setFavorite(!contact.isFavorite());
        int color = (contact.isFavorite()) ? R.color.yellow : R.color.grey;
        databaseOperations.updateContact(contact.getId(), contact);
        imageView.setColorFilter(
                ContextCompat.getColor(view.getContext(), color),
                PorterDuff.Mode.SRC_IN
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.setAdapter(new ContactAdapter(databaseOperations.getAllContacts(), this, view.getContext().getFilesDir().toString()));
    }
}