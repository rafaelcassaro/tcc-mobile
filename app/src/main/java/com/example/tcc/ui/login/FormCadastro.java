package com.example.tcc.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.ui.constants.TaskConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormCadastro extends AppCompatActivity {

    private EditText nomeEt, emailEt, senhaEt, celularEt, link1Et, link2Et, link3Et;
    private Button bt_cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        IniciarComponentes();

        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Usuario tempUsuario = new Usuario();

                tempUsuario.setNome(nomeEt.getText().toString());
                tempUsuario.setEmail(emailEt.getText().toString());
                tempUsuario.setSenha(senhaEt.getText().toString());
                tempUsuario.setCelular(celularEt.getText().toString());
                tempUsuario.setLink1(link1Et.getText().toString());
                tempUsuario.setLink2(link2Et.getText().toString());
                tempUsuario.setLink3(link3Et.getText().toString());

                Call<Usuario> call = new RetrofitConfig("").getUserService().registrar(tempUsuario);

                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.code() == TaskConstants.HTTP.CREATED) {

                            Intent intent = new Intent(FormCadastro.this, FormLogin.class);
                            startActivity(intent);
                        } else {
                            //mostrarErro(FormLogin.this, "Usuario ou senha inválida");
                             Log.e("login user", "deu bom res: " + response);
                        }

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

    private void IniciarComponentes(){
        nomeEt = findViewById(R.id.et_nome);
        emailEt= findViewById(R.id.et_email);
        senhaEt = findViewById(R.id.et_senha);
        celularEt = findViewById(R.id.et_celular);
        link1Et = findViewById(R.id.et_link1);
        link2Et = findViewById(R.id.et_link2);
        link3Et = findViewById(R.id.et_link3);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);

    }

    private void verificarDados(){
        String nomeAdd = nomeEt.getText().toString();
        String emailAdd = emailEt.getText().toString();
        String senhaAdd = senhaEt.getText().toString();

        if (nomeAdd.length() == 0){
            nomeEt.requestFocus();
            nomeEt.setError("Preencha o campo");
        }
        else if(emailAdd.length() == 0){
            emailEt.requestFocus();
            emailEt.setError("Preencha o campo");
        }
        else if(senhaAdd.length() == 0){
            senhaEt.requestFocus();
            senhaEt.setError("Preencha o campo");
        }
        else{
            //integracao com a api
        }
    }

}