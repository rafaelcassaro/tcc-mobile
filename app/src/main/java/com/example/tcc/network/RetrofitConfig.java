package com.example.tcc.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.network.services.UserService;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private final Retrofit retrofit;

    private String token = "";


    Gson gson = new GsonBuilder().setDateFormat("dd MMM yyyy").create();

    public RetrofitConfig() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.107:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public String getTokene(){
        return token;
    }

    public void addHeader(String tokenValue){
        token = tokenValue;

    }


    public <T> T getService(Class<T> serviceClass){
        return retrofit.create(serviceClass);
    }

    public UserService getUserService(){
        return this.retrofit.create(UserService.class);
    }

    public PostService getPostService(){return this.retrofit.create(PostService.class);}



}
