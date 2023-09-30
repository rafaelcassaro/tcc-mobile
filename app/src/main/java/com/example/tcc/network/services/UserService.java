package com.example.tcc.network.services;

import com.example.tcc.network.entities.Usuario;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {

    @GET("usuarios/{udi}")
    Call<Usuario> getUser(@Path("udi") String uid);

}
