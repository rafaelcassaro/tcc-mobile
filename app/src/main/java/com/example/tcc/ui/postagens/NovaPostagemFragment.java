package com.example.tcc.ui.postagens;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.databinding.FragmentNovaPostagemBinding;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.RetrofitConfigToken;
import com.example.tcc.network.SessaoManager;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Detalhes;
import com.example.tcc.network.entities.Fotos;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.entities.PostMoradia;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.constants.TaskConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NovaPostagemFragment extends Fragment {

    private FragmentNovaPostagemBinding binding;
    private SecurityPreferences securityPreferences;
    private Picasso picasso;
    private RetrofitConfig retrofitConfig;
    private Usuario usuario;






    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNovaPostagemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRetrofit();
                getUserViaApi();
                picasso = new Picasso.Builder(binding.getRoot().getContext())
                        .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                        .build();



            }
        }).start();



        RetrofitConfigCepApi retrofitConfigCepApi = new RetrofitConfigCepApi();
        Post post = new Post();

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
                            CepApi cepApiDados = response.body();

                            post.setCidade(cepApiDados.getCity());
                            post.setEstado(cepApiDados.getState());

                            salvarViaApi(post);

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

    private void getUserViaApi(){
        String idUser = securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY);
        Call<Usuario> call = retrofitConfig.getUserService().getUserById(Long.parseLong(idUser));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()){
                    Usuario tempUser = response.body();
                    usuario= tempUser;
                    picasso.load("http://192.168.1.107:8080/usuarios/fotoperfil/" + usuario.getNomeFotoPerfil()).noFade().placeholder(R.drawable.img_not_found_little)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(binding.ivPerfilPost);
                    binding.tvNameUsuarioPost.setText(usuario.getNome());

                }
                else {}
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private void startRetrofit(){
        securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

    }

    private OkHttpClient getOkHttpClientWithAuthorization(final String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                okhttp3.Request original = chain.request();
                Request request = original
                        .newBuilder()
                        .addHeader("Authorization", token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        return httpClient.build();

    }

    private void salvarViaApi(Post post){
        SecurityPreferences securityPreferences = new SecurityPreferences(getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));

        post.setComentario(binding.etComentario.getText().toString());
        post.setCep(binding.etCepUsuario.getText().toString());
        post.setQntdDenuncia(0);

       // List<Fotos> fotos = new ArrayList<>();
       // post.setPostMoradia(new PostMoradia());
       // post.getPostMoradia().setDetalhesMoradia(new Detalhes());
       // post.getPostMoradia().setFotos(fotos);

        Call<Post> callSave = retrofitConfig.getPostService().createPost(post, id );
        callSave.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){
                    Log.e("msg", "deu bom");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("novo_post_tag", "editPostTag");
                    startActivity(intent);
                }
                else{
                    //Log.e("msg", "deu bom ruim"+ response.errorBody());
                }



            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("msg", "deu ruim");
            }
        });
    }
}