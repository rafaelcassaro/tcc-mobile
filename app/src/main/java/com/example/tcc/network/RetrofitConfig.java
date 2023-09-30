package com.example.tcc.network;

import com.example.tcc.network.services.UserService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private final Retrofit retrofit;

    public RetrofitConfig() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.107:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public UserService getUserService(){
        return this.retrofit.create(UserService.class);
    }
}
