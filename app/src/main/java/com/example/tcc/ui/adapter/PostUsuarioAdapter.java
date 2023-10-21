package com.example.tcc.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.network.entities.Post;

import java.util.ArrayList;
import java.util.List;

public class PostUsuarioAdapter extends RecyclerView.Adapter<PostUsuarioAdapter.MyViewHolder> {

    private List<Post> db = new ArrayList<>();
    private LayoutInflater inflater;

    private OnItemClickListener listener;


    public PostUsuarioAdapter(Context context, OnItemClickListener listener) {
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PostUsuarioAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_postagem_usuario_layout, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull PostUsuarioAdapter.MyViewHolder holder, int position) {
        Post item = db.get(position);

        holder.cidadeTv.setText(String.valueOf(db.get(position).getCidade()));
        holder.comentarioTv.setText(String.valueOf(db.get(position).getComentario()));
        holder.dataTv.setText(String.valueOf(db.get(position).getDataPost()));
        holder.estadoTv.setText(String.valueOf(db.get(position).getEstado()));

        if (db.get(position).getUsuario() != null) {
            holder.celularTv.setText(String.valueOf(db.get(position).getUsuario().getCelular()));
            holder.nomeTv.setText(String.valueOf(db.get(position).getUsuario().getNome()));
        }

        Log.e("ADAPTER", "item:" + db.toString());

    }

    @Override
    public int getItemCount() {
        return db.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nomeTv;
        public TextView cidadeTv;
        public TextView comentarioTv;
        public TextView celularTv;
        public TextView dataTv;
        public TextView estadoTv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTv = itemView.findViewById(R.id.tv_name_usuario_post);
            cidadeTv = itemView.findViewById(R.id.tv_cidade_usuario_post);
            comentarioTv = itemView.findViewById(R.id.tv_comment);
            celularTv = itemView.findViewById(R.id.tv_celular_numero);
            estadoTv = itemView.findViewById(R.id.tv_estado_usuario);
            dataTv = itemView.findViewById(R.id.tv_data_usuario_post);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setPostagens(List<Post> db){
        this.db = db;
        notifyDataSetChanged();
    }

}

