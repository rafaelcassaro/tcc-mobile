package com.example.tcc.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;

import java.util.ArrayList;
import java.util.List;

public class ImagemAdapter2 extends RecyclerView.Adapter<ImagemAdapter2.MyViewHolder>{

    private List<Uri> db = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public ImagemAdapter2(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ImagemAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemAdapter2.MyViewHolder holder, int position) {
       // Fotos imaggemPosition = db.get(position);
        //holder.carouselImageView.setImageURI(db.get(position));
        //Glide.with().load(db.get(position));
        //Glide.with(context).load(db.get(position)).error(R.drawable.img_not_found_little).into(holder.carouselImageView);

        //Picasso.get().load(db.get(position)).into(holder.carouselImageView);
        //Ion.with(context).load();

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
