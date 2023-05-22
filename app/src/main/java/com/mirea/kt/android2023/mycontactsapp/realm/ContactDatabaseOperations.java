package com.mirea.kt.android2023.mycontactsapp.realm;

import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ContactDatabaseOperations {

    private final RealmConfiguration configuration;

    public ContactDatabaseOperations(RealmConfiguration configuration) {
        this.configuration = configuration;
    }

    public void insertContact(Contact contact) {
        Realm realm = Realm.getInstance(configuration);

        realm.beginTransaction();
        realm.insert(contact);
        realm.commitTransaction();
    }

    public Contact getContact(long id) {
        Realm realm = Realm.getInstance(configuration);

        return realm.where(Contact.class).equalTo("id", id).findFirst();
    }

    public List<Contact> getAllContacts() {
        Realm realm = Realm.getInstance(configuration);

        RealmResults<Contact> results = realm.where(Contact.class).findAll();
        return new ArrayList<>(realm.copyFromRealm(results));
    }

    public void updateContact(long id, Contact contact) {
        Realm realm = Realm.getInstance(configuration);

        contact.setId(id);

        realm.beginTransaction();
        realm.insertOrUpdate(contact);
        realm.commitTransaction();
    }

    public void addNumber(long id, PhoneNumber number) {
        Realm realm = Realm.getInstance(configuration);

        Contact contact = getContact(id);
        realm.beginTransaction();
        contact.getNumbers().add(number);
        realm.commitTransaction();
    }

    public void addNumber(long id, PhoneNumber number, int index) {
        Realm realm = Realm.getInstance(configuration);

        Contact contact = getContact(id);
        realm.beginTransaction();
        contact.getNumbers().add(index, number);
        realm.commitTransaction();
    }

    public void deleteNumber(long id, PhoneNumber number) {
        Realm realm = Realm.getInstance(configuration);

        Contact contact = getContact(id);
        realm.beginTransaction();
        contact.getNumbers().remove(number);
        realm.commitTransaction();
    }

    public void deleteContact(long id) {
        Realm realm = Realm.getInstance(configuration);

        Contact contact = realm.where(Contact.class).equalTo("id", id).findFirst();
        if (contact != null) {
            realm.beginTransaction();
            contact.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public void updateImage(long id, String path) {
        Realm realm = Realm.getInstance(configuration);

        Contact contact = getContact(id);

        realm.beginTransaction();
        contact.setImagePath(path);
        realm.commitTransaction();
    }
}
