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


public class MoradiasAdapter extends RecyclerView.Adapter<MoradiasAdapter.MyViewHolder> {

    private MoradiasDb db = new MoradiasDb();
    private LayoutInflater inflater;


    public MoradiasAdapter(Context context, MoradiasDb wordList){
        inflater = LayoutInflater.from(context);
        this.db = wordList;
    }


    @NonNull
    @Override
    public MoradiasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = inflater.inflate(R.layout.activity_anuncio_layout, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MoradiasAdapter.MyViewHolder holder, int position) {
        holder.tipoMoradiaTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getTipoMoradia()));
        holder.cidadeTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getCidade()));
        holder.ruaTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getRua()));
        holder.numCasaTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getNumCasa()));
        holder.moradoresTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getMoradores()));
        holder.quartoTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getQuarto()));
        holder.garagemTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getGaragem()));
        holder.petTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getPet()));
        holder.valorTv.setText(String.valueOf(MoradiasDb.myDataset.get(position).getValor()));

    }

    @Override
    public int getItemCount() {
        return MoradiasDb.myDataset.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tipoMoradiaTv;
        public TextView cidadeTv;
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
            ruaTv = itemView.findViewById(R.id.tv_rua);
            numCasaTv = itemView.findViewById(R.id.tv_num_casa);
            moradoresTv = itemView.findViewById(R.id.tv_ic_moradores);
            quartoTv = itemView.findViewById(R.id.tv_ic_quarto);
            garagemTv = itemView.findViewById(R.id.tv_ic_garagem);
            petTv = itemView.findViewById(R.id.tv_ic_pet);
            valorTv = itemView.findViewById(R.id.tv_valor);


        }
    }
}
