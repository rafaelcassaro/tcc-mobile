package com.example.tcc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.db.PostagemDb;

public class PostagemAdapter  extends RecyclerView.Adapter<PostagemAdapter.MyViewHolder>{

    private PostagemDb db = new PostagemDb();

    private LayoutInflater inflater;

    public PostagemAdapter(Context context, PostagemDb wordList) {
        inflater = LayoutInflater.from(context);
        this.db = wordList;
    }

    @NonNull
    @Override
    public PostagemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = inflater.inflate(R.layout.activity_postagem_layout, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull PostagemAdapter.MyViewHolder holder, int position) {

        holder.nomeTv.setText(String.valueOf(PostagemDb.myDataset.get(position).getNome()));
        holder.cidadeTv.setText(String.valueOf(PostagemDb.myDataset.get(position).getCidade()));
        holder.comentarioTv.setText(String.valueOf(PostagemDb.myDataset.get(position).getComentario()));
        holder.celularTv.setText(String.valueOf(PostagemDb.myDataset.get(position).getCelular()));

    }

    @Override
    public int getItemCount() {
        return PostagemDb.myDataset.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView nomeTv;
        public TextView cidadeTv;
        public TextView comentarioTv;
        public TextView celularTv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTv = itemView.findViewById(R.id.tv_name);
            cidadeTv = itemView.findViewById(R.id.tv_cidade);
            comentarioTv = itemView.findViewById(R.id.tv_comment);
            celularTv = itemView.findViewById(R.id.tv_celular_numero);
        }
    }
}

