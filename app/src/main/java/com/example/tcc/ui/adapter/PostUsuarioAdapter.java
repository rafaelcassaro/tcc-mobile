package com.example.tcc.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.moradias.MoradiaUsuarioEditar;
import com.example.tcc.ui.postagens.PostagensUsuarioEditar;
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

public class PostUsuarioAdapter extends RecyclerView.Adapter<PostUsuarioAdapter.MyViewHolder> {

    private List<Post> db = new ArrayList<>();

    private SecurityPreferences securityPreferences;
    private Picasso picasso;
    private Context context;


    public PostUsuarioAdapter(Context context) {
        this.context =context;

    }

    @NonNull
    @Override
    public PostUsuarioAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_postagem_usuario_layout, parent, false);
        securityPreferences = new SecurityPreferences(context);
        picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                .build();
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull PostUsuarioAdapter.MyViewHolder holder, int position) {

        holder.cidadeTv.setText(String.valueOf(db.get(position).getCidade()));
        holder.comentarioTv.setText(String.valueOf(db.get(position).getComentario()));
        holder.dataTv.setText(String.valueOf(db.get(position).getDataPost()));
        holder.estadoTv.setText(String.valueOf(db.get(position).getEstado()));

        if (db.get(position).getUsuario() != null) {
            holder.celularTv.setText(String.valueOf(db.get(position).getUsuario().getCelular()));
            holder.nomeTv.setText(String.valueOf(db.get(position).getUsuario().getNome()));
        }

        picasso.load("http://192.168.1.107:8080/usuarios/fotoperfil/" + db.get(position).getUsuario().getNomeFotoPerfil()).noFade().placeholder(R.drawable.img_not_found_little).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);


        holder.btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostagensUsuarioEditar.class);
                intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW, db.get(position));
                v.getContext().startActivity(intent);

            }
        });

        Log.e("ADAPTER", "item:" + db.toString());

    }

    @Override
    public int getItemCount() {
        return db.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView nomeTv;
        public TextView cidadeTv;
        public TextView comentarioTv;
        public TextView celularTv;
        public TextView dataTv;
        public TextView estadoTv;
        public CircleImageView imageView;
        public Button btEditar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTv = itemView.findViewById(R.id.tv_name_usuario_post);
            cidadeTv = itemView.findViewById(R.id.tv_cidade_usuario_post);
            comentarioTv = itemView.findViewById(R.id.tv_comment);
            celularTv = itemView.findViewById(R.id.tv_celular_numero);
            estadoTv = itemView.findViewById(R.id.tv_estado_usuario);
            dataTv = itemView.findViewById(R.id.tv_data_usuario_post);
            imageView = itemView.findViewById(R.id.iv_perfil_post);
            btEditar = itemView.findViewById(R.id.bt_editar_postagem_usuario);


        }



    }



    public void setPostagens(List<Post> db){
        this.db = db;
        notifyDataSetChanged();
    }

    private OkHttpClient getOkHttpClientWithAuthorization(final String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                okhttp3.Request original = chain.request();
                Request request = original
                        .newBuilder()
                        .addHeader("Authorization", token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        return httpClient.build();

    }

}

