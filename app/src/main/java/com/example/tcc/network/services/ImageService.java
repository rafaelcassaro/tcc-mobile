package com.example.tcc.network.services;

import com.example.tcc.network.entities.Fotos;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageService {

    @Multipart
    @POST("/imagens/cadastro/{id}")
    Call <Fotos> uploadImage(@Part MultipartBody.Part file, @Path("id") Long id);

}
