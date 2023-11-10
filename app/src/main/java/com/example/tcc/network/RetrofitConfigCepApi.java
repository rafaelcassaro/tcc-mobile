package com.example.tcc.network;

import com.example.tcc.network.services.CepService;

import okhttp3.OkHttpClient;
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

    public CepService getCepService() {
        return this.retrofit.create(CepService.class);
    }
}
