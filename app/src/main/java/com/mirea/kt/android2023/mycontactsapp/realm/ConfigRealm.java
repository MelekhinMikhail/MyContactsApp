package com.mirea.kt.android2023.mycontactsapp.realm;

import io.realm.RealmConfiguration;

public class ConfigRealm {

    private static long realmVersion = 1L;

    public static RealmConfiguration getRealmConfiguration() {

        return new RealmConfiguration.Builder()
                .name("app_db_2")
                .schemaVersion(realmVersion)
                .allowWritesOnUiThread(true)
                .build();
    }
}
