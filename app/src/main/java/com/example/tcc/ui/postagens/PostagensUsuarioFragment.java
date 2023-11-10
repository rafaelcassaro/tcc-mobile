package com.example.tcc.ui.postagens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tcc.databinding.FragmentPostagensUsuarioBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.adapter.PostUsuarioAdapter;
import com.example.tcc.ui.constants.TaskConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostagensUsuarioFragment extends Fragment {

    private FragmentPostagensUsuarioBinding binding;
    private PostUsuarioAdapter postAdapter;
    private List<Post> db = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPostagensUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // --------- recycler view  --------
        configAdapter();
        getPostsByUserId(container);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        postAdapter= null;
    }


    private void configAdapter() {
        postAdapter = new PostUsuarioAdapter(getContext());
        binding.rvPostagensUsuario.setAdapter(postAdapter);
    }

    private void getPostsByUserId(ViewGroup container) {
        SecurityPreferences securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        String idUser = securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY);

        Call<List<Post>> call = retrofitConfig.getService(PostService.class).getPostByUserId(Long.parseLong(idUser));
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {

                    List<Post> tempDb;
                    tempDb = response.body();

                    for (Post post : tempDb) {
                        if (post.getPostMoradia() == null) {
                            db.add(post);
                        }
                    }
                    postAdapter.setPostagens(db);
                } else {
                    mostrarErro(container);
                }


            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                mostrarErro(container);
                Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
            }
        });
    }

    private void mostrarErro(ViewGroup container) {
        Toast.makeText(container.getContext(), "deu ruim !", Toast.LENGTH_SHORT).show();
    }
}