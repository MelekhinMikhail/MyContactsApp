package com.mirea.kt.android2023.mycontactsapp.retrofit.clients;

import com.mirea.kt.android2023.mycontactsapp.R;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UniversityClient {

    public static final String BASE_URL = "https://android-for-students.ru/";

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
    }
}
