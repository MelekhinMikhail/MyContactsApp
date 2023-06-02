package com.mirea.kt.android2023.mycontactsapp.models;

import com.mirea.kt.android2023.mycontactsapp.models.enums.NumberType;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PhoneNumber extends RealmObject {

    @PrimaryKey
    private long id;

    private String number;

    private String type;

    private Date date;

    public PhoneNumber(String number, NumberType type) {
        this.number = number;
        this.type = type.getName();
        date = new Date();
        id = ((number == null) ? 0 : number.hashCode()) + date.getTime();
    }

    public PhoneNumber() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(NumberType type) {
        this.type = type.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
