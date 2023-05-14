package com.mirea.kt.android2023.mycontactsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);

        SharedPreferences preferences = getSharedPreferences(
                PreferenceManager.getDefaultSharedPreferencesName(getApplicationContext()), MODE_PRIVATE);
        if (!preferences.contains("first_start")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first_start", true);
            editor.apply();
        }

        boolean isFirstStart = preferences.getBoolean("first_start", false);
        if (isFirstStart) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView imageMenu = findViewById(R.id.imageMenu);
        NavigationView navigationView = findViewById(R.id.navigationView);

        imageMenu.setOnClickListener(x -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView textTitle = findViewById(R.id.textTitle);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                textTitle.setText(navDestination.getLabel());
            }
        });
    }
}