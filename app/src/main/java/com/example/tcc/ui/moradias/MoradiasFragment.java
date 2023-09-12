package com.example.tcc.ui.moradias;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.R;
import com.example.tcc.databinding.FragmentMoradiasBinding;
import com.example.tcc.db.MoradiasDb;
import com.example.tcc.ui.adapter.MoradiasAdapter;
import com.example.tcc.ui.user.TelaUsuario;

public class MoradiasFragment extends Fragment {

    private FragmentMoradiasBinding binding;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public static final String EXTRA_SHOW = "EXTRA_SHOW";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);



        binding = FragmentMoradiasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.rvMoradias.setHasFixedSize(true);

        adapter = new MoradiasAdapter(container.getContext(), new MoradiasDb());

        binding.rvMoradias.setAdapter(adapter);
        //layoutManager = new LinearLayoutManager(this);

        //binding.rvMoradias.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}