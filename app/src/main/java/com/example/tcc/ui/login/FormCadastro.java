package com.example.tcc.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tcc.R;

public class FormCadastro extends AppCompatActivity {

    private EditText edit_nome, edit_email, edit_senha;
    private Button bt_cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        IniciarComponentes();

        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeAdd = edit_nome.getText().toString();
                String emailAdd = edit_email.getText().toString();
                String senhaAdd = edit_senha.getText().toString();

                if (nomeAdd.length() == 0){
                    edit_nome.requestFocus();
                    edit_nome.setError("Preencha o campo");
                }
                else if(emailAdd.length() == 0){
                    edit_email.requestFocus();
                    edit_email.setError("Preencha o campo");
                }
                else if(senhaAdd.length() == 0){
                    edit_senha.requestFocus();
                    edit_senha.setError("Preencha o campo");
                }
                else{
                    //integracao com a api
                }

            }
        });

    }

    private void IniciarComponentes(){
        edit_nome = findViewById(R.id.et_nome);
        edit_email= findViewById(R.id.et_email);
        edit_senha= findViewById(R.id.et_senha);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);

    }
}