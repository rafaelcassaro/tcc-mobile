package com.example.tcc.ui.perfil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tcc.R;
import com.example.tcc.databinding.FragmentPerfilBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.UserService;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.login.FormCadastro;
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

                verificarDados();
                verificarCelular();

                if(verificarDados() && verificarCelular()){
                    editarViaApi();
                }

            }
        });

        return root;
    }

    private boolean verificarCelular() {
        String[] ddd = {"61", "62", "64", "65", "66", "67", "82", "71", "73", "74", "75", "77", "85", "88", "98", "99", "83", "81", "87", "86"
                , "89", "84", "79", "68", "96", "92", "97", "91", "93", "94", "69", "95", "63", "27", "28", "31", "32", "33", "34", "35", "36", "37",
                "38", "21", "22", "24", "11", "12", "13", "14", "15", "16", "17", "18", "19", "41", "42", "43", "44", "45", "46", "51", "53", "54", "55", "47", "48", "49"};
        String teste;
        String teste2 = "";
        if(binding.etCelular.getText().toString().length() > 3){
            teste2 = binding.etCelular.getText().toString().substring(0, 3);
        }

        Log.e("verificarCelular", "teste2: "+ teste2);
        Log.e("verificarCelular", "teste2: "+ teste2.equals("+55"));
        if (teste2.equals("+55") && binding.etCelular.getText().toString().length() > 3 && binding.etCelular.getText().toString().length() != 0) {
            teste = binding.etCelular.getText().toString().substring(3, 5);
            Log.e("verificarCelular", "deu bao1: ");
            for (String dddCel : ddd) {
                if (teste.equals(dddCel.substring(0, 2))) {
                    return true;
                }

            }
            binding.etCelular.setError("Digite um ddd valido!");
            binding.etCelular.requestFocus();
            return false;
        } else if (binding.etCelular.getText().toString().length() == 0) {
            Log.e("verificarCelular", "deu bao2: ");
            binding.etCelular.setError("Digite um número!");
            binding.etCelular.requestFocus();
            return false;
        }else if(teste2 != "+55"){
            Log.e("verificarCelular", "deu bao3: ");
            binding.etCelular.setError("Digite um ddd valido começado com +55ddd!!");
            binding.etCelular.requestFocus();
            return false;
        }
        Log.e("verificarCelular", "final: ");
        return true;
    }

    private boolean verificarDados(){
        String nomeAdd = binding.etNome.getText().toString();
        String emailAdd = binding.etEmail.getText().toString();
        String senhaAdd = binding.etSenha.getText().toString();
        String instagramAdd = binding.etLink1.getText().toString();
        String facebookAdd = binding.etLink2.getText().toString();
        String TwitterAdd = binding.etLink3.getText().toString();

        if (!isValidEmail(emailAdd)) {
            binding.etEmail.setError("Email inválido");
            binding.etEmail.requestFocus();
            return false;
        } else if (nomeAdd.length() == 0) {
            binding.etNome.requestFocus();
            binding.etNome.setError("Preencha o campo");
            return false;
        } else if (senhaAdd.length() == 0) {
            binding.etSenha.requestFocus();
            binding.etSenha.setError("Preencha o campo");
            return false;
        } else if (binding.etLink1.getText().toString().length() >= 1 && !instagramAdd.matches("[a-zA-Z0-9._]+") || instagramAdd.startsWith(".") || instagramAdd.contains("..")) {
            binding.etLink1.setError("Nome de usuário inválido para o Instagram!");
            binding.etLink1.requestFocus();
            return false;
        } else if (binding.etLink2.getText().toString().length() >= 1 && !facebookAdd.matches("[a-zA-Z0-9._]+") || facebookAdd.startsWith(".") || facebookAdd.contains("..")) {
            binding.etLink2.requestFocus();
            binding.etLink2.setError("Nome de usuário inválido para o Facebook!");
            return false;
        } else if (!TwitterAdd.matches("[a-zA-Z0-9._]+") && binding.etLink3.getText().toString().length() >= 1) {
            binding.etLink3.requestFocus();
            binding.etLink3.setError("Nome de usuário inválido para o Twitter!");
            return false;
        }

        return true;

    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void editarViaApi() {
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

        picasso.load(securityPreferences.getAuthToken(TaskConstants.PATH.URL)+"/usuarios/fotoperfil/" + usuario.getNomeFotoPerfil()).noFade().placeholder(R.drawable.img_not_found_little)
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