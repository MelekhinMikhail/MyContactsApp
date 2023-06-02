package com.mirea.kt.android2023.mycontactsapp.adapters;

import android.content.Context;
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

import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;
import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;

import java.util.List;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.ViewHolder> {
    private List<PhoneNumber> numbers;
    private OnNumberClickListener onNumberClickListener;

    public NumberAdapter(List<PhoneNumber> numbers, OnNumberClickListener onNumberClickListener) {
        this.numbers = numbers;
        this.onNumberClickListener = onNumberClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.number_list_item, parent, false);

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
            onNumberClickListener.onNumberClick(number, holder.getAdapterPosition(), holder.itemView);
        });

        holder.itemView.setOnLongClickListener(x -> {
            onNumberClickListener.onNumberLongClick(number, holder.getAdapterPosition(), holder.itemView);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    public interface OnNumberClickListener {
        void onNumberClick(PhoneNumber phoneNumber, int position, View itemView);
        void onNumberLongClick(PhoneNumber phoneNumber, int position, View itemView);
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
