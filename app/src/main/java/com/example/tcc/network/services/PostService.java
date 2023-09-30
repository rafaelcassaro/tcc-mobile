package com.example.tcc.network.services;

import com.example.tcc.network.entities.Post;
import com.example.tcc.network.entities.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PostService {

    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") String id);

    @POST("posts")
    Call<Void> createPost(@Body Post post);


}
