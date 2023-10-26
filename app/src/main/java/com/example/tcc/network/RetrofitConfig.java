package com.example.tcc.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.ImageService;
import com.example.tcc.network.services.PostService;
import com.example.tcc.network.services.UserService;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.Duration;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private final Retrofit retrofit;
    private String token = "";
    private OkHttpClient okHttpClient;

    Gson gson = new GsonBuilder().setDateFormat("dd MMM yyyy").create();

    public RetrofitConfig(String tokenValue) {

        if (tokenValue.equals("")){
            okHttpClient = new OkHttpClient().newBuilder().build();
        }
        else{
            okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {

                @NonNull
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {

                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", token)
                            .build();

                    return chain.proceed(request);
                }
            }).build();
        }

        this.retrofit = new Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:8080/") //para o emulador
                .baseUrl("http://192.168.1.107:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }



    public Retrofit getRetrofit() {
        return retrofit;
    }



    public void setHttpClient(){

    }

    public String getToken(){
        return token;
    }

    public void setToken(String theToken){
        token = theToken;
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
    public ImageService getImageService(){return this.retrofit.create(ImageService.class);}



}
