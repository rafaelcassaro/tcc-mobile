package com.example.tcc.db;

import android.util.Log;

import com.example.tcc.db.models.Postagem;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDb {

    private final List<Post> myDataset;

    public PostDb(List<Post> myDataset) {
        this.myDataset = myDataset;
    }

    public List<Post> getMyDataset() {
        return myDataset;
    }
}