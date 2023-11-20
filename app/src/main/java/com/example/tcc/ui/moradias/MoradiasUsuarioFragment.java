package com.example.tcc.ui.moradias;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tcc.databinding.FragmentMoradiaUsuarioBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.adapter.MoradiasUsuarioAdapter;
import com.example.tcc.ui.constants.TaskConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiasUsuarioFragment extends Fragment {

    private FragmentMoradiaUsuarioBinding binding;
    private MoradiasUsuarioAdapter moradiasAdapter;
    private List<Post> db = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = com.example.tcc.databinding.FragmentMoradiaUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //-------------rv
        getMoradiasUsuario();
        configAdapter();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        moradiasAdapter = null;
        db.clear();
    }

    private void configAdapter() {
        moradiasAdapter = new MoradiasUsuarioAdapter(getContext());
        binding.rvMoradiasUsuario.setAdapter(moradiasAdapter);
    }


    private void getMoradiasUsuario() {
        SecurityPreferences securityPreferences = new SecurityPreferences(getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        String idUser = securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY);
        Call<List<Post>> call = retrofitConfig.getService(PostService.class).getPostByUserId(Long.parseLong(idUser));
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    List<Post> tempDb = response.body();
                    for (int i = 0; tempDb.size() > i; i++) {
                        if (tempDb.get(i).getPostMoradia() != null) {
                            db.add(tempDb.get(i));
                        }
                    }
                    moradiasAdapter.setPostagens(db);

                } else {
                    mostrarErro();
                }


            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                mostrarErro();
                Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
            }
        });
    }

    private void mostrarErro() {
        Toast.makeText(getContext(), "deu ruim !", Toast.LENGTH_SHORT).show();
    }

}