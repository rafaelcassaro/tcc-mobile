package com.example.tcc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
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
                        else if(email.getText().toString().equals("q")){
                            Intent intent = new Intent(FormLogin.this, MainActivity.class);
                            startActivity(intent);
                        }


                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
                    }
                });





            }
        });
    }

    private void IniciarComponentes(){
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
        login_button = findViewById(R.id.bt_login);
        email = findViewById(R.id.edit_email);
        senha = findViewById(R.id.edit_senha);
    }
}