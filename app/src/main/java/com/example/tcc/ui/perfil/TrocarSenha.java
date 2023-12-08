package com.example.tcc.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.UserService;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.login.FormLogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrocarSenha extends AppCompatActivity {


    private EditText senhaNova, senhaNovaConfirmar;
    private Button btEnviar;
    private ImageView btVoltar;
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trocar_senha);
        setRetrofit();
        Usuario usuario = (Usuario) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_USER);


        btEnviar = findViewById(R.id.bt_enviar);
        btVoltar = findViewById(R.id.iv_voltar);

        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrocarSenha.super.onBackPressed();
            }
        });

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senhaNova = findViewById(R.id.et_senha_nova);
                senhaNovaConfirmar = findViewById(R.id.et_senha_nova_confirmar);

                String newPassword = senhaNova.getText().toString();
                String newPasswordTest = senhaNovaConfirmar.getText().toString();

                if (newPassword.isEmpty()) {
                    senhaNova.requestFocus();
                    senhaNova.setError("Preencha o campo!");

                } else if (newPasswordTest.isEmpty()) {
                    senhaNovaConfirmar.requestFocus();
                    senhaNovaConfirmar.setError("Preencha o campo!");
                } else {
                    if (newPassword.equals(newPasswordTest)) {
                        usuario.setSenha(newPassword);
                        trocarSenha(usuario);

                    } else {
                        Toast.makeText(TrocarSenha.this, "Senhas diferentes!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }


    private void trocarSenha(Usuario newPassword) {
        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        Call<Usuario> call = retrofitConfig.getService(UserService.class).atualizarSenha(id, newPassword);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.e("TROCAR SENHA", "response.isSuccessful()");
                    Intent intent = new Intent(getApplicationContext(), FormLogin.class);

                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.e("TROCAR SENHA", "response.eselsae()");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("TROCAR SENHA", "response.onFailure()" + t.getMessage());
            }
        });
    }


    private void setRetrofit() {
        securityPreferences = new SecurityPreferences(this);
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

    }
}