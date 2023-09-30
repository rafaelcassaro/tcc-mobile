package com.example.tcc.network;

import com.example.tcc.network.services.PostService;
import com.example.tcc.network.services.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private final Retrofit retrofit;

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();

    public RetrofitConfig() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.107:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }

    public UserService getUserService(){
        return this.retrofit.create(UserService.class);
    }

    public PostService getPostService(){return this.retrofit.create(PostService.class);}



}
