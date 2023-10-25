package com.example.tcc.network.services;

import com.example.tcc.network.entities.Post;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PostService {

    @GET("posts/userPosts/{id}")
    Call<List<Post>> getPostByUserId(@Path("id") Long id);

    @GET("posts")
    Call<List<Post>> getAllPost();



    @POST("posts/{idUser}")
    Call<Post> createPost( @Body Post post, @Path("idUser")Long idUser );

    @POST("posts/edit/{idUser}/{idPostMoradia}")
    Call<Void> updatePostMoradia(@Body Post post, @Path("idUser")Long idUser, @Path("idPostMoradia")Long idPostMoradia);

    @POST("posts/edit/{idUser}")
    Call<Void> updatePost(@Body Post post, @Path("idUser")Long idUser);


}
