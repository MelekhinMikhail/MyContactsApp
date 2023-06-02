package com.mirea.kt.android2023.mycontactsapp.fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;
import com.mirea.kt.android2023.mycontactsapp.utils.ImageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.RealmList;

public class ImportContactsFragment extends Fragment {

    private View view;
    private Button button;
    private ContactDatabaseOperations databaseOperations;
    private ImageManager imageManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_import_contacts, container, false);
        databaseOperations = new ContactDatabaseOperations();
        imageManager = new ImageManager(view.getContext().getFilesDir().toString());

        button = view.findViewById(R.id.buttonImport);

        button.setOnClickListener(x -> {
            if (!checkPermission()) {
                Toast.makeText(view.getContext(), "Необходимо разрешить доступ!", Toast.LENGTH_LONG).show();
                return;
            }
            importContacts();
            Toast.makeText(view.getContext(), "Контакты успешно импортированы!", Toast.LENGTH_LONG).show();
        });

        return view;
    }

    private void importContacts() {
        Cursor cursor = view.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            Bitmap bitmap = null;
            if (imageUri != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), Uri.parse(imageUri));
                } catch (IOException e) {
                    Log.e("app_tag", e.getMessage());
                }
            }

            NumberType numberType;
            switch (type) {
                case "1":
                    numberType = NumberType.HOME;
                    break;
                case "2":
                    numberType = NumberType.CELLULAR;
                    break;
                case "3":
                    numberType = NumberType.WORKER;
                    break;
                default:
                    numberType = NumberType.NONE;
            }

            if (databaseOperations.nameIsPresent(name)) {
                List<Contact> contacts = databaseOperations.getAllContacts().stream()
                        .filter(x -> x.getName().equals(name))
                        .collect(Collectors.toList());

                for (Contact contact : contacts) {
                    if (!contact.getNumbers().isEmpty()) {
                        if (contact.getNumbers().stream().noneMatch(x -> x.getNumber().equals(number))) {
                            databaseOperations.addNumber(contact.getId(), new PhoneNumber(number, numberType));
                        }
                    } else {
                        databaseOperations.addNumber(contact.getId(), new PhoneNumber(number, numberType));
                    }
                }
            } else {
                Contact contact = new Contact(name, "none");
                contact.setNumbers(new RealmList<>(new PhoneNumber(number, numberType)));
                if (bitmap != null) {
                    contact.setImagePath(imageManager.saveImage(bitmap, contact.getId()));
                }

                databaseOperations.insertContact(contact);
            }
        }

        cursor.close();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(view.getContext(), "android.permission.READ_CONTACTS")
                == PackageManager.PERMISSION_GRANTED;
    }
}