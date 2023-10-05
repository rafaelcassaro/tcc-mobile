package com.example.tcc.network.services;

import com.example.tcc.network.entities.Post;
import com.example.tcc.network.entities.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PostService {

    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") String id);

    @GET("posts")
    Call<List<Post>> getAllPost();

    @POST("posts")
    Call<Void> createPost(@Body Post post);


}
