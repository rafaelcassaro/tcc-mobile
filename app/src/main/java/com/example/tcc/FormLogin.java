package com.example.tcc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.login.FormCadastro;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormLogin extends AppCompatActivity {

    private TextView text_tela_cadastro;
    private Button login_button;
    private EditText email;
    private EditText senha;
    private Usuario usuario = new Usuario();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        Context context = this;
        SecurityPreferences securityPreferences = new SecurityPreferences(context);

        //getSupportActionBar().hide();
        IniciarComponentes();

        text_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormLogin.this, FormCadastro.class);
                startActivity(intent);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario tempUsuario = new Usuario();
                tempUsuario.setEmail(email.getText().toString());
                tempUsuario.setSenha(senha.getText().toString());


                Call<Usuario> call = new RetrofitConfig().getUserService().login(tempUsuario);
                Log.e("TOKEN", TaskConstants.SHARED.TOKEN_KEY);
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.code() == TaskConstants.HTTP.SUCCESS) {
                            Usuario user = response.body();
                            securityPreferences.store(user.getToken());
                            Log.e("TOKEN", securityPreferences.getAuthToken());
                            RetrofitConfig retrofitConfig = new RetrofitConfig();

                            //securityPreferences.store(TaskConstants.SHARED.PERSON_NAME,user.getNome());


                            //Log.e("login user", "deu bao: " + user.getToken());
                            //Log.e("TOKEN", retrofitConfig.getTokene());
                            Intent intent = new Intent(FormLogin.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            //mostrarErro(FormLogin.this, "Usuario ou senha inválida");

                        }

                        Log.e("login user", "deu bao: " + response);
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        String s = "";
                        Log.e("login user", "deu ruim" + t);
                    }
                });


            }
        });

    }

    private void mostrarErro(FormLogin x, String mensagem) {
        Toast.makeText(x, mensagem, Toast.LENGTH_SHORT).show();
    }


    private void IniciarComponentes() {
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
        login_button = findViewById(R.id.bt_login);
        email = findViewById(R.id.edit_email);
        senha = findViewById(R.id.edit_senha);
    }


    /*login_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Call<Usuario> call = new RetrofitConfig().getUserService().getUser("1");

            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    usuario = response.body();
                    if(usuario.getEmail().equals(email.getText().toString()) && usuario.getSenha().equals(senha.getText().toString())){

                        //logginManager.setUser(usuario);
                        Intent intent = new Intent(FormLogin.this, MainActivity.class);
                        startActivity(intent);
                    }

                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
                }
            });*/
}