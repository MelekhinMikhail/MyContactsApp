package com.mirea.kt.android2023.mycontactsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mirea.kt.android2023.mycontactsapp.adapters.NumberAdapter;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;
import com.mirea.kt.android2023.mycontactsapp.utils.CircularTransformation;
import com.mirea.kt.android2023.mycontactsapp.utils.ImageManager;
import com.mirea.kt.android2023.mycontactsapp.utils.Validator;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class ContactInfoActivity extends AppCompatActivity implements NumberAdapter.OnNumberClickListener {

    private ImageView back;
    private ImageView more;
    private ImageView imageContact;
    private TextView toolbarTitle;
    private TextView contactName;
    private Button addNumberButton;
    private Button changeNameButton;
    private RecyclerView recyclerView;
    private ContactDatabaseOperations operations;
    private Contact contact;
    private ImageManager imageManager;
    static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        back = findViewById(R.id.buttonBack);
        more = findViewById(R.id.imageViewContactInfoMore);
        imageContact = findViewById(R.id.imageViewContactInfo);
        toolbarTitle = findViewById(R.id.tvToolBarTitleContactInfo);
        contactName = findViewById(R.id.tvContactInfoName);
        addNumberButton = findViewById(R.id.buttonAddNumber);
        changeNameButton = findViewById(R.id.buttonUpdateName);
        operations = new ContactDatabaseOperations();
        imageManager = new ImageManager(getFilesDir().toString());

        contact = operations.getContact(Long.parseLong(getIntent().getStringExtra("id")));
        toolbarTitle.setText(contact.getName());
        contactName.setText(contact.getName());

        if (contact.getImagePath() != null && !contact.getImagePath().equalsIgnoreCase("none") && !contact.getImagePath().isEmpty()) {
            if (contact.getImagePath().startsWith("http")) {
                Picasso.with(this)
                        .load(contact.getImagePath())
                        .transform(new CircularTransformation())
                        .into(imageContact);
            } else {
                imageContact.setImageBitmap(imageManager.getImage(contact.getImagePath()));
            }
        }

        recyclerView = findViewById(R.id.rvNumbers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        NumberAdapter numberAdapter = new NumberAdapter(contact.getNumbers(), this);
        recyclerView.setAdapter(numberAdapter);

        back.setOnClickListener(x -> finish());

        addNumberButton.setOnClickListener(x -> addNumber());

        imageContact.setOnClickListener(x -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        });

        more.setOnClickListener(x -> showMoreMenu());

        changeNameButton.setOnClickListener(x -> changeName());
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
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        Picasso.with(this)
                                .load(selectedImage)
                                .transform(new CircularTransformation())
                                .into(imageView);

                        operations.updateImage(contact.getId(), imageManager.saveImage(bitmap, contact.getId()));
                    Log.d("app_tag", selectedImage.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    @Override
    public void onNumberClick(PhoneNumber phoneNumber, int position, View itemView) {
        PopupMenu popupMenu = new PopupMenu(this, itemView);

        popupMenu.getMenu().add(0, 1, Menu.NONE, "Редактировать");
        popupMenu.getMenu().add(0, 2, Menu.NONE, "Удалить");

        popupMenu.setOnMenuItemClickListener(z -> {
            switch (z.getItemId()) {
                case 1:
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View addNumberView = inflater.inflate(R.layout.dialog_update_number, null);

                    EditText etPhoneNumber = addNumberView.findViewById(R.id.etDialogNumberUpdateNumber);
                    Spinner spinner = addNumberView.findViewById(R.id.spinnerUpdateNumber);
                    Button button = addNumberView.findViewById(R.id.buttonUpdateNumber);

                    etPhoneNumber.setText(phoneNumber.getNumber());

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.phone_type,
                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

                    spinner.setAdapter(adapter);

                    int selection;
                    switch (phoneNumber.getType()) {
                        case "HOME":
                            selection = 1;
                            break;
                        case "WORKER":
                            selection = 2;
                            break;
                        case "CELLULAR":
                            selection = 3;
                            break;
                        default:
                            selection = 0;
                            break;
                    }
                    spinner.setSelection(selection);

                    dialogBuilder.setView(addNumberView);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    button.setOnClickListener(o -> {
                        String number = etPhoneNumber.getText().toString();

                        if (number.isEmpty()) {
                            Toast.makeText(this, "Номер должен быть заполнен!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        number = Validator.validateNumber(number);
                        if (number == null) {
                            Toast.makeText(this, "Неверный формат номера!", Toast.LENGTH_LONG).show();
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

                        PhoneNumber newPhoneNumber = new PhoneNumber(number, numberType);
                        newPhoneNumber.setId(phoneNumber.getId());
                        newPhoneNumber.setDate(phoneNumber.getDate());

                        operations.deleteNumber(contact.getId(), phoneNumber);
                        operations.addNumber(contact.getId(), newPhoneNumber, position);

                        recyclerView.setAdapter(new NumberAdapter(new ArrayList<>(operations.getContact(contact.getId()).getNumbers()), this));

                        dialog.cancel();

                        Toast.makeText(this, "Номер изменен", Toast.LENGTH_LONG).show();
                    });
                    break;
                case 2:
                    operations.deleteNumber(contact.getId(), phoneNumber);
                    recyclerView.setAdapter(new NumberAdapter(new ArrayList<>(operations.getContact(contact.getId()).getNumbers()), this));
                    Toast.makeText(itemView.getContext(), "Номер удален", Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        });


        popupMenu.show();
    }

    @Override
    public void onNumberLongClick(PhoneNumber phoneNumber, int position, View itemView) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber.getNumber()));
        startActivity(intent);

    }

    private void addNumber() {
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

            number = Validator.validateNumber(number);
            if (number == null) {
                Toast.makeText(this, "Неверный формат номера!", Toast.LENGTH_LONG).show();
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

            recyclerView.setAdapter(new NumberAdapter(new ArrayList<>(operations.getContact(contact.getId()).getNumbers()), this));

            dialog.cancel();

            Toast.makeText(this, "Номер добавлен!", Toast.LENGTH_LONG).show();
        });
    }

    private void showMoreMenu() {
        PopupMenu popupMenu = new PopupMenu(this, more);

        popupMenu.getMenu().add(0, 1, Menu.NONE, "Поделиться");
        popupMenu.getMenu().add(0, 2, Menu.NONE, "Удалить");

        popupMenu.setOnMenuItemClickListener(z -> {
            switch (z.getItemId()) {
                case 1:
                    StringBuilder builder = new StringBuilder();
                    builder.append(contact.getName() + "\n");
                    if (!contact.getNumbers().isEmpty()) {
                        contact.getNumbers().stream()
                                .map(x -> builder.append(x.getNumber() + "\n"));
                    }

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                    break;
                case 2:
                    operations.deleteContact(contact.getId());
                    finish();
                    break;
            }
            return true;
        });


        popupMenu.show();
    }

    private void changeName() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ContactInfoActivity.this);
        View updateNameView = getLayoutInflater().inflate(R.layout.dialog_update_name, null);

        EditText editTextUpdateName = updateNameView.findViewById(R.id.editTextDialogUpdateName);
        Button button = updateNameView.findViewById(R.id.buttonDialogUpdateName);

        editTextUpdateName.setText(contact.getName());

        dialogBuilder.setView(updateNameView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        button.setOnClickListener(x -> {
            String name = editTextUpdateName.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Поле должно быть заполнено!", Toast.LENGTH_LONG).show();
                return;
            }

            operations.updateName(contact.getId(), name);
            Toast.makeText(this, "Успешно!", Toast.LENGTH_LONG).show();

            contactName.setText(name);
            toolbarTitle.setText(name);

            dialog.cancel();
        });
    }
}