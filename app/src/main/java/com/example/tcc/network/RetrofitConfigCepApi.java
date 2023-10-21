package com.example.tcc.network;

import androidx.annotation.NonNull;

import com.example.tcc.network.services.CepService;
import com.example.tcc.network.services.PostService;
import com.example.tcc.network.services.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfigCepApi {

    private final Retrofit retrofit;

    private OkHttpClient okHttpClient;

    public RetrofitConfigCepApi() {
        okHttpClient = new OkHttpClient().newBuilder().build();

        this.retrofit = new Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:8080/") para o emulador
                .baseUrl("https://brasilapi.com.br/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public Retrofit getRetrofit() {
        return retrofit;
    }


    public void setHttpClient(){

    }


    public CepService getCepService(){
        return  this.retrofit.create(CepService.class);
    }
}
