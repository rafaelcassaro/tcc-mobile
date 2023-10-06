package com.example.tcc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.db.MoradiasDb;
import com.example.tcc.network.entities.Post;

import java.util.ArrayList;
import java.util.List;


public class MoradiasAdapter extends RecyclerView.Adapter<MoradiasAdapter.MyViewHolder> {

    private List<Post> db = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickListener listener;


    public MoradiasAdapter(Context context, OnItemClickListener listener){
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }


    @NonNull
    @Override
    public MoradiasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View itemList = inflater.inflate(R.layout.activity_anuncio_layout, parent, false);
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_moradia_layout,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MoradiasAdapter.MyViewHolder holder, int position) {
        holder.tipoMoradiaTv.setText(String.valueOf(db.get(position).getPostMoradia().getTipoResidencia()));
        holder.cidadeTv.setText(String.valueOf(db.get(position).getCidade()));
        holder.ruaTv.setText(String.valueOf(db.get(position).getPostMoradia().getEndereco()));
        holder.numCasaTv.setText(String.valueOf(db.get(position).getPostMoradia().getNumCasa()));
        holder.moradoresTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getMoradores()));
        holder.quartoTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getQuarto()));
        holder.garagemTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getGaragem()));
        holder.petTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getPets()));
        holder.valorTv.setText(String.valueOf(db.get(position).getPostMoradia().getValorAluguel()));
        holder.estadoTv.setText(String.valueOf(db.get(position).getEstado()));
        holder.generoTv.setText(String.valueOf(db.get(position).getPostMoradia().getDetalhesMoradia().getGeneroMoradia()));

    }

    @Override
    public int getItemCount() {
        return db.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tipoMoradiaTv = itemView.findViewById(R.id.tv_tipo_moradia);
            cidadeTv = itemView.findViewById(R.id.tv_cidade);
            estadoTv = itemView.findViewById(R.id.tv_estado);
            generoTv = itemView.findViewById(R.id.tv_genero_moradia);
            ruaTv = itemView.findViewById(R.id.tv_rua);
            numCasaTv = itemView.findViewById(R.id.tv_num_casa);
            moradoresTv = itemView.findViewById(R.id.tv_ic_moradores);
            quartoTv = itemView.findViewById(R.id.tv_ic_quarto);
            garagemTv = itemView.findViewById(R.id.tv_ic_garagem);
            petTv = itemView.findViewById(R.id.tv_ic_pet);
            valorTv = itemView.findViewById(R.id.tv_valor);
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
