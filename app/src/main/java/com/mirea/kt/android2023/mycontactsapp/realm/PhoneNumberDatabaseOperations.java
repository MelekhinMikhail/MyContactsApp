package com.mirea.kt.android2023.mycontactsapp.realm;

import com.mirea.kt.android2023.mycontactsapp.models.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class PhoneNumberDatabaseOperations {

    private final RealmConfiguration configuration;

    public PhoneNumberDatabaseOperations(RealmConfiguration configuration) {
        this.configuration = configuration;
    }

    public void insertPhoneNumber(PhoneNumber phoneNumber) {
        Realm realm = Realm.getInstance(configuration);

        realm.beginTransaction();
        realm.insert(phoneNumber);
        realm.commitTransaction();
    }

    public PhoneNumber getPhoneNumber(long id) {
        Realm realm = Realm.getInstance(configuration);

        return realm.where(PhoneNumber.class).equalTo("id", id).findFirst();
    }

    public List<PhoneNumber> getAllPhoneNumbers() {
        Realm realm = Realm.getInstance(configuration);

        RealmResults<PhoneNumber> results = realm.where(PhoneNumber.class).findAll();
        return new ArrayList<>(realm.copyFromRealm(results));
    }

    public void updatePhoneNumber(long id, PhoneNumber phoneNumber) {
        Realm realm = Realm.getInstance(configuration);

        phoneNumber.setId(id);

        realm.beginTransaction();
        realm.insertOrUpdate(phoneNumber);
        realm.commitTransaction();
    }

    public void deletePhoneNumber(long id) {
        Realm realm = Realm.getInstance(configuration);

        PhoneNumber phoneNumber = realm.where(PhoneNumber.class).equalTo("id", id).findFirst();
        if (phoneNumber != null) {
            realm.beginTransaction();
            phoneNumber.deleteFromRealm();
            realm.commitTransaction();
        }
    }
}
