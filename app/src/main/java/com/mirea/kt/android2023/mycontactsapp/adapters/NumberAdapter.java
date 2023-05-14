package com.mirea.kt.android2023.mycontactsapp.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mirea.kt.android2023.mycontactsapp.ContactInfoActivity;
import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ConfigRealm;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;

import java.util.List;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.ViewHolder> {

    private static Context context;
    private static Contact contact;
    static List<PhoneNumber> numbers;
    private ContactDatabaseOperations operations;

    public NumberAdapter(Context context, Contact contact, List<PhoneNumber> numbers) {
        NumberAdapter.context = context;
        NumberAdapter.contact = contact;
        NumberAdapter.numbers = numbers;
        operations = new ContactDatabaseOperations(ConfigRealm.getRealmConfiguration());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.number_list_item, parent, false);
        return new NumberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhoneNumber number = numbers.get(position);

        holder.number.setText(number.getNumber());
        switch (number.getType()) {
            case "HOME":
                holder.type.setText("Домашний");
                break;
            case "WORKER":
                holder.type.setText("Рабочий");
                break;
            case "CELLULAR":
                holder.type.setText("Мобильный");
                break;
            default:
                break;
        }

        holder.itemView.setOnClickListener(x -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.itemView);

            popupMenu.getMenu().add(0, 1, Menu.NONE, "Редактировать");
            popupMenu.getMenu().add(0, 2, Menu.NONE, "Удалить");

            popupMenu.setOnMenuItemClickListener(z -> {
                switch (z.getItemId()) {
                    case 1:
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        View addNumberView = inflater.inflate(R.layout.dialog_update_number, null);

                        EditText etPhoneNumber = addNumberView.findViewById(R.id.etDialogNumberUpdateNumber);
                        Spinner spinner = addNumberView.findViewById(R.id.spinnerUpdateNumber);
                        Button button = addNumberView.findViewById(R.id.buttonUpdateNumber);

                        etPhoneNumber.setText(numbers.get(position).getNumber());

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.phone_type,
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

                        spinner.setAdapter(adapter);

                        int selection;
                        switch (numbers.get(position).getType()) {
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
                            String phoneNumber = etPhoneNumber.getText().toString();

                            if (phoneNumber.isEmpty()) {
                                Toast.makeText(context, "Номер должен быть заполнен!", Toast.LENGTH_LONG).show();
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

                            PhoneNumber oldPhoneNumber = numbers.get(position);
                            PhoneNumber newPhoneNumber = new PhoneNumber(phoneNumber, numberType);
                            newPhoneNumber.setId(oldPhoneNumber.getId());
                            newPhoneNumber.setDate(oldPhoneNumber.getDate());

                            operations.deleteNumber(contact.getId(), oldPhoneNumber);
                            operations.addNumber(contact.getId(), newPhoneNumber, position);

                            notifyDataSetChanged();

                            dialog.cancel();

                            Toast.makeText(context, "Номер изменен", Toast.LENGTH_LONG).show();
                        });
                        break;
                    case 2:
                        operations.deleteNumber(contact.getId(), numbers.get(position));
                        notifyDataSetChanged();
                        Toast.makeText(holder.itemView.getContext(), "Номер удален", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            });

            holder.itemView.setOnClickListener(y -> {
                popupMenu.show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView number, type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            number = itemView.findViewById(R.id.tvContactNumberListItem);
            type = itemView.findViewById(R.id.tvTypeOfNumberListItem);
        }
    }
}
