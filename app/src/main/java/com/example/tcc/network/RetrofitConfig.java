package com.example.tcc.network;

import androidx.annotation.NonNull;

import com.example.tcc.network.services.ImageService;
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

public class RetrofitConfig {

    private final Retrofit retrofit;
    private String token = "";
    private OkHttpClient okHttpClient;

    Gson gson = new GsonBuilder().setDateFormat("dd MMM yyyy").create();

    public RetrofitConfig(String tokenValue) {
        token = tokenValue;
        if (tokenValue.equals("")) {
            okHttpClient = new OkHttpClient().newBuilder().build();
        } else {
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
                .baseUrl("http://10.0.2.2:8080/") //para o emulador
                //.baseUrl("http://192.168.1.107:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public OkHttpClient getOkHttpClientWithAuthorization(final String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                okhttp3.Request original = chain.request();
                Request request = original
                        .newBuilder()
                        .addHeader("Authorization", token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        return httpClient.build();

    }

    public String getToken() {
        return token;
    }

    public void setToken(String theToken) {
        token = theToken;
    }

    public <T> T getService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

}
