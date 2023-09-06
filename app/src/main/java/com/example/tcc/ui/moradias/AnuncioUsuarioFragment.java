package com.example.tcc.ui.moradias;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tcc.databinding.FragmentAnuncioUsuarioBinding;

public class AnuncioUsuarioFragment extends Fragment {

    private FragmentAnuncioUsuarioBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // HomeViewModel homeViewModel =
        //        new ViewModelProvider(this).get(HomeViewModel.class);

        binding = com.example.tcc.databinding.FragmentAnuncioUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textHello.setText("alou");
       // final TextView textView = binding.textHello;

       // homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}