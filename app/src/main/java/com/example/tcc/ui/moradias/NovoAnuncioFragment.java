package com.example.tcc.ui.moradias;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
import com.example.tcc.ui.adapter.ImagemAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

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
    private List<MultipartBody.Part> imagemLista = new ArrayList<>();
    private List<Uri> imageUriList = new ArrayList<>();
    private ImagemAdapter imagemAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNovoAnuncioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
       // iniciarViews();
        //Post post = new Post();
        RetrofitConfigCepApi retrofitConfigCepApi = new RetrofitConfigCepApi();

        Post post = new Post();
        PostMoradia moradia = new PostMoradia();
        Detalhes detalhesMoradia = new Detalhes();
        post.setPostMoradia(moradia);
        post.getPostMoradia().setDetalhesMoradia(detalhesMoradia);
        post.getPostMoradia().setFotos(new ArrayList<>());
        imagemAdapter = new ImagemAdapter(container.getContext());

        imagemAdapter.setImagem(imageUriList);

        binding.ivFotosUsuario.setAdapter(imagemAdapter);
        binding.ivFotosUsuario.setInfinite(true);
        // binding.ivFotosUsuario.set3DItem(true);
        binding.ivFotosUsuario.setFlat(true);


        registerResult();

        configAdapter();
        binding.btAddImg.setOnClickListener(view -> pickImage());  //coloca uma imagem na lista uri e atualiza o rv das imgs

        binding.btRemoveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               /* if ( imageUriList.size() != 0) {
                    if(imageUriList.get(binding.ivFotosUsuario.getSelectedPosition()) !=null){
                        imageUriList.remove(binding.ivFotosUsuario.getSelectedPosition());
                        configAdapter();
                    }


                }*/


                if (!imageUriList.isEmpty() && binding.ivFotosUsuario.getSelectedPosition() >= 0 && binding.ivFotosUsuario.getSelectedPosition() < imageUriList.size()) {

                    imageUriList.remove(binding.ivFotosUsuario.getSelectedPosition());
                    if (imageUriList.isEmpty()){
                       // imgNotFound = Uri.parse("android.resource://"+  getContext().getPackageName()  + "/" + R.drawable.img_not_found_little);
                        imageUriList.add(imgNotFound);
                       // imagemAdapter.setImagem(imageUriList);
                    }
                    imagemAdapter.setImagem(imageUriList);

                }



            }
        });

        binding.btPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setarDados(post, root);

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

                            salvarPostagemViaApi(post);

                        }
                        else {

                        }

                    }

                    @Override
                    public void onFailure(Call<CepApi> call, Throwable t) {

                    }
                });

            }
        });


        // Inflate the layout for this fragment
        return root;
    }

    private void configAdapter(){
        imagemAdapter.setImagem(imageUriList);

        Log.e("IMAGEM LISTA", ": "+imageUriList.toString());

       // binding.ivFotosUsuario.setAdapter(imagemAdapter);
       // binding.ivFotosUsuario.setInfinite(true);
       // binding.ivFotosUsuario.set3DItem(true);
       // binding.ivFotosUsuario.setFlat(true);
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
                    imageUri = result.getData().getData();
                    imageUriList.add(imageUri);

                    if(imageUriList.get(0) == imgNotFound){
                        imageUriList.remove(0);
                    }

                    Log.e("IMG", ": " + imageUri.toString());
                   // binding.ivFotosUsuario.setImageURI(imageUri);
                    configAdapter();

                    File imageFile = new File(getRealPathFromUri(getContext(), imageUri));

                   // RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
                    MultipartBody.Part imagemPart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
                    imagemLista.add(imagemPart);
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


    private void salvarPostagemViaApi(Post post) {
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



                    for(int i = 0; i < imagemLista.size(); i++){
                        SalvarImagemViaApi(retrofitConfig, idMoradia, imagemLista.get(i));
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

    private void setarDados(Post post,View root){
        post.getPostMoradia().setEndereco(binding.etRuaUsuario.getText().toString());
        post.getPostMoradia().setNumCasa(Integer.valueOf(binding.etNumCasaUsuario.getText().toString()));
        post.setComentario(binding.etComentarioAnuncio.getText().toString());
        post.getPostMoradia().getDetalhesMoradia().setMoradores(Integer.valueOf(binding.etNumMoradoresUsuario.getText().toString()));
        post.getPostMoradia().setValorAluguel(Double.valueOf(binding.etAluguelUsuario.getText().toString()));

        int escolhaAp = verificarChip(root.findViewById(R.id.chip_ap),  R.id.chip_ap_apart, R.id.chip_ap_casa);
        adicionarAp(post, escolhaAp);
        int escolhaPet = verificarChip(root.findViewById(R.id.chips_pet),  R.id.chips_pet_sim, R.id.chips_pet_nao);
        adicionarPet(post, escolhaPet);
        int escolhaGaragem = verificarChip(root.findViewById(R.id.chips_garagem),  R.id.chips_garagem_sim, R.id.chips_garagem_nao);
        adicionarGaragem(post, escolhaGaragem);
        int escolhaGenero = verificarChip3Opcoes(binding.chipsGenero, R.id.chips_genero_masc, R.id.chips_genero_fem, R.id.chips_genero_misto);
        adicionarGenero(post, escolhaGenero);
    }

    private int verificarChip(ChipGroup chip, int opcao1, int opcao2)  {
        int escolha= 0;
        int selectedChipId = chip.getCheckedChipId(); // Obtém o ID do Chip selecionado

        if (selectedChipId != View.NO_ID) {
            Chip selectedChip = chip.findViewById(selectedChipId); // Obtém a referência ao Chip selecionado
            if (selectedChip.getId() == opcao1) {
                escolha = 1;
            } else if (selectedChip.getId() == opcao2) {
                escolha = 2;
            }
        } else {
            escolha = 0;
        }

        return escolha;
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

    private void adicionarAp(Post post, int escolha){
        if(escolha == 1){
            post.getPostMoradia().setTipoResidencia(false);
        }
        else if (escolha == 2){
            post.getPostMoradia().setTipoResidencia(true);
        }
    }

    private void adicionarPet(Post post, int escolha){
        if(escolha == 1){
            post.getPostMoradia().getDetalhesMoradia().setPets(true);
        }
        else if (escolha == 2){
            post.getPostMoradia().getDetalhesMoradia().setPets(false);
        }
    }

    private void adicionarGaragem(Post post, int escolha){
        if(escolha == 1){
            post.getPostMoradia().getDetalhesMoradia().setGaragem(true);
        }
        else if (escolha == 2){
            post.getPostMoradia().getDetalhesMoradia().setGaragem(false);
        }
    }

    private void adicionarGenero(Post post, int escolha){
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