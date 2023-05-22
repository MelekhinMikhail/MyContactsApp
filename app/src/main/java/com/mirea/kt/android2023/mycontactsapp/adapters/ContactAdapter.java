package com.mirea.kt.android2023.mycontactsapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mirea.kt.android2023.mycontactsapp.ContactInfoActivity;
import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.fragments.ContactInfoFragment;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.realm.ConfigRealm;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private static Context context;
    public static List<Contact> contacts;
    private ContactDatabaseOperations operations;

    public ContactAdapter(Context context, List<Contact> contacts) {
        ContactAdapter.context = context;
        ContactAdapter.contacts = contacts;
        operations = new ContactDatabaseOperations(ConfigRealm.getRealmConfiguration());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.contact_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        int color = (contact.isFavorite()) ? R.color.yellow : R.color.grey;

        holder.contactImageView.setImageResource(R.drawable.ic_person);
        holder.tvName.setText(contact.getName());
        holder.favoriteImageView.setColorFilter(
                ContextCompat.getColor(context, color),
                PorterDuff.Mode.SRC_IN
        );
//        Glide.with(context).load(contact.getImagePath()).circleCrop() // Отрисовка фотографии пользователя с помощью библиотеки Glide
//                .error(R.drawable.ic_person)
//                .placeholder(R.drawable.ic_person).into(holder.contactImageView);
//        https://habr.com/ru/articles/705064/

        PopupMenu popupMenu = new PopupMenu(context, holder.moreImageView);

        popupMenu.setGravity(Gravity.END);
        popupMenu.getMenu().add(0, 1, Menu.NONE, "Удалить");

        popupMenu.setOnMenuItemClickListener(x -> {
            switch (x.getItemId()) {
                case 1:

                    operations.deleteContact(contacts.get(position).getId());
                    contacts = operations.getAllContacts();
                    notifyDataSetChanged();

                    Toast.makeText(context, "Редактируем", Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        });

        holder.moreImageView.setOnClickListener(x -> {
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView contactImageView;
        ImageView favoriteImageView;
        ImageView moreImageView;
        TextView tvName;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactImageView = itemView.findViewById(R.id.imageContactItem);
            favoriteImageView = itemView.findViewById(R.id.favoritesImageView);
            moreImageView = itemView.findViewById(R.id.moreImageViewListContact);
            tvName = itemView.findViewById(R.id.tvItemContactName);
            mainLayout = itemView.findViewById(R.id.mainContentListItem);
            ContactDatabaseOperations operations = new ContactDatabaseOperations(ConfigRealm.getRealmConfiguration());




            mainLayout.setOnClickListener(x -> {
                Toast.makeText(itemView.getContext(), "Переход", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(context, ContactInfoActivity.class);
                intent.putExtra("id", String.valueOf(contacts.get(getAdapterPosition()).getId()));
                context.startActivity(intent);
            });

            favoriteImageView.setOnClickListener(x -> {
                Contact contact = contacts.get(getAdapterPosition());
                contact.setFavorite(!contact.isFavorite());
                int color = (contact.isFavorite()) ? R.color.yellow : R.color.grey;
                operations.updateContact(contact.getId(), contact);
                favoriteImageView.setColorFilter(
                        ContextCompat.getColor(context, color),
                        PorterDuff.Mode.SRC_IN
                );
            });
        }
    }
}
