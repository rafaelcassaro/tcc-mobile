package com.example.tcc.ui.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
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
    private Button bt_cadastrar, bt_perfil_add;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUri;
    private MultipartBody.Part imagemPart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);
        IniciarComponentes();

        String [] ddd ={ "61","62","64","65","66","67","82","71","73","74","75","77","85","88","98","99","83","81","87","86"
                ,"89","84","79","68","96","92","97","91","93","94","69","95","63","27","28","31","32","33","34","35","36","37",
                "38","21","22","24","11","12","13","14","15","16","17","18","19","41","42","43","44","45","46","51","53","54","55","47","48","49"};

        registerResult();
        bt_perfil_add.setOnClickListener(view -> pickImage());

        ImageView backButton = findViewById(R.id.iv_voltar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormCadastro.super.onBackPressed();
            }
        });

        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Usuario tempUsuario = new Usuario();

                tempUsuario.setNome(nomeEt.getText().toString());
                tempUsuario.setEmail(emailEt.getText().toString());
                tempUsuario.setSenha(senhaEt.getText().toString());

                tempUsuario.setLink1(link1Et.getText().toString());
                tempUsuario.setLink2(link2Et.getText().toString());
                tempUsuario.setLink3(link3Et.getText().toString());

                String x = celularEt.getText().toString().substring(0,2);
                Log.e("ASDFASDFASDF", "ASDFASDFASDFASDF: "+x);



                // registerResult();



                //  bt_perfil_add.setOnClickListener(new View.OnClickListener() {
                //       @Override
                //       public void onClick(View v) {

                //       }
                //    });
                for (String dddCel: ddd) {
                    Log.e("ASDFASDFASDF", "x: "+ x);
                    Log.e("ASDFASDFASDF", "dddCel: "+ dddCel);
                    if(x.equals(dddCel.substring(0,2))){
                        Log.e("ASDFASDFASDF", "ASDFASDFASDFASDF: ");
                        tempUsuario.setCelular("+55"+celularEt.getText().toString());






                Call<Usuario> call = new RetrofitConfig("").getUserService().registrar(tempUsuario);
                Log.e("FORMCADASTRO", "RESULT_OK");
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.code() == TaskConstants.HTTP.CREATED) {
                            Long id = response.body().getId();
                            Log.e("onResponse", "id: " + id);
                            salvarImagemViaApi(id);
                            Intent intent = new Intent(FormCadastro.this, FormLogin.class);
                            startActivity(intent);







                            //startActivity(intent);
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

                        break;
                    }
                }


                celularEt.requestFocus();
                celularEt.setError("Digite um ddd valido");


            }
        });

    }


    private void pickImage() {
        // registerResult();
        Log.e("FORMCADASTRO", "RESULT_OK");
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);

        resultLauncher.launch(intent);
        Log.e("FORMCADASTRO", "RESULT_OKk");
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

    private void salvarImagemViaApi(Long id){
        Log.e("salvarImagemViaApi", "inicio");
        Call<Usuario> call = new RetrofitConfig("").getUserService().registrarFotoPerfil(imagemPart, id);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Log.e("salvarImagemViaApi", "response.isSuccessful");

                }
                else {
                    Log.e("salvarImagemViaApi", "response.else "+ response.errorBody());
                    Log.e("salvarImagemViaApi", "response.else "+ response.body());

                }
            }


            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("salvarImagemViaApi", "onFailure: "+ t.getMessage());

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
        nomeEt = findViewById(R.id.et_nome);
        emailEt = findViewById(R.id.et_email);
        senhaEt = findViewById(R.id.et_senha);
        celularEt = findViewById(R.id.et_celular);
        link1Et = findViewById(R.id.et_link1);
        link2Et = findViewById(R.id.et_link2);
        link3Et = findViewById(R.id.et_link3);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
        iv_perfil = findViewById(R.id.iv_foto_perfil);
        bt_perfil_add = findViewById(R.id.bt_add_ft_perfil);

    }

    private void verificarDados() {
        String nomeAdd = nomeEt.getText().toString();
        String emailAdd = emailEt.getText().toString();
        String senhaAdd = senhaEt.getText().toString();

        if (nomeAdd.length() == 0) {
            nomeEt.requestFocus();
            nomeEt.setError("Preencha o campo");
        } else if (emailAdd.length() == 0) {
            emailEt.requestFocus();
            emailEt.setError("Preencha o campo");
        } else if (senhaAdd.length() == 0) {
            senhaEt.requestFocus();
            senhaEt.setError("Preencha o campo");
        } else {
            //integracao com a api
        }
    }

}