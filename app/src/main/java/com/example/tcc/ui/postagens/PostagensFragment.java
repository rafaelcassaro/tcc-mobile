package com.example.tcc.ui.postagens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.MainActivity;
import com.example.tcc.databinding.FragmentPostagensBinding;
import com.example.tcc.db.MoradiasDb;
import com.example.tcc.db.PostDb;
import com.example.tcc.db.PostagemDb;
import com.example.tcc.db.models.Postagem;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigToken;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.MoradiasAdapter;
import com.example.tcc.ui.adapter.PostAdapter;
import com.example.tcc.ui.adapter.PostagemAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.moradias.MoradiaExpandir;
import com.example.tcc.ui.moradias.TesteActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostagensFragment extends Fragment {

    private FragmentPostagensBinding binding;
    private PostAdapter postAdapter;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentPostagensBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // --------- recycler view  --------
        configAdapert(container);
        getDbBack(container);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void configAdapert(ViewGroup container){

        postAdapter = new PostAdapter(container.getContext());
        binding.rvPostagens.setAdapter(postAdapter);
       // Log.e("rv", "dados db:" + db.toString());
    }

    private void getDbBack(ViewGroup container){
        SecurityPreferences securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        RetrofitConfigToken retrofitConfigToken = new RetrofitConfigToken();
        retrofitConfigToken.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Call<List<Post>> call = retrofitConfigToken.getPostService().getAllPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()){
                    List<Post> db = new ArrayList<>();
                    List<Post> tempDb ;
                    tempDb = response.body();

                    for (Post post:tempDb) {
                        if(post.getPostMoradia() == null){
                            db.add(post);
                        }
                    }

                    Log.e("Response body", "dados db local:" + db.toString());
                    postAdapter.setPostagens(db);
                    Log.e("Response body", "dados ResponseBody:" + response.body());

                }
                else{
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

    private void mostrarErro(ViewGroup container){
        Toast.makeText(container.getContext(), "deu ruim !",Toast.LENGTH_SHORT).show();
    }



}