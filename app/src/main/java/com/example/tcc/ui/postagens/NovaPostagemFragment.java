package com.example.tcc.ui.postagens;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tcc.databinding.FragmentNovaPostagemBinding;
import com.example.tcc.network.SessaoManager;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.services.PostService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NovaPostagemFragment extends Fragment {

    private FragmentNovaPostagemBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNovaPostagemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        SessaoManager sessaoManager = SessaoManager.getInstance();
        if(sessaoManager.isLoggedIn()){
            Log.e("msg", "ta logado");
        }
        else {
            Log.e("msg", "ta deslogado");
        }

        binding.btPostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Post post = new Post();
                post.setComentario(binding.etComentario.getText().toString());

                post.setCidade("jefte");

                Call<Void> call = new RetrofitConfig().getPostService().createPost(post);
                //Call<Void> call = new RetrofitConfig().getService(PostService.class).createPost(post);
                //Call<Post> callGet = new RetrofitConfig().getPostService().getPost("2");
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            Log.e("msg", "deu bom");
                        }
                        else{
                            Log.e("msg", "deu bom ruim");
                        }


                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("msg", "deu ruim");
                    }



                });

                /*callGet.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        Post postTeste = response.body();
                        Log.e("CEPService   ", postTeste.toString());
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());

                    }
                });*/

            }
        });
        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}