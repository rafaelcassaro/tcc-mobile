package com.example.tcc.ui.gallery;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tcc.R;
import com.example.tcc.ui.home.HomeViewModel;

public class BlankFragment extends Fragment {

    private com.example.tcc.databinding.FragmentBlankBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // HomeViewModel homeViewModel =
        //        new ViewModelProvider(this).get(HomeViewModel.class);

        binding = com.example.tcc.databinding.FragmentBlankBinding.inflate(inflater, container, false);
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