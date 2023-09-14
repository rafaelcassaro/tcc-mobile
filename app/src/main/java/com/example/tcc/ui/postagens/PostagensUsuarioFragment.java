package com.example.tcc.ui.postagens;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tcc.R;
import com.example.tcc.databinding.FragmentMoradiasBinding;
import com.example.tcc.databinding.FragmentPostagensUsuarioBinding;
import com.example.tcc.db.PostagemDb;
import com.example.tcc.ui.adapter.PostagemAdapter;

public class PostagensUsuarioFragment extends Fragment {

    private FragmentPostagensUsuarioBinding binding;

    private RecyclerView.Adapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPostagensUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // --------- recycler view  --------
        binding.rvPostagensUsuario.setHasFixedSize(true);
        adapter = new PostagemAdapter(container.getContext(), new PostagemDb());
        binding.rvPostagensUsuario.setAdapter(adapter);

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}