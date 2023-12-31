package com.example.tcc.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.constants.TaskConstants;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoradiasAdapter extends RecyclerView.Adapter<MoradiasAdapter.MyViewHolder> {

    private List<Post> db = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private ImagemAdapter imagemAdapter;
    private Context context;

    public MoradiasAdapter(Context context, OnItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public MoradiasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = inflater.inflate(R.layout.activity_moradia_layout, parent, false);
        return new MyViewHolder(itemList);
    }


    @Override
    public void onBindViewHolder(@NonNull MoradiasAdapter.MyViewHolder holder, int position) {


        holder.cidadeTv.setText(String.valueOf(db.get(position).getCidade()));
        holder.ruaTv.setText(String.valueOf(db.get(position).getPostMoradia().getEndereco()));
        holder.numCasaTv.setText(String.valueOf(db.get(position).getPostMoradia().getNumCasa()));
        holder.moradoresTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getMoradores()));
        holder.valorTv.setText(String.valueOf(db.get(position).getPostMoradia().getValorAluguel()));
        holder.estadoTv.setText(String.valueOf(db.get(position).getEstado()));
        holder.generoTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getGeneroMoradia()));

        holderDetalhesMoradia(holder);

        imagemAdapter = new ImagemAdapter(context);
        imagemAdapter.setDbPost(db.get(position).getPostMoradia().getFotos());
        holder.recyclerview.setAdapter(imagemAdapter);


        holder.denunciarIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("denunciarIb", "onClick");
                SecurityPreferences securityPreferences = new SecurityPreferences(context);
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
        public ImageButton denunciarIb;
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
            denunciarIb = itemView.findViewById(R.id.ib_deuninciar_moradia);

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

    public void setPostagens(List<Post> db) {
        this.db = db;
        notifyDataSetChanged();
    }

    private void holderDetalhesMoradia(@NonNull MyViewHolder holder) {
        if (db.get(holder.getAdapterPosition()).getPostMoradia().isTipoResidencia()) {
            holder.tipoMoradiaTv.setText(" Casa");
        } else {
            holder.tipoMoradiaTv.setText(" Apartamento");
        }
        if (db.get(holder.getAdapterPosition()).getPostMoradia().getDetalhesMoradia().isGaragem()) {
            holder.garagemTv.setText(" possui");
        } else {
            holder.garagemTv.setText(" não possui");
        }
        if (db.get(holder.getAdapterPosition()).getPostMoradia().getDetalhesMoradia().isPets()) {
            holder.petTv.setText(" possui");
        } else {
            holder.petTv.setText(" não possui");
        }

        if (db.get(holder.getAdapterPosition()).getPostMoradia().getDetalhesMoradia().isQuarto()) {
            holder.quartoTv.setText(" individual");
        } else {
            holder.quartoTv.setText(" compartilhado");
        }
    }


}
