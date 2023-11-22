package com.example.tcc.ui.postagens;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.databinding.FragmentNovaPostagemBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.network.services.UserService;
import com.example.tcc.ui.constants.TaskConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NovaPostagemFragment extends Fragment {

    private FragmentNovaPostagemBinding binding;
    private SecurityPreferences securityPreferences;
    private Picasso picasso;
    private RetrofitConfig retrofitConfig;
    private Usuario usuario;
    private Post post;
    private RetrofitConfigCepApi retrofitConfigCepApi;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNovaPostagemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        threadStartDados();

        post = new Post();

        binding.etCepUsuario.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                pegarCepViaApi();
                return false;
            }
        });


        binding.btPostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarDados()) {
                    verificarCepViaApi();
                }


            }
        });
        // Inflate the layout for this fragment
        return root;
    }

    private boolean verificarDados() {
        if (binding.etCepUsuario.getText().toString().length() == 0) {
            binding.etCepUsuario.requestFocus();
            binding.etCepUsuario.setError("Digite um CEP !");
            return false;
        } else if (binding.etComentario.getText().toString().length() == 0) {
            binding.etComentario.requestFocus();
            binding.etComentario.setError("Digite um Comentario !");
            return false;
        }
        return true;
    }

    private void pegarCepViaApi() {
        Integer cep = Integer.valueOf(binding.etCepUsuario.getText().toString());
        Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
        callApi.enqueue(new Callback<CepApi>() {
            @Override
            public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                if (response.isSuccessful()) {
                    CepApi cepApiDados = response.body();

                    post.setCidade(cepApiDados.getCity());
                    post.setEstado(cepApiDados.getState());
                    binding.tvCidadeUsuarioPost.setText(cepApiDados.getCity());
                    binding.tvEstadoUsuario.setText(cepApiDados.getState());

                } else {
                    binding.etCepUsuario.requestFocus();
                    binding.etCepUsuario.setError("Digite um CEP valido!");
                }

            }

            @Override
            public void onFailure(Call<CepApi> call, Throwable t) {

            }
        });
    }

    private void verificarCepViaApi() {
        Integer cep = Integer.valueOf(binding.etCepUsuario.getText().toString());
        Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
        callApi.enqueue(new Callback<CepApi>() {
            @Override
            public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                if (response.isSuccessful()) {
                    CepApi cepApiDados = response.body();

                    post.setCidade(cepApiDados.getCity());
                    post.setEstado(cepApiDados.getState());
                    binding.tvCidadeUsuarioPost.setText(cepApiDados.getCity());
                    binding.tvEstadoUsuario.setText(cepApiDados.getState());
                    salvarViaApi();
                } else {
                    binding.etCepUsuario.requestFocus();
                    binding.etCepUsuario.setError("Digite um CEP valido!");
                }

            }

            @Override
            public void onFailure(Call<CepApi> call, Throwable t) {

            }
        });
    }

    private void threadStartDados() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRetrofit();
                getUserViaApi();
                picasso = new Picasso.Builder(binding.getRoot().getContext())
                        .downloader(new OkHttp3Downloader(retrofitConfig.getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                        .build();
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getUserViaApi() {
        String idUser = securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY);
        Call<Usuario> call = retrofitConfig.getService(UserService.class).getUserById(Long.parseLong(idUser));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Usuario tempUser = response.body();
                    usuario = tempUser;
                    picasso.load(securityPreferences.getAuthToken(TaskConstants.PATH.URL)+"/usuarios/fotoperfil/" + usuario.getNomeFotoPerfil()).noFade().placeholder(R.drawable.img_not_found_little)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(binding.ivPerfilPost);
                    binding.tvNameUsuarioPost.setText(usuario.getNome());

                } else {
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private void startRetrofit() {
        securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfigCepApi = new RetrofitConfigCepApi();
    }

    private void salvarViaApi() {

        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        post.setComentario(binding.etComentario.getText().toString());
        post.setCep(binding.etCepUsuario.getText().toString());
        post.setQntdDenuncia(0);

        Call<Post> callSave = retrofitConfig.getService(PostService.class).createPost(post, id);
        callSave.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("novo_post_tag", "editPostTag");
                    startActivity(intent);
                } else {

                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }
}