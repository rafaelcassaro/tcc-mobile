package com.example.tcc.ui.postagens;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostagensUsuarioEditar extends AppCompatActivity {

    private TextView cidadeTv;
    private TextView estadoTv;
    private TextView nomeTv;
    private EditText cepEt;
    private EditText comentarioEt;
    private Button botaoEditar;
    private CircleImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postagens_usuario_editar);

        Post post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);
        iniciarViews();
        RetrofitConfigCepApi retrofitConfigCepApi = new RetrofitConfigCepApi();

        cidadeTv.setText(post.getCidade());
        estadoTv.setText(post.getEstado());
        nomeTv.setText(post.getUsuario().getNome());
        cepEt.setText(post.getCep());
        comentarioEt.setText(post.getComentario());

        SecurityPreferences securityPreferences = new SecurityPreferences(this);
        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                .build();

        picasso.load("http://192.168.1.107:8080/usuarios/fotoperfil/" + post.getUsuario().getNomeFotoPerfil()).noFade().placeholder(R.drawable.img_not_found_little).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);



        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setarDados(post);

                Integer cep = Integer.valueOf(cepEt.getText().toString());
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




    }

    private void salvarViaApi(Post post) {
        SecurityPreferences securityPreferences = new SecurityPreferences(getApplicationContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        //Long idPost = post.getPostMoradia().getId();



        Call<Void> call=retrofitConfig.getPostService().updatePost(post, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Intent intent = new Intent(PostagensUsuarioEditar.this, MainActivity.class);
                    intent.putExtra("editar_post_tag", "editPostTag");
                    startActivity(intent);



                }
                else {
                    Log.e("EDITAR POSTMORADIA", "deu ruim"+ post.getDataPost());
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EDITAR POST", "deu erro:"+ t);

            }
        });


    }

    private void setarDados(Post post) {
        post.setComentario(comentarioEt.getText().toString());
        post.setCep(cepEt.getText().toString());
    }

    private void iniciarViews() {
        cidadeTv = findViewById(R.id.tv_cidade_usuario_post);
        estadoTv = findViewById(R.id.tv_estado_usuario);
        nomeTv = findViewById(R.id.tv_name_usuario_post);
        cepEt = findViewById(R.id.et_cep_usuario);
        comentarioEt = findViewById(R.id.et_comentario);
        botaoEditar = findViewById(R.id.bt_postar);
        imageView = findViewById(R.id.iv_perfil_post);

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



}