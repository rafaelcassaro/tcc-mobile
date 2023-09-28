package com.example.tcc.ui.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tcc.R;
import com.example.tcc.databinding.FragmentFeedbackBinding;
import com.example.tcc.databinding.FragmentPlanosBinding;

public class PlanosFragment extends Fragment {

    private FragmentPlanosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPlanosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        escolhaPlano(binding);
        escolhaSemanas(binding);

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void escolhaPlano(FragmentPlanosBinding binding){
        binding.tvPlano1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvPlano1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_blue));
                binding.tvPlano2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));
                binding.tvPlano3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));

            }
        });

        binding.tvPlano2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvPlano2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_blue));
                binding.tvPlano1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));
                binding.tvPlano3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));
            }
        });

        binding.tvPlano3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvPlano3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_blue));
                binding.tvPlano1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));
                binding.tvPlano2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));
            }
        });
    }

    public void escolhaSemanas(FragmentPlanosBinding binding){

        binding.tvSemana1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvSemana1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_blue));
                binding.tvSemana2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));
                binding.tvSemana3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));

            }
        });

        binding.tvSemana2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvSemana2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_blue));
                binding.tvSemana1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));
                binding.tvSemana3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));

            }
        });

        binding.tvSemana3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvSemana3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_blue));
                binding.tvSemana2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));
                binding.tvSemana1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_planos_white));

            }
        });
    }

}