package com.example.tcc.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ImagemEditarMoradiaAdapter extends RecyclerView.Adapter<ImagemEditarMoradiaAdapter.MyViewHolder> {

    private List<Uri> db = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private SecurityPreferences securityPreferences;
    private Picasso picasso;
    private Uri imgNotFound;

    public ImagemEditarMoradiaAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        imgNotFound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.img_not_found_little);
    }

    @NonNull
    @Override
    public ImagemEditarMoradiaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = inflater.inflate(R.layout.carousel_item_editar, parent, false);
        securityPreferences = new SecurityPreferences(context);
        picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                .build();

        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemEditarMoradiaAdapter.MyViewHolder holder, int position) {

        Picasso.get().load(db.get(position)).error(R.drawable.img_not_found_little).placeholder(R.drawable.img_not_found_little).into(holder.carouselImageView);

    }

    @Override
    public int getItemCount() {
        return db.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView carouselImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            carouselImageView = itemView.findViewById(R.id.iv_fotos_editar);
        }
    }

    public List<Uri> getDb() {
        return db;
    }

    public void addImagem(Uri uri) {
        if (db.get(0) == imgNotFound) {
            db.remove(0);
        }

        db.add(uri);
        Log.e("ADAPTER", "addImagem db: " + db.size());
        notifyItemInserted(db.size() - 1);
        notifyDataSetChanged();
    }

    public void removeImg(int position) {

        if (!db.isEmpty() && position >= 0 && position < db.size()) {
            db.remove(position);
        }

        notifyItemRemoved(position);
        notifyDataSetChanged();

    }

    public void addImgvazia() {
        if (db.isEmpty()) {
            db.add(imgNotFound);
            notifyDataSetChanged();
        }

    }


    private OkHttpClient getOkHttpClientWithAuthorization(final String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
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
