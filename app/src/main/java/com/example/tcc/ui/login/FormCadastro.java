package com.example.tcc.ui.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.services.UserService;
import com.example.tcc.ui.constants.TaskConstants;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormCadastro extends AppCompatActivity {

    private EditText nomeEt, emailEt, senhaEt, celularEt, link1Et, link2Et, link3Et;
    private CircleImageView iv_perfil;
    private Button bt_cadastrar;
    private ImageView backButton;
    private ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUri;
    private MultipartBody.Part imagemPart;
    private RetrofitConfig retrofitConfig;
    private boolean foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        IniciarComponentes();
        registerResult();

        iv_perfil.setOnClickListener(view -> pickImage());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormCadastro.super.onBackPressed();
            }
        });

        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verificarDados();
                verificarCelular();

                if(verificarDados() && verificarCelular()){
                    addViaApi();
                }

            }
        });

    }

    private void addViaApi() {
        Usuario tempUsuario = new Usuario();
        tempUsuario.setNome(nomeEt.getText().toString());
        tempUsuario.setEmail(emailEt.getText().toString());
        tempUsuario.setSenha(senhaEt.getText().toString());
        tempUsuario.setLink1(link1Et.getText().toString());
        tempUsuario.setLink2(link2Et.getText().toString());
        tempUsuario.setLink3(link3Et.getText().toString());

        tempUsuario.setCelular("+55" + celularEt.getText().toString());

        Call<Usuario> call = retrofitConfig.getService(UserService.class).registrar(tempUsuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.code() == TaskConstants.HTTP.CREATED) {
                    Long id = response.body().getId();
                    salvarImagemViaApi(id);
                    Intent intent = new Intent(FormCadastro.this, FormLogin.class);
                    startActivity(intent);
                } else {
                    Log.e("onResponse", "else: " + response);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                String s = "";
                Log.e("onResponse", "onFailure: " + t.getMessage());
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult() {
        Log.e("registerResult", "inicio");
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Log.e("registerResult", "RESULT_OK");
                                imageUri = result.getData().getData();
                                foto = true;
                                iv_perfil.setBackground(null);
                                File imageFile = new File(getRealPathFromUri(FormCadastro.this, result.getData().getData()));
                                RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
                                imagemPart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
                                iv_perfil.setImageURI(imageUri);
                                // multiPartImgList.add(imagemPart);
                                // Enviar a imagem usando Retrofit
                            }
                            Log.e("registerResult", "RESULT_notok");
                        } catch (Exception e) {
                            Log.e("sem img", "sem");
                        }
                    }
                });
    }

    private void salvarImagemViaApi(Long id) {
        Call<Usuario> call = retrofitConfig.getService(UserService.class).registrarFotoPerfil(imagemPart, id);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("salvarImagemViaApi", "onFailure: " + t.getMessage());
            }
        });
    }

    public String getRealPathFromUri(Context context, Uri uri) {
        String realPath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                realPath = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return realPath;
    }

    private void IniciarComponentes() {
        retrofitConfig = new RetrofitConfig("");
        foto = false;
        nomeEt = findViewById(R.id.et_nome);
        emailEt = findViewById(R.id.et_email);
        senhaEt = findViewById(R.id.et_senha);
        celularEt = findViewById(R.id.et_celular);
        link1Et = findViewById(R.id.et_link1);
        link2Et = findViewById(R.id.et_link2);
        link3Et = findViewById(R.id.et_link3);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
        iv_perfil = findViewById(R.id.iv_foto_perfil);
        backButton = findViewById(R.id.iv_voltar);
    }

    private boolean verificarCelular() {
        String[] ddd = {"61", "62", "64", "65", "66", "67", "82", "71", "73", "74", "75", "77", "85", "88", "98", "99", "83", "81", "87", "86"
                , "89", "84", "79", "68", "96", "92", "97", "91", "93", "94", "69", "95", "63", "27", "28", "31", "32", "33", "34", "35", "36", "37",
                "38", "21", "22", "24", "11", "12", "13", "14", "15", "16", "17", "18", "19", "41", "42", "43", "44", "45", "46", "51", "53", "54", "55", "47", "48", "49"};
        String teste;

        if (celularEt.getText().toString().length() > 0 && celularEt.getText().toString().length() != 0) {
            teste = celularEt.getText().toString().substring(0, 2);

            for (String dddCel : ddd) {
                if (teste.equals(dddCel.substring(0, 2))) {
                    return true;
                }

            }
            celularEt.setError("Digite um ddd valido!");
            celularEt.requestFocus();
            return false;
        } else if (celularEt.getText().toString().length() == 0) {
            celularEt.setError("Digite um número!");
            celularEt.requestFocus();
            return false;
        }

        return true;
    }

    private boolean verificarDados() {
        String nomeAdd = nomeEt.getText().toString();
        String emailAdd = emailEt.getText().toString();
        String senhaAdd = senhaEt.getText().toString();
        String instagramAdd = link1Et.getText().toString();
        String facebookAdd = link2Et.getText().toString();
        String TwitterAdd = link3Et.getText().toString();


        if (!isValidEmail(emailAdd)) {
            emailEt.setError("Email inválido");
            emailEt.requestFocus();
            return false;
        } else if (nomeAdd.length() == 0) {
            nomeEt.requestFocus();
            nomeEt.setError("Preencha o campo");
            return false;
        } else if (senhaAdd.length() == 0) {
            senhaEt.requestFocus();
            senhaEt.setError("Preencha o campo");
            return false;
        } else if (link1Et.getText().toString().length() >= 1 && !instagramAdd.matches("[a-zA-Z0-9._]+") || instagramAdd.startsWith(".") || instagramAdd.contains("..")) {
            link1Et.setError("Nome de usuário inválido para o Instagram!");
            link1Et.requestFocus();
            return false;
        } else if (link2Et.getText().toString().length() >= 1 && !facebookAdd.matches("[a-zA-Z0-9._]+") || facebookAdd.startsWith(".") || facebookAdd.contains("..")) {
            link2Et.requestFocus();
            link2Et.setError("Nome de usuário inválido para o Facebook!");
            return false;
        } else if (!TwitterAdd.matches("[a-zA-Z0-9._]+") && link3Et.getText().toString().length() >= 1) {
            link3Et.requestFocus();
            link3Et.setError("Nome de usuário inválido para o Twitter!");
            return false;
        }else if(!foto){
            Toast.makeText(FormCadastro.this, "Adicione uma imagem de perfil!", Toast.LENGTH_SHORT).show();
            iv_perfil.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_cadastro_error));
            return false;
        }
        return true;
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}