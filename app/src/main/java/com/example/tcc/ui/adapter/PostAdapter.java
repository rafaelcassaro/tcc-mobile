package com.example.tcc.ui.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private List<Post> db = new ArrayList<>();
    private LayoutInflater inflater;
    private SecurityPreferences securityPreferences;
    private Picasso picasso;
    private Context context;


    public PostAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = inflater.inflate(R.layout.activity_postagem_layout, parent, false);
        securityPreferences = new SecurityPreferences(context);
        picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                .build();

        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        ;
        holder.cidadeTv.setText(String.valueOf(db.get(holder.getAdapterPosition()).getCidade()));
        holder.comentarioTv.setText(String.valueOf(db.get(holder.getAdapterPosition()).getComentario()));
        holder.dataTv.setText(String.valueOf(db.get(holder.getAdapterPosition()).getDataPost()));
        holder.estadoTv.setText(String.valueOf(db.get(holder.getAdapterPosition()).getEstado()));

        if (db.get(holder.getAdapterPosition()).getUsuario() != null) {
            holder.celularTv.setText(String.valueOf(db.get(holder.getAdapterPosition()).getUsuario().getCelular()));
            holder.nomeTv.setText(String.valueOf(db.get(holder.getAdapterPosition()).getUsuario().getNome()));
        }

        holder.denunciarIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
                retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

                Call<Void> addDenuncia = retrofitConfig.getService(PostService.class).denunciarPost(db.get(position).getId());
                addDenuncia.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(context, "Denúncia enviada!", Toast.LENGTH_SHORT).show();
                        holder.denunciarIb.setClickable(false);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });

        picasso.load( securityPreferences.getAuthToken(TaskConstants.PATH.URL)+"/usuarios/fotoperfil/" + db.get(holder.getAdapterPosition()).getUsuario().getNomeFotoPerfil()).noFade().placeholder(R.drawable.img_not_found_little).into(holder.imageView);

        holder.tvRedesSocias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.custom_menu, popupMenu.getMenu());


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        item.setIcon(R.drawable.logo);
                        // Handle item clicks here
                        if (item.getItemId() == R.id.facebook) {
                            if (db.get(holder.getAdapterPosition()).getUsuario().getLink1().length() > 0) {
                                openSocialMediaLink("https://www.facebook.com/" + db.get(holder.getAdapterPosition()).getUsuario().getLink1());
                            } else {
                                Toast.makeText(context, "Usuario não possui facebook!", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                        else if (item.getItemId() == R.id.instagram) {
                            if (db.get(holder.getAdapterPosition()).getUsuario().getLink2().length() > 0) {
                                openSocialMediaLink("https://www.instagram.com/" + db.get(holder.getAdapterPosition()).getUsuario().getLink2());
                            } else {
                                Toast.makeText(context, "Usuario não possui instagram!", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                        else if (item.getItemId() == R.id.X) {
                            if (db.get(holder.getAdapterPosition()).getUsuario().getLink3().length() > 0) {
                                openSocialMediaLink("https://www.x.com/" + db.get(holder.getAdapterPosition()).getUsuario().getLink3());
                            } else {
                                Toast.makeText(context, "Usuario não possui Twitter!", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
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
        public TextView tvRedesSocias;
        public ImageButton denunciarIb;
        public CircleImageView imageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTv = itemView.findViewById(R.id.tv_name_usuario_post);
            cidadeTv = itemView.findViewById(R.id.tv_cidade_usuario_post);
            comentarioTv = itemView.findViewById(R.id.tv_comment);
            celularTv = itemView.findViewById(R.id.tv_celular_numero);
            estadoTv = itemView.findViewById(R.id.tv_estado_usuario);
            dataTv = itemView.findViewById(R.id.tv_data_usuario_post);
            imageView = itemView.findViewById(R.id.iv_perfil_post);
            tvRedesSocias = itemView.findViewById(R.id.tv_redes_sociais);
            denunciarIb = itemView.findViewById(R.id.ib_denunciar);
        }

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

    private void openSocialMediaLink(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                // Caso não haja aplicativo disponível para abrir o link
                context.startActivity(intent);
                //Toast.makeText(context, "Nenhum aplicativo disponível para abrir o link", Toast.LENGTH_SHORT).show();
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setPostagens(List<Post> db) {
        this.db = db;
        notifyDataSetChanged();
    }

}

