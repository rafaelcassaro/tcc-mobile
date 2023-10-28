package com.example.tcc.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tcc.R;
import com.example.tcc.network.entities.Fotos;
import com.example.tcc.network.entities.Post;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class ImagemAdapter extends RecyclerView.Adapter<ImagemAdapter.MyViewHolder>{

    private List<Uri> db = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public ImagemAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ImagemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemAdapter.MyViewHolder holder, int position) {
       // Fotos imaggemPosition = db.get(position);
        //holder.carouselImageView.setImageURI(db.get(position));
        //Glide.with().load(db.get(position));
        Glide.with(context).load(db.get(position)).error(R.drawable.img_not_found_little).into(holder.carouselImageView);
    }

    @Override
    public int getItemCount() {
        return db.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView carouselImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            carouselImageView = itemView.findViewById(R.id.iv_fotos);

        }


    }

    public void setImagem(List<Uri> db){
        this.db = db;
        notifyDataSetChanged();
    }
}
