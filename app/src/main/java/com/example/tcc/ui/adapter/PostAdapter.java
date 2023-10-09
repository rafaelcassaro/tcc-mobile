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
import com.example.tcc.db.PostDb;
import com.example.tcc.db.PostagemDb;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private List<Post> db = new ArrayList<>();
    private LayoutInflater inflater;


    public PostAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = inflater.inflate(R.layout.activity_postagem_layout, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        //Post item = db.get(position);

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


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nomeTv;
        public TextView cidadeTv;
        public TextView comentarioTv;
        public TextView celularTv;
        public TextView dataTv;
        public TextView estadoTv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTv = itemView.findViewById(R.id.tv_name);
            cidadeTv = itemView.findViewById(R.id.tv_cidade);
            comentarioTv = itemView.findViewById(R.id.tv_comment);
            celularTv = itemView.findViewById(R.id.tv_celular_numero);
            estadoTv = itemView.findViewById(R.id.tv_estado);
            dataTv = itemView.findViewById(R.id.tv_data);

        }

    }

    public void setPostagens(List<Post> db){
        this.db = db;
        notifyDataSetChanged();
    }

}

