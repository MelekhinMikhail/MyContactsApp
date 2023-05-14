package com.mirea.kt.android2023.mycontactsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mirea.kt.android2023.mycontactsapp.retrofit.clients.UniversityClient;
import com.mirea.kt.android2023.mycontactsapp.retrofit.services.UniversityService;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etLogin;
    private EditText etPassword;
    private Button button;
    private TextView tvErrors;
    private UniversityService universityService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etLoginLogin);
        etPassword = findViewById(R.id.etLoginPassword);
        button = findViewById(R.id.buttonLogin);
        tvErrors = findViewById(R.id.tvLoginErrors);
        universityService = UniversityClient.getClient().create(UniversityService.class);

        button.setOnClickListener(x -> {

            String login = etLogin.getText().toString();
            String password = etPassword.getText().toString();

            if (login.isEmpty() || password.isEmpty()) {
                tvErrors.setText("Все поля должны быть заполнены!");
                return;
            }

            Call<String> call = universityService.getAuthorization(login, password, "RIBO-01-21");
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {

                        String result = response.body();

                        if (result != null) {
                            try {

                                JSONObject jsonObject = new JSONObject(result);

                                int responseResult = jsonObject.getInt("result_code");
                                if (responseResult == 1) {

                                    SharedPreferences preferences = getSharedPreferences(
                                            PreferenceManager.getDefaultSharedPreferencesName(getApplicationContext()), MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("first_start", false);
                                    editor.putString("user_login", login);
                                    editor.putString("user_password", password);
                                    editor.apply();

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                } else {
                                    tvErrors.setText("Неправильный логин или пароль!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    call.cancel();
                    tvErrors.setText("Ошибка сервера!");
                }
            });
        });
    }
}