package com.example.tcc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<String> db = new ArrayList<>();
    private OnItemClickListener listener;


    public SearchAdapter(Context context, OnItemClickListener listener) {

        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = inflater.inflate(R.layout.nome_cidades_layouts, parent, false);

        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position) {
        holder.tvCidadesSearch.setText(db.get(position));
    }

    @Override
    public int getItemCount() {
        return db.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvCidadesSearch;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCidadesSearch = itemView.findViewById(R.id.tv_cidades_layout_search);
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

    public List<String> getDb() {
        return db;
    }

    public void setDb(List<String> db) {
        this.db = db;
        notifyDataSetChanged();
    }
}

