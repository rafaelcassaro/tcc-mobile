package com.example.tcc.ui.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<String> db = new ArrayList<>();



    public SearchAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        db.add("ola");
        db.add("olaa");
        db.add("olaaa");
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.nome_cidades_layouts,parent,false);
        View texte = LayoutInflater.from(context).inflate(R.layout.nome_cidades_layouts, parent, false);
        return new MyViewHolder(texte);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position) {
        holder.tvCidadesSearch.setText(db.get(position));

    }

    @Override
    public int getItemCount() {
        return db.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvCidadesSearch;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCidadesSearch = itemView.findViewById(R.id.tv_cidades_layout_search);

        }

    }

    public void setDb(List<String> db) {
        this.db = db;
        notifyDataSetChanged();
    }
}

