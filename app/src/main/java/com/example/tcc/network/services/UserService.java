package com.example.tcc.network.services;

import com.example.tcc.network.entities.Usuario;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {

    @GET("usuarios/{id}")
    Call<Usuario> getUserById(@Path("id") Long id);

    @POST("auth/login")
    Call<Usuario> login(@Body Usuario usuario);

    @POST("auth/register")
    Call<Usuario> registrar(@Body Usuario usuario);

    @PUT("usuarios/update/data/{id}")
    Call<Usuario> atualizarDados(@Path("id") Long id, @Body Usuario usuario);

    @Multipart
    @POST("usuarios/register/fotoperfil/{id}")
    Call<Usuario> registrarFotoPerfil(@Part MultipartBody.Part file, @Path("id") Long id);

    @Multipart
    @POST("usuarios/edit/fotoperfil/{id}")
    Call<Usuario> editarFotoPerfil(@Part MultipartBody.Part file, @Path("id") Long id);

    @PUT("usuarios/update/password/{id}")
    Call<Usuario> atualizarSenha(@Path("id") Long id, @Body Usuario user);


}
