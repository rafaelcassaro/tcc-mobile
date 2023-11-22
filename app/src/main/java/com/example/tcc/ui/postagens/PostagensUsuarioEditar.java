package com.example.tcc.ui.postagens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.constants.TaskConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostagensUsuarioEditar extends AppCompatActivity {

    private TextView cidadeTv, estadoTv, nomeTv;
    private EditText cepEt, comentarioEt;
    private Button botaoEditar;
    private CircleImageView imageView;
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;
    private Post post;
    private ImageView backButton;
    private Picasso picasso;
    private RetrofitConfigCepApi retrofitConfigCepApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postagens_usuario_editar);

        iniciarViews();
        iniciarDados();
        setarDadosNasViews();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostagensUsuarioEditar.super.onBackPressed();
            }
        });

        cepEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                pegarCepViaAPi();
                return false;
            }
        });

        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarDados()) {
                    verificarCepViaAPi();
                }
            }
        });
    }

    private boolean verificarDados() {
        if (cepEt.getText().toString().length() == 0) {
            cepEt.requestFocus();
            cepEt.setError("Digite um CEP !");
            return false;
        } else if (comentarioEt.getText().toString().length() == 0) {
            comentarioEt.requestFocus();
            comentarioEt.setError("Digite um Comentario !");
            return false;
        }
        return true;
    }

    private void verificarCepViaAPi() {
        Integer cep = Integer.valueOf(cepEt.getText().toString());
        Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
        callApi.enqueue(new Callback<CepApi>() {
            @Override
            public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                if (response.isSuccessful()) {
                    CepApi cepApiDados = response.body();
                    post.setCidade(cepApiDados.getCity());
                    post.setEstado(cepApiDados.getState());
                    cidadeTv.setText(cepApiDados.getCity());
                    estadoTv.setText(cepApiDados.getState());
                    // post.setDataPost(new Date());

                    salvarViaApi();
                    Log.e("POSTAGEMUSUARIOEDITAR", "response.isSuccessful" + post.getDataPost());

                } else {
                    cepEt.requestFocus();
                    cepEt.setError("Digite um CEP valido!");

                }
            }

            @Override
            public void onFailure(Call<CepApi> call, Throwable t) {
                Log.e("POSTAGEMUSUARIOEDITAR", "onFailure" + t.getMessage());
            }
        });
    }

    private void pegarCepViaAPi() {
        Integer cep = Integer.valueOf(cepEt.getText().toString());
        Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
        callApi.enqueue(new Callback<CepApi>() {
            @Override
            public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                if (response.isSuccessful()) {
                    CepApi cepApiDados = response.body();
                    post.setCidade(cepApiDados.getCity());
                    post.setEstado(cepApiDados.getState());
                    cidadeTv.setText(cepApiDados.getCity());
                    estadoTv.setText(cepApiDados.getState());
                    // post.setDataPost(new Date());


                    Log.e("POSTAGEMUSUARIOEDITAR", "response.isSuccessful" + post.getDataPost());

                } else {
                    cepEt.requestFocus();
                    cepEt.setError("Digite um CEP valido!");
                    Log.e("POSTAGEMUSUARIOEDITAR", "response.isSuccessful else" + post.getDataPost());
                }
            }

            @Override
            public void onFailure(Call<CepApi> call, Throwable t) {
                Log.e("POSTAGEMUSUARIOEDITAR", "onFailure" + t.getMessage());
            }
        });
    }

    private void setarDadosNasViews() {
        picasso.load(securityPreferences.getAuthToken(TaskConstants.PATH.URL)+"/usuarios/fotoperfil/" + post.getUsuario().getNomeFotoPerfil()).noFade().placeholder(R.drawable.img_not_found_little).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        cidadeTv.setText(post.getCidade());
        estadoTv.setText(post.getEstado());
        nomeTv.setText(post.getUsuario().getNome());
        cepEt.setText(post.getCep());
        comentarioEt.setText(post.getComentario());
    }

    private void iniciarDados() {
        post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);
        //securityPreferences = new SecurityPreferences(getApplicationContext());
        securityPreferences = new SecurityPreferences(this);
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfigCepApi = new RetrofitConfigCepApi();
        picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(retrofitConfig.getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                .build();
    }

    private void salvarViaApi() {
        setarDados();
        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        Call<Void> call = retrofitConfig.getService(PostService.class).updatePost(post, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(PostagensUsuarioEditar.this, MainActivity.class);
                    intent.putExtra("editar_post_tag", "editPostTag");
                    startActivity(intent);
                } else {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void setarDados() {
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
        backButton = findViewById(R.id.iv_voltar);

    }


}