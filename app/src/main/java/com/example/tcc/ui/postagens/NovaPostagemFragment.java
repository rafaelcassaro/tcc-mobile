package com.example.tcc.ui.postagens;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tcc.databinding.FragmentNovaPostagemBinding;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.RetrofitConfigToken;
import com.example.tcc.network.SessaoManager;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Detalhes;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.entities.PostMoradia;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.constants.TaskConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NovaPostagemFragment extends Fragment {

    private FragmentNovaPostagemBinding binding;
    private Post post = new Post();
    private CepApi cepApiDados = new CepApi();
    private RetrofitConfigCepApi retrofitConfigCepApi = new RetrofitConfigCepApi();


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
                Integer cep = Integer.valueOf(binding.etCepUsuario.getText().toString());

                Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
                callApi.enqueue(new Callback<CepApi>() {
                    @Override
                    public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                        if (response.isSuccessful()){
                            cepApiDados = response.body();
                            post.setCidade(cepApiDados.getCity());
                            post.setEstado(cepApiDados.getState());

                            salvarViaApi();

                        }
                        else {

                        }
                    }

                    @Override
                    public void onFailure(Call<CepApi> call, Throwable t) {

                    }
                });

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

    private void salvarViaApi(){
        SecurityPreferences securityPreferences = new SecurityPreferences(getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));

        post.setComentario(binding.etComentario.getText().toString());
        post.setCep(binding.etCepUsuario.getText().toString());
        post.setQntdDenuncia(0);

        //post.setPostMoradia(new PostMoradia());
        //post.getPostMoradia().setDetalhesMoradia(new Detalhes());

        Call<Void> callSave = retrofitConfig.getPostService().createPost(post, id);
        callSave.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.e("msg", "deu bom");
                    //Intent intent = new Intent(getContext(), PostagensUsuarioFragment.class);
                    //intent.putExtra("fragment_tag", "SeuFragmentTag");
                    //startActivity(intent);
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
    }
}