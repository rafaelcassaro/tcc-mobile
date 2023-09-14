package com.example.tcc.ui.postagens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.databinding.FragmentPostagensBinding;
import com.example.tcc.db.PostagemDb;
import com.example.tcc.ui.adapter.PostagemAdapter;

public class PostagensFragment extends Fragment {


    private FragmentPostagensBinding binding;
    private RecyclerView.Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentPostagensBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // --------- recycler view  --------
        binding.rvPostagens.setHasFixedSize(true);
        adapter = new PostagemAdapter(container.getContext(), new PostagemDb());
        binding.rvPostagens.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}