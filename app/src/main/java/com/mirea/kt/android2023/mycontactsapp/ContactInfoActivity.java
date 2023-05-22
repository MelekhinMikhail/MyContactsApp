package com.mirea.kt.android2023.mycontactsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mirea.kt.android2023.mycontactsapp.adapters.ContactAdapter;
import com.mirea.kt.android2023.mycontactsapp.adapters.NumberAdapter;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ConfigRealm;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;
import com.mirea.kt.android2023.mycontactsapp.utils.CircularTransformation;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactInfoActivity extends AppCompatActivity {

    private ImageView back;
    private ImageView imageContact;
    private TextView toolbarTitle;
    private TextView contactName;
    private Button addNumberButton;
    private ContactDatabaseOperations operations;
    private Contact contact;
    static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        back = findViewById(R.id.buttonBack);
        imageContact = findViewById(R.id.imageViewContactInfo);
        toolbarTitle = findViewById(R.id.tvToolBarTitleContactInfo);
        contactName = findViewById(R.id.tvContactInfoName);
        addNumberButton = findViewById(R.id.buttonAddNumber);
        operations = new ContactDatabaseOperations(ConfigRealm.getRealmConfiguration());

        contact = operations.getContact(Long.parseLong(getIntent().getStringExtra("id")));
        toolbarTitle.setText(contact.getName());
        contactName.setText(contact.getName());

        if (contact.getImagePath() != null && !contact.getImagePath().equalsIgnoreCase("none")) {

//            Picasso.with(this)
//                    .load(Uri.parse(contact.getImagePath()))
//                    .transform(new CircularTransformation())
//                    .into(imageContact);
            imageContact.setImageURI(Uri.parse(contact.getImagePath()));
        }
//        List<PhoneNumber> numbers = new ArrayList<>();
//        numbers.add(new PhoneNumber("111", NumberType.HOME));
//        numbers.add(new PhoneNumber("112", NumberType.WORKER));
//        numbers.add(new PhoneNumber("113", NumberType.CELLULAR));
//        numbers.add(new PhoneNumber("114", NumberType.NONE));
//        numbers.add(new PhoneNumber("115", NumberType.HOME));
//        numbers.add(new PhoneNumber("116", NumberType.HOME));
//        numbers.add(new PhoneNumber("117", NumberType.CELLULAR));
//        numbers.add(new PhoneNumber("118", NumberType.HOME));
//        numbers.add(new PhoneNumber("119", NumberType.WORKER));

        RecyclerView contactsList = findViewById(R.id.rvNumbers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactsList.setLayoutManager(layoutManager);
        contactsList.setHasFixedSize(true);

        NumberAdapter numberAdapter = new NumberAdapter(this, contact, contact.getNumbers());
        contactsList.setAdapter(numberAdapter);

        back.setOnClickListener(x -> {
            finish();
        });

        addNumberButton.setOnClickListener(x -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ContactInfoActivity.this);
            View addNumberView = getLayoutInflater().inflate(R.layout.dialog_add_number, null);

            EditText etPhoneNumber = addNumberView.findViewById(R.id.etDialogNumberAddNumber);
            Spinner spinner = addNumberView.findViewById(R.id.spinnerAddNumber);
            Button button = addNumberView.findViewById(R.id.buttonAddNumber);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.phone_type,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            dialogBuilder.setView(addNumberView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            button.setOnClickListener(z -> {
                String number = etPhoneNumber.getText().toString();

                if (number.isEmpty()) {
                    Toast.makeText(this, "Номер должен быть заполнен!", Toast.LENGTH_LONG).show();
                    return;
                }

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

                numberAdapter.notifyDataSetChanged();

                dialog.cancel();

                Toast.makeText(this, "Номер добавлен!", Toast.LENGTH_LONG).show();
            });


        });

        imageContact.setOnClickListener(x -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        ImageView imageView = (ImageView) findViewById(R.id.imageViewContactInfo);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage).;
                        Picasso.with(this)
                                .load(selectedImage)
                                .transform(new CircularTransformation())
                                .into(imageView);

                        operations.updateImage(contact.getId(), selectedImage.getPath());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

//                    imageView.setImageBitmap(bitmap);
                }
        }
    }
}