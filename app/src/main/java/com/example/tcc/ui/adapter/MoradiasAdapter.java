package com.example.tcc.ui.adapter;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class MoradiasAdapter extends RecyclerView.Adapter<MoradiasAdapter.MyViewHolder> {

    private List<Post> db = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    private ImagemAdapter imagemAdapter;


    //private List<Uri> imageUriList = new ArrayList<>();
    private Context context;
    private Observable<List<Uri>> dataObservable;


    public MoradiasAdapter(Context context, OnItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.context = context;
        //imageUriList = new ArrayList<>();
        //imagemAdapter = new ImagemAdapter(context);
    }


    @NonNull
    @Override
    public MoradiasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View itemList = inflater.inflate(R.layout.activity_anuncio_layout, parent, false);
        //imagemAdapter.setImagem(imageUriList);

        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_moradia_layout, parent, false);

        return new MyViewHolder(itemList);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull MoradiasAdapter.MyViewHolder holder, int position) {
        // holder.tipoMoradiaTv.setText(String.valueOf(db.get(position).getPostMoradia().isTipoResidencia()));
        holder.cidadeTv.setText(String.valueOf(db.get(position).getCidade()));
        holder.ruaTv.setText(String.valueOf(db.get(position).getPostMoradia().getEndereco()));
        holder.numCasaTv.setText(String.valueOf(db.get(position).getPostMoradia().getNumCasa()));
        holder.moradoresTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getMoradores()));
        //holder.quartoTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().isQuarto()));
        //holder.garagemTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().isGaragem()));
        //holder.petTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().isPets()));
        holder.valorTv.setText(String.valueOf(db.get(position).getPostMoradia().getValorAluguel()));
        holder.estadoTv.setText(String.valueOf(db.get(position).getEstado()));
        holder.generoTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getGeneroMoradia()));

        if (db.get(position).getPostMoradia().isTipoResidencia()) {
            holder.tipoMoradiaTv.setText(" Casa");
        } else {
            holder.tipoMoradiaTv.setText(" Apartamento");
        }
        if (db.get(position).getPostMoradia().getDetalhesMoradia().isGaragem()) {
            holder.garagemTv.setText(" possui");
        } else {
            holder.garagemTv.setText(" não possui");
        }
        if (db.get(position).getPostMoradia().getDetalhesMoradia().isPets()) {
            holder.petTv.setText(" possui");
        } else {
            holder.petTv.setText(" não possui");
        }

        if (db.get(position).getPostMoradia().getDetalhesMoradia().isQuarto()) {
            holder.quartoTv.setText(" individual");
        } else {
            holder.quartoTv.setText(" compartilhado");
        }


        //if (db.get(position).getPostMoradia().getFotos().size() > 0) {
        List<Uri> imageUriList = new ArrayList<>();
       // imageUriList = db.get(position).getPostMoradia().getFotos();
       // imageUriList.clear();
        imagemAdapter = new ImagemAdapter(context);

        //imageUriList.clear();
       // imagemAdapter.setImagem(imageUriList);
        imagemAdapter.setDbPost(db.get(position).getPostMoradia().getFotos());

        holder.recyclerview.setAdapter(imagemAdapter);
        //holder.recyclerview.setInfinite(true);
        //holder.recyclerview.setFlat(true);





        //carregarImagemPost(db.get(position), imageUriList, imagemAdapter, picasso);

        Log.e("LISTA ADAPTER", ": "+ imageUriList);

        //carregarImagemPostt(db.get(position), imageUriList, imagemAdapter, picasso);
       // String nomeImg = post.getPostMoradia().getFotos().get(i).getNomeFoto();

       // picasso.load("http://192.168.1.107:8080/imagens/" + db.get(position).getPostMoradia().getFotos().get(0).getNomeFoto())
       //         .into(imagemAdapter.get);





        Log.e("VALOR STRING", ": "+ imageUriList);


      //  holder.recyclerview.scrollToPosition(imageUriList.size()-1);
        //imagemAdapter.setImagem(imageUriList);
       // holder.recyclerview.setAdapter(imagemAdapter);

    }

    @Override
    public int getItemCount() {
        return db.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tipoMoradiaTv;
        public TextView cidadeTv;
        public TextView estadoTv;
        public TextView generoTv;
        public TextView ruaTv;
        public TextView numCasaTv;
        public TextView moradoresTv;
        public TextView quartoTv;
        public TextView garagemTv;
        public TextView petTv;
        public TextView valorTv;
        public CarouselRecyclerview recyclerview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tipoMoradiaTv = itemView.findViewById(R.id.tv_tipo_moradia);
            cidadeTv = itemView.findViewById(R.id.tv_cidade_usuario_post);
            estadoTv = itemView.findViewById(R.id.tv_estado_usuario);
            generoTv = itemView.findViewById(R.id.tv_genero_moradia);
            ruaTv = itemView.findViewById(R.id.tv_rua);
            numCasaTv = itemView.findViewById(R.id.tv_num_casa);
            moradoresTv = itemView.findViewById(R.id.tv_ic_moradores);
            quartoTv = itemView.findViewById(R.id.tv_ic_quarto);
            garagemTv = itemView.findViewById(R.id.tv_ic_garagem);
            petTv = itemView.findViewById(R.id.tv_ic_pet);
            valorTv = itemView.findViewById(R.id.tv_valor);
            recyclerview = itemView.findViewById(R.id.crv_fotos_moradia);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           // Log.e("VALOR STRINGg", ": ");
            listener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

    }

    public void setPostagens(List<Post> db) {
        this.db = db;
        notifyDataSetChanged();
    }






// ... Seu código anterior ...



    private void carregarImagemPostt(Post post, List<Uri> imageUriList, ImagemAdapter imagemAdapter, Picasso picasso) {
        Handler handler = new Handler(Looper.getMainLooper());
        // Crie uma nova Thread para executar o carregamento em segundo plano



            for (int i = 0; i < post.getPostMoradia().getFotos().size(); i++) {
                String nomeImg = post.getPostMoradia().getFotos().get(i).getNomeFoto();

                picasso.load("http://192.168.1.107:8080/imagens/" + nomeImg).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        new Thread(() -> { File imageFile = createImageFileFromBitmap(bitmap);

                        if (imageFile != null) {
                            Uri imgPicasso = Uri.fromFile(imageFile);
                            imageUriList.add(imgPicasso);

                            // Notifique a RecyclerView na thread principal usando um Handler
                            handler.post(() -> {
                                imagemAdapter.notifyItemInserted(imageUriList.size() - 1);
                            });
                        }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        // Trate falhas se necessário
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Faça o preparo se necessário
                    }
                });
            }
        //
    }




    private void carregarImagemPost(Post post, List<Uri> imageUriList, ImagemAdapter imagemAdapter,Picasso picasso) {


        //SecurityPreferences securityPreferences = new SecurityPreferences(context);
        //Picasso picasso = new Picasso.Builder(context)
       //         .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
        //        .build();

        //  picasso.load("http://192.168.1.107:8080/imagens/Imagem1.jpg").into(target);


      //  int numImagens = post.getPostMoradia().getFotos().size();

        for (int i = 0; i < post.getPostMoradia().getFotos().size(); i++) {
            String nomeImg = post.getPostMoradia().getFotos().get(i).getNomeFoto();


            picasso.load("http://192.168.1.107:8080/imagens/" + nomeImg).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    File imageFile = createImageFileFromBitmap(bitmap);

                    if (imageFile != null) {
                       // Uri imgPicasso = Uri.fromFile(imageFile);
                        imageUriList.add(Uri.fromFile(imageFile));
                        imagemAdapter.notifyItemInserted(imageUriList.size()-1);

                        Log.e("LISTA ADAPTER", ": "+ imageUriList);
                        //imagemAdapter.setImagem(imageUriList);

                    } else {

                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }


    }



    private File createImageFileFromBitmap(Bitmap bitmap) {
        File imageFile = null;
        FileOutputStream fos = null;

        try {
            // Crie um arquivo temporário no diretório de armazenamento externo
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            imageFile = File.createTempFile("image", ".jpg", storageDir);

            // Salve o Bitmap como um arquivo JPEG
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            // Lidar com exceções, se ocorrerem
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imageFile;
    }

}
