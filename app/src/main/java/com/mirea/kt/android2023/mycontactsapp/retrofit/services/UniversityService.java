package com.mirea.kt.android2023.mycontactsapp.retrofit.services;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UniversityService {

    @FormUrlEncoded
    @POST("coursework/login.php")
    Call<String> getAuthorization(@Field("lgn") String login, @Field("pwd") String password, @Field("g") String group);
}
