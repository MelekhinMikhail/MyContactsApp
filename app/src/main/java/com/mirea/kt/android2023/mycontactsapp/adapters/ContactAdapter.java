package com.mirea.kt.android2023.mycontactsapp.adapters;

import android.content.Intent;
import android.graphics.PorterDuff;
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
import androidx.recyclerview.widget.RecyclerView;

import com.mirea.kt.android2023.mycontactsapp.ContactInfoActivity;
import com.mirea.kt.android2023.mycontactsapp.R;
import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.realm.ContactDatabaseOperations;
import com.mirea.kt.android2023.mycontactsapp.utils.CircularTransformation;
import com.mirea.kt.android2023.mycontactsapp.utils.ImageManager;
import com.squareup.picasso.Picasso;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    public List<Contact> contacts;
    private OnContactClickListener onContactClickListener;
    private ImageManager imageManager;

    public ContactAdapter(List<Contact> contacts, OnContactClickListener onContactClickListener, String filesDir) {
        this.contacts = contacts.stream().sorted(Comparator.comparing(Contact::getName)).collect(Collectors.toList());
        this.onContactClickListener = onContactClickListener;
        this.imageManager = new ImageManager(filesDir);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        int color = (contact.isFavorite()) ? R.color.yellow : R.color.grey;

        if (contact.getImagePath() != null && !contact.getImagePath().equals("none") && !contact.getImagePath().isEmpty()) {
            if (contact.getImagePath().startsWith("https")) {
                Picasso.with(holder.itemView.getContext())
                    .load(contact.getImagePath())
                    .transform(new CircularTransformation())
                    .into(holder.contactImageView);
            } else {
                holder.contactImageView.setImageBitmap(imageManager.getImage(contact.getImagePath()));
            }
        }

        holder.tvName.setText(contact.getName());
        holder.favoriteImageView.setColorFilter(
                ContextCompat.getColor(holder.itemView.getContext(), color),
                PorterDuff.Mode.SRC_IN
        );

        holder.itemView.setOnClickListener(x -> {
            onContactClickListener.onContactClick(contact, holder.getAdapterPosition());
        });

        holder.favoriteImageView.setOnClickListener(x -> {
            onContactClickListener.onContactFavoriteClick(contact, holder.getAdapterPosition(), holder.favoriteImageView);
        });

        holder.moreImageView.setOnClickListener(x -> {
            onContactClickListener.onContactPopupMenuClick(contact, holder.getAdapterPosition(), holder.moreImageView);
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public interface OnContactClickListener {
        void onContactClick(Contact contact, int position);

        void onContactPopupMenuClick(Contact contact, int position, View viewItem);

        void onContactFavoriteClick(Contact contact, int position, ImageView imageView);
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
        }
    }
}
