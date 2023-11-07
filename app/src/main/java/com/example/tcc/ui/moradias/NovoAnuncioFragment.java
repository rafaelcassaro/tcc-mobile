package com.example.tcc.ui.moradias;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.databinding.FragmentNovoAnuncioBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Detalhes;
import com.example.tcc.network.entities.Fotos;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.entities.PostMoradia;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.ImagemEditarMoradiaAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NovoAnuncioFragment extends Fragment {

    private FragmentNovoAnuncioBinding binding;
    private ActivityResultLauncher<Intent> resultLauncher;
    private ImageView imageView;
    private Uri imageUri;
    private Uri imgNotFound;
    private List<MultipartBody.Part> multiPartImgList = new ArrayList<>();
   // private List<Uri> imageUriList = new ArrayList<>();
    private Post post;
    private ImagemEditarMoradiaAdapter imagemAdapter;
    private Picasso picasso ;
    private RetrofitConfigCepApi retrofitConfigCepApi;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNovoAnuncioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
       // iniciarViews();
        //Post post = new Post();
        retrofitConfigCepApi = new RetrofitConfigCepApi();

        post = new Post();
        PostMoradia moradia = new PostMoradia();
        Detalhes detalhesMoradia = new Detalhes();
        post.setPostMoradia(moradia);
        post.getPostMoradia().setDetalhesMoradia(detalhesMoradia);
        post.getPostMoradia().setFotos(new ArrayList<>());

        configAdapter(getContext());
        multiPartImgList.clear();

        registerResult();
        getCheckButtons();
        binding.etCepUsuario.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                pegarCepViaApi();

                return false;
            }
        });





        binding.ibAdicionarImg.setOnClickListener(view -> pickImage());  //coloca uma imagem na lista uri e atualiza o rv das imgs

        binding.ibRemoverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imagemAdapter.removeImg(binding.crvFotosMoradia.getSelectedPosition());

                if(!multiPartImgList.isEmpty() && binding.crvFotosMoradia.getSelectedPosition() >= 0 && binding.crvFotosMoradia.getSelectedPosition() < multiPartImgList.size()) {
                    multiPartImgList.remove(binding.crvFotosMoradia.getSelectedPosition());
                }

                imagemAdapter.addImgvazia();
                binding.crvFotosMoradia.smoothScrollToPosition(0);

            }
        });

        binding.btPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                salvarPostagemViaApi();


            }
        });


        // Inflate the layout for this fragment
        return root;
    }

    private void getCheckButtons(){
        binding.cbGaragem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cbGaragem.isChecked()){
                    post.getPostMoradia().getDetalhesMoradia().setGaragem(true);
                    Log.e("onCheckboxClicked","CheckBoxON cb_garagem "+ post.getPostMoradia().getDetalhesMoradia().isGaragem());
                }
                else {
                    post.getPostMoradia().getDetalhesMoradia().setGaragem(false);
                    Log.e("onCheckboxClicked","CheckBoxOFF cb_garagem "+post.getPostMoradia().getDetalhesMoradia().isGaragem());
                }
            }
        });

        binding.cbResidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( binding.cbResidencia.isChecked()){
                    post.getPostMoradia().setTipoResidencia(true);
                    Log.e("onCheckboxClicked","CheckBoxON cb_residencia "+post.getPostMoradia().isTipoResidencia());
                }
                else {
                    post.getPostMoradia().setTipoResidencia(false);
                    Log.e("onCheckboxClicked","CheckBoxOFF cb_residencia "+post.getPostMoradia().isTipoResidencia());
                }

            }
        });

        binding.cbPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( binding.cbPets.isChecked()){
                    post.getPostMoradia().getDetalhesMoradia().setPets(true);
                    Log.e("onCheckboxClicked","CheckBoxON cb_residencia "+post.getPostMoradia().isTipoResidencia());
                }
                else {
                    post.getPostMoradia().getDetalhesMoradia().setPets(false);
                    Log.e("onCheckboxClicked","CheckBoxOFF cb_residencia "+post.getPostMoradia().isTipoResidencia());
                }

            }
        });

        binding.cbQuarto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( binding.cbQuarto.isChecked()){
                    post.getPostMoradia().getDetalhesMoradia().setQuarto(true);
                    Log.e("onCheckboxClicked","CheckBoxON cb_residencia "+post.getPostMoradia().isTipoResidencia());
                }
                else {
                    post.getPostMoradia().getDetalhesMoradia().setQuarto(false);
                    Log.e("onCheckboxClicked","CheckBoxOFF cb_residencia "+post.getPostMoradia().isTipoResidencia());
                }

            }
        });



    }

    private void pegarCepViaApi() {
        Integer cep = Integer.valueOf(binding.etCepUsuario.getText().toString());
        Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
        callApi.enqueue(new Callback<CepApi>() {
            @Override
            public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                if (response.isSuccessful()){
                    CepApi cepApiDados = response.body();

                    Log.e("VERIFICAR POST ", "deu bom "+ cepApiDados.toString());
                    post.setCidade(cepApiDados.getCity());
                    post.setEstado(cepApiDados.getState());
                    post.setCep(cepApiDados.getCep());
                    post.setQntdDenuncia(0);
                    binding.tvCidadeUsuario.setText(cepApiDados.getCity());
                    binding.tvEstadoUsuario.setText(cepApiDados.getState());



                }
                else {

                }

            }

            @Override
            public void onFailure(Call<CepApi> call, Throwable t) {

            }
        });
    }

    private void configAdapter(Context context){
        imagemAdapter = new ImagemEditarMoradiaAdapter(context);
        binding.crvFotosMoradia.setAdapter(imagemAdapter);
        imagemAdapter.addImgvazia();
        binding.crvFotosMoradia.setInfinite(true);
        binding.crvFotosMoradia.setFlat(true);

    }

    private OkHttpClient getOkHttpClientWithAuthorization(final String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                okhttp3.Request original = chain.request();
                Request request = original
                        .newBuilder()
                        .addHeader("Authorization", token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        return httpClient.build();

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

    private void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);

    }

    private void registerResult(){
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                try {
                    //imageUri = result.getData().getData();

                   // binding.ivFotosUsuario.setImageURI(imageUri);

                    File imageFile = new File(getRealPathFromUri(getContext(), result.getData().getData()));
                   // RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
                    MultipartBody.Part imagemPart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
                    multiPartImgList.add(imagemPart);
                    imagemAdapter.addImagem(result.getData().getData());
                    // Enviar a imagem usando Retrofit

                }
                catch (Exception e){
                    Log.e("sem img", "sem");
                }

            }
        });
    }



    private void SalvarImagemViaApi(RetrofitConfig retrofitConfig,Long id, MultipartBody.Part imagem){

        Call<Fotos> call = retrofitConfig.getImageService().uploadImage(imagem, id);
        call.enqueue(new Callback<Fotos>() {
            @Override
            public void onResponse(Call<Fotos> call, Response<Fotos> response) {
                // Lidar com a resposta do servidor, se houver
                if (response.isSuccessful()) {
                    Fotos resposta = response.body();
                    // Faça algo com a resposta do servidor, se necessário
                    Log.e("json bom", ": " + resposta);
                }
                else {
                    Log.e("json rum", ": " + response.body());
                }
            }

            @Override
            public void onFailure(Call<Fotos> call, Throwable t) {
                // Lidar com erros na requisição
                Log.e("JSON ERRO", ": " + t.getMessage());
            }
        });
    }


    private void salvarPostagemViaApi() {
        setarDados();
        SecurityPreferences securityPreferences = new SecurityPreferences(getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        post.getPostMoradia().setFotos(new ArrayList<>());

        Call<Post> callSave = retrofitConfig.getPostService().createPost(post, id);
        callSave.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()){
                    Post tempoPost = response.body();
                    Long idMoradia = tempoPost.getPostMoradia().getId();
                    Log.e("NovoAnuncio", "deu bom" + response.body().toString());
                    Log.e("NovoAnuncio", "IDIDIDI" + id);



                    for(int i = 0; i < multiPartImgList.size(); i++){
                        SalvarImagemViaApi(retrofitConfig, idMoradia, multiPartImgList.get(i));
                    }



                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("novo_postMoradia_tag", "editPostTag");
                    startActivity(intent);
                }
                else{
                    Log.e("NovoAnuncio", "deu bom ruim");
                }



            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("NovoAnuncio", "deu ruim");
            }


        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }


    //-------------------BOTOES------------------------------------------

    private void setarDados(){
        post.getPostMoradia().setEndereco(binding.etRuaUsuario.getText().toString());
        post.getPostMoradia().setNumCasa(Integer.valueOf(binding.etNumCasaUsuario.getText().toString()));
        post.setComentario(binding.etComentarioAnuncio.getText().toString());
        post.getPostMoradia().getDetalhesMoradia().setMoradores(Integer.valueOf(binding.etNumMoradoresUsuario.getText().toString()));
        post.getPostMoradia().setValorAluguel(Double.valueOf(binding.etAluguelUsuario.getText().toString()));

        int escolhaGenero = verificarChip3Opcoes(binding.chipsGenero, R.id.chips_genero_masc, R.id.chips_genero_fem, R.id.chips_genero_misto);
        adicionarGenero(escolhaGenero);
    }



    private int verificarChip3Opcoes(ChipGroup chip, int opcao1, int opcao2,int opcao3)  {
        int escolha= 0;

        int selectedChipId = chip.getCheckedChipId(); // Obtém o ID do Chip selecionado
        if (selectedChipId != View.NO_ID) {
            Chip selectedChip = chip.findViewById(selectedChipId); // Obtém a referência ao Chip selecionado
            // Verifica qual Chip foi selecionado com base no ID
            if (selectedChip.getId() == opcao1) {
                escolha = 1;
            } else if (selectedChip.getId() == opcao2) {
                escolha = 2;
            }
            else if (selectedChip.getId() == opcao3) {
                escolha = 3;
            }
        } else {
            escolha = 0;
        }
        return escolha;
    }

    private void adicionarGenero( int escolha){
        if(escolha == 1){
            post.getPostMoradia().getDetalhesMoradia().setGeneroMoradia("MASCULINA");
        }
        else if (escolha == 2){
            post.getPostMoradia().getDetalhesMoradia().setGeneroMoradia("FEMININA");
        }
        else if (escolha == 3){
            post.getPostMoradia().getDetalhesMoradia().setGeneroMoradia("MISTA");
        }
    }




}