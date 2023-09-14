package com.example.tcc.ui.moradias;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.tcc.databinding.FragmentMoradiaUsuarioBinding;
import com.example.tcc.db.MoradiasDb;
import com.example.tcc.ui.adapter.MoradiasAdapter;

public class MoradiasUsuarioFragment extends Fragment {

    private FragmentMoradiaUsuarioBinding binding;
    private RecyclerView.Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // HomeViewModel homeViewModel =
        //        new ViewModelProvider(this).get(HomeViewModel.class);

        binding = com.example.tcc.databinding.FragmentMoradiaUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //-------------rv
        binding.rvMoradiasUsuario.setHasFixedSize(true);

        adapter = new MoradiasAdapter(container.getContext(), new MoradiasDb());


        binding.rvMoradiasUsuario.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}