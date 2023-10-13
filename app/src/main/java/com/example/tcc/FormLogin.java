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
    private SecurityPreferences securityPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        Context context = this;
        securityPreferences = new SecurityPreferences(context);
        Log.e("token valor", securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        IniciarComponentes();
        btnCadastro();
        btnLogin();
    }



    private void IniciarComponentes() {
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
        login_button = findViewById(R.id.bt_login);
        email = findViewById(R.id.edit_email);
        senha = findViewById(R.id.edit_senha);
    }

    private void mostrarErro(FormLogin x, String mensagem) {
        Toast.makeText(x, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void btnCadastro(){
        text_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormLogin.this, FormCadastro.class);
                startActivity(intent);
            }
        });
    }

    public void btnLogin(){

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario tempUsuario = new Usuario();
                tempUsuario.setEmail(email.getText().toString());
                tempUsuario.setSenha(senha.getText().toString());

                Call<Usuario> call = new RetrofitConfig("").getUserService().login(tempUsuario);
                Log.e("TOKEN", TaskConstants.SHARED.TOKEN_KEY);
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.code() == TaskConstants.HTTP.SUCCESS) {
                            salvarDadosLogin(response);

                            Intent intent = new Intent(FormLogin.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            mostrarErro(FormLogin.this, "Usuario ou senha inválida");
                            // Log.e("login user", "deu ruim: " + response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        String s = "";
                        Log.e("login user", "deu ruim" + t);
                    }
                });

                //Intent intent = new Intent(FormLogin.this, MainActivity.class);
                //startActivity(intent);


            }
        });

    }

    public void salvarDadosLogin(Response<Usuario> response){
        Usuario user = response.body();
        securityPreferences.store(TaskConstants.SHARED.TOKEN_KEY,user.getToken());
        securityPreferences.store(TaskConstants.SHARED.PERSON_KEY, String.valueOf(user.getId()));

        Log.e("TOKEN", securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        Log.e("ID USER", securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
    }

}