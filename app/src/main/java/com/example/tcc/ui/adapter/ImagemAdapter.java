package com.example.tcc.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import com.example.tcc.R;
import com.example.tcc.network.entities.Fotos;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ImagemAdapter extends RecyclerView.Adapter<ImagemAdapter.MyViewHolder>{

    private List<Uri> db = new ArrayList<>();
    private List<Fotos> dbPost = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private SecurityPreferences securityPreferences;
    private Picasso picasso;
    //private Picasso picasso2;
    //private Uri imagem;
  //  private Uri imagem2;

    public ImagemAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ImagemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item,parent,false);

        securityPreferences = new SecurityPreferences(context);
        picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                .build();

        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemAdapter.MyViewHolder holder, int position) {
       // Fotos imaggemPosition = db.get(position);
        //holder.carouselImageView.setImageURI(db.get(position));
        //Glide.with().load(db.get(position));
        //Glide.with(context).load(db.get(position)).error(R.drawable.img_not_found_little).into(holder.carouselImageView);

      //  Ion.with(context)
      //          .load("http://192.168.1.107:8080/imagens/" + dbPost.get(position).getNomeFoto())
       //         .withBitmap()
      //          .placeholder(R.drawable.img_not_found_little)
      //          .error(R.drawable.logo)
      //          .intoImageView(holder.carouselImageView);


        //holder.carouselImageView.setImageBitmap(picasso.load("http://192.168.1.107:8080/imagens/" + dbPost.get(position).getNomeFoto())
         //       .into(););
                //.animateLoad(spinAnimation)
                //.animateIn(fadeInAnimation)
                ;


        picasso.load("http://192.168.1.107:8080/imagens/" + dbPost.get(position).getNomeFoto()).placeholder(R.drawable.img_not_found_little).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.carouselImageView.setImageBitmap(bitmap);

                Log.e("IMAGEMVIEW", "onBitmapLoaded: ");
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                Log.e("IMAGEMVIEW", "onBitmapFailed: "+e.toString());

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

                Log.e("IMAGEMVIEW", "onPrepareLoad: ");

            }
        });
        Log.e("IMAGEMVIEW", ": "+dbPost.get(position).getNomeFoto());
    }

    @Override
    public int getItemCount() {
        return dbPost.size();
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

    public void setDbPost(List<Fotos> dbPost){
        this.dbPost = dbPost;
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
