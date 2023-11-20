package com.example.tcc.network.services;

import com.example.tcc.network.entities.Feedback;
import com.example.tcc.network.entities.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FeedbackService {


    @POST("feedback/{id}")
    Call<Feedback> enviarFeedback(@Path("id") Long id, @Body Feedback feedback);
}
