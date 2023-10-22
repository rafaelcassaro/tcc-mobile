package com.example.tcc.network.services;

import com.example.tcc.network.entities.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("usuarios/{id}")
    Call<Usuario> getUserById(@Path("id") Long id);

    @POST("auth/login")
    Call<Usuario> login(@Body Usuario usuario);

    @GET("usuarios/{id}")
    Call<Usuario> findById(@Path("id")Long id);

    @POST("auth/register")
    Call<Usuario> registrar(@Body Usuario usuario);

    @PUT("usuarios/{id}")
    Call<Usuario> atualizarDados(@Path("id") Long id,@Body Usuario usuario);



}
