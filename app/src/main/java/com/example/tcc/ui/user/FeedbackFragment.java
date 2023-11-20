package com.example.tcc.ui.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.databinding.FragmentFeedbackBinding;
import com.example.tcc.databinding.FragmentNovaPostagemBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Feedback;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.FeedbackService;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.moradias.MoradiaUsuarioEditar;
import com.example.tcc.ui.postagens.PostagensUsuarioEditar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackFragment extends Fragment {

    private FragmentFeedbackBinding binding;
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;
    private Long id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        securityPreferences = new SecurityPreferences(getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));

        binding.btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comentario = binding.etFeedback.getText().toString();

                if(binding.etFeedback.getText().toString().isEmpty())
                {
                    binding.etFeedback.requestFocus();
                    binding.etFeedback.setError("Digite um comentário !");
                }
                else {
                    saveViaApi(comentario);
                }


            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    private void saveViaApi( String comentario) {
        Feedback feedback = new Feedback();
        feedback.setComentario(comentario);
        Call<Feedback> call = retrofitConfig.getService(FeedbackService.class).enviarFeedback(id, feedback);

        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                if (response.isSuccessful()){

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    Toast.makeText(getContext(), "Feedback enviado!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                else{

                }

            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}