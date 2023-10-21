package com.example.tcc.ui.postagens;

import static android.app.PendingIntent.getActivity;

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

import java.util.ArrayList;
import java.util.List;

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
    private List<Post> db = new ArrayList<>();
    private CepApi cepApiDados = new CepApi();
    private RetrofitConfigCepApi retrofitConfigCepApi = new RetrofitConfigCepApi();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postagens_usuario_editar);

        Post post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);
        iniciarViews();


        Log.e("VALOR ID POSTMORADA", "deu ruim"+ post.getCidade());
        Log.e("VALOR ID POSTMORADA", "deu ruim"+ post.getEstado());
        Log.e("VALOR ID POSTMORADA", "deu ruim"+ post.getCep());
        Log.e("VALOR ID POSTMORADA", "deu ruim"+ post.getComentario());
        Log.e("VALOR ID POSTMORADA", "deu ruim"+ cidadeTv.getText().toString());

        cidadeTv.setText(post.getCidade());
        estadoTv.setText(post.getEstado());
        nomeTv.setText(post.getUsuario().getNome());
        cepEt.setText(post.getCep());
        comentarioEt.setText(post.getComentario());

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
                            cepApiDados = response.body();
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

        Integer cep = Integer.valueOf(cepEt.getText().toString());

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

    }



}