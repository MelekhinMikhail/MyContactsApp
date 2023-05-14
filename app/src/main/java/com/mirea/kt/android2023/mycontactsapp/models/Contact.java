package com.mirea.kt.android2023.mycontactsapp.models;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Contact extends RealmObject {

    @PrimaryKey
    private long id;

    @Required
    private String name;

    private String imagePath;

    private RealmList<PhoneNumber> numbers;

    private Date date;

    private boolean isFavorite;

    public Contact(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
        date = new Date();
        id = ((name == null) ? 0 : name.hashCode()) + date.getTime();
    }

    public Contact() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public RealmList<PhoneNumber> getNumbers() {
        return numbers;
    }

    public void setNumbers(RealmList<PhoneNumber> numbers) {
        this.numbers = numbers;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
