package com.example.tcc.ui.perfil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tcc.R;
import com.example.tcc.databinding.FragmentPerfilBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.UserService;
import com.example.tcc.ui.constants.TaskConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private Usuario usuario;
    private Picasso picasso;
    private ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUri;
    private MultipartBody.Part imagemPart;
    private Long id;
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        usuario = new Usuario();

        setRetrofit();

        id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        pegarDadosViaApi();

        registerResult();

        binding.ivEditImgPerfil.setOnClickListener(view -> pickImage());

        binding.btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = atualizarDadosUsuario();
                Call<Usuario> call = retrofitConfig.getService(UserService.class).atualizarDados(id, usuario);
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.isSuccessful()) {
                            salvarImagemViaApi();
                            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }

                        } else {
                        }

                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {

                    }
                });

            }
        });

        return root;
    }

    private void setRetrofit() {
        securityPreferences = new SecurityPreferences(getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        picasso = new Picasso.Builder(getContext())
                .downloader(new OkHttp3Downloader(retrofitConfig.getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setarDados(Usuario usuario) {
        binding.etNome.setText(usuario.getNome());
        binding.etEmail.setText(usuario.getEmail());
        binding.etCelular.setText(usuario.getCelular());
        binding.etLink1.setText(usuario.getLink1());
        binding.etLink2.setText(usuario.getLink2());
        binding.etLink3.setText(usuario.getLink3());

        picasso.load("http://192.168.1.107:8080/usuarios/fotoperfil/" + usuario.getNomeFotoPerfil()).noFade().placeholder(R.drawable.img_not_found_little)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(binding.ivEditImgPerfil);
    }


    private void pegarDadosViaApi() {
        Call<Usuario> call = retrofitConfig.getService(UserService.class).getUserById(id);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Usuario tempUser = response.body();
                    setarDados(tempUser);
                    usuario.setSenha("");
                } else {

                }

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private Usuario atualizarDadosUsuario() {
        Usuario tempUsuario = new Usuario();
        tempUsuario.setNome(binding.etNome.getText().toString());
        tempUsuario.setEmail(binding.etEmail.getText().toString());
        tempUsuario.setSenha(binding.etSenha.getText().toString());
        tempUsuario.setCelular(binding.etCelular.getText().toString());
        tempUsuario.setLink1(binding.etLink1.getText().toString());
        tempUsuario.setLink2(binding.etLink2.getText().toString());
        tempUsuario.setLink3(binding.etLink3.getText().toString());

        return tempUsuario;
    }


    //-----------------------------------------LIDAR COM IMG -------------------------------------------------------------

    private void salvarImagemViaApi() {
        Call<Usuario> call = retrofitConfig.getService(UserService.class).editarFotoPerfil(imagemPart, id);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                } else {
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
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
                                imageUri = result.getData().getData();
                                File imageFile = new File(getRealPathFromUri(binding.getRoot().getContext(), result.getData().getData()));
                                RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
                                imagemPart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
                                binding.ivEditImgPerfil.setImageURI(imageUri);

                            }
                        } catch (Exception e) {
                        }
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


}