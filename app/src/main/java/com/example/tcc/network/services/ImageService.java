package com.example.tcc.network.services;

import android.media.Image;

import com.example.tcc.network.entities.Fotos;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageService {

    @Multipart
    @POST("/imagens/cadastro/{id}")
    Call <Fotos> uploadImage(@Part MultipartBody.Part file, @Path("id") Long id);


    @GET("/imagens/{nome}")
    Call <ResponseBody>  getImage(@Path("nome")String nome);

}
