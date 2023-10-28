package com.example.tcc.ui.adapter;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;


public class MoradiasUsuarioAdapter extends RecyclerView.Adapter<MoradiasUsuarioAdapter.MyViewHolder> {

    private List<Post> db = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickListener listener;



    public MoradiasUsuarioAdapter(Context context, OnItemClickListener listener){
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }


    @NonNull
    @Override
    public MoradiasUsuarioAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View itemList = inflater.inflate(R.layout.activity_anuncio_layout, parent, false);
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_moradia_usuario_layout,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MoradiasUsuarioAdapter.MyViewHolder holder, int position) {
        //holder.tipoMoradiaTv.setText(String.valueOf(db.get(position).getPostMoradia().isTipoResidencia()));
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
        holder.comentarioTv.setText(String.valueOf(db.get(position).getComentario()));


        if(db.get(position).getPostMoradia().isTipoResidencia()){
            holder.tipoMoradiaTv.setText(" Casa");
        }
        else{
            holder.tipoMoradiaTv.setText(" Apartamento");
        }
        if(db.get(position).getPostMoradia().getDetalhesMoradia().isGaragem()){
            holder.garagemTv.setText(" possui");
        }
        else{
            holder.garagemTv.setText(" não possui");
        }
        if(db.get(position).getPostMoradia().getDetalhesMoradia().isPets()){
            holder.petTv.setText(" possui");
        }
        else{
            holder.petTv.setText(" não possui");
        }

        if(db.get(position).getPostMoradia().getDetalhesMoradia().isQuarto()){
            holder.quartoTv.setText(" individual");
        }
        else{
            holder.quartoTv.setText(" compartilhado");
        }



        holder.editarBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int btId = v.getId();

                if(btId == R.id.bt_editar_moradia_usuario){
                    int clickedPosition = position;
                    Log.e("BOTAO EDITAR", ""+ clickedPosition);

                }
            }
        });

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
        public TextView comentarioTv;
        public Button editarBt;

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
            editarBt = itemView.findViewById(R.id.bt_editar_moradia_usuario);
            comentarioTv = itemView.findViewById(R.id.tv_comentario_usuario);
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
