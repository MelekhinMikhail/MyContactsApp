package com.mirea.kt.android2023.mycontactsapp.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mirea.kt.android2023.mycontactsapp.ContactInfoActivity;
import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.adapters.ContactAdapter;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.RealmList;

public class FavoriteContactsFragment extends Fragment implements ContactAdapter.OnContactClickListener {

    private RecyclerView recyclerView;
    private View view;
    private ContactDatabaseOperations databaseOperations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_favorite_contacts, container, false);

        recyclerView = view.findViewById(R.id.favoriteRecyclerView);
        databaseOperations = new ContactDatabaseOperations();

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        List<Contact> contacts = new ArrayList<>(databaseOperations.getAllContacts());
        contacts = contacts.stream().filter(Contact::isFavorite).collect(Collectors.toList());

        ContactAdapter contactAdapter = new ContactAdapter(contacts, this, view.getContext().getFilesDir().toString());
        recyclerView.setAdapter(contactAdapter);

        return view;
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
}