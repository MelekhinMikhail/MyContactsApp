package com.mirea.kt.android2023.mycontactsapp.realm;

import com.mirea.kt.android2023.mycontactsapp.models.Contact;
import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ContactDatabaseOperations {

    private Realm realm;
    private static long realmVersion = 1L;

    public ContactDatabaseOperations() {
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("app_db_2")
                .schemaVersion(realmVersion)
                .allowWritesOnUiThread(true)
                .build();

        this.realm = Realm.getInstance(configuration);
    }

    public void insertContact(Contact contact) {

        realm.beginTransaction();
        realm.insert(contact);
        realm.commitTransaction();
    }

    public Contact getContact(long id) {

        return realm.where(Contact.class).equalTo("id", id).findFirst();
    }

    public List<Contact> getAllContacts() {

        RealmResults<Contact> results = realm.where(Contact.class).findAll();
        return new ArrayList<>(realm.copyFromRealm(results));
    }

    public void updateContact(long id, Contact contact) {

        contact.setId(id);

        realm.beginTransaction();
        realm.insertOrUpdate(contact);
        realm.commitTransaction();
    }

    public void addNumber(long id, PhoneNumber number) {

        Contact contact = getContact(id);
        realm.beginTransaction();
        contact.getNumbers().add(number);
        realm.commitTransaction();
    }

    public void addNumber(long contactId, PhoneNumber number, int index) {

        Contact contact = getContact(contactId);
        realm.beginTransaction();
        contact.getNumbers().add(index, number);
        realm.commitTransaction();
    }

    public void deleteNumber(long id, PhoneNumber number) {

        Contact contact = getContact(id);
        realm.beginTransaction();
        contact.getNumbers().remove(number);
        realm.commitTransaction();
    }

    public void deleteContact(long id) {

        Contact contact = realm.where(Contact.class).equalTo("id", id).findFirst();
        if (contact != null) {
            realm.beginTransaction();
            contact.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public void updateImage(long id, String path) {

        Contact contact = getContact(id);

        realm.beginTransaction();
        contact.setImagePath(path);
        realm.commitTransaction();
    }

    public boolean nameIsPresent(String contactName) {
        return getAllContacts().stream().anyMatch(x -> x.getName().equals(contactName));
    }

    public void updateName(long id, String name) {
        Contact contact = getContact(id);

        realm.beginTransaction();
        contact.setName(name);
        realm.commitTransaction();
    }
}
