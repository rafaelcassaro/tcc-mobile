package com.example.tcc.ui.moradias;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Fotos;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.ImagemAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

public class MoradiaUsuarioEditar extends AppCompatActivity {

    private EditText cepEt;
    private EditText ruaEt;
    private EditText numCasaEt;
    private EditText comentarioEt;
    private EditText numMoradoresEt;
    private EditText aluguelEt;
    private ChipGroup chipAp;
    private ChipGroup chipPet;
    private ChipGroup chipGaragem;
    private ChipGroup chipGeneroRep;
    private Button botaoEditar;
    private Button btAddImg;
    private Button btRemoveImg;

    private ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUri;
    private Uri imgNotFound;
    private Uri imgPicasso;
    private CarouselRecyclerview recyclerview;
    private List<MultipartBody.Part> imagemLista = new ArrayList<>();
    private List<Uri> imageUriList = new ArrayList<>();
    private ImagemAdapter imagemAdapter;
    private boolean carregarImgs = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moradia_usuario_editar_layout);
        iniciarViews();
        //Pegar dados do post clicado com a constante EXTRA_SHOW
        Post post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);
        final RetrofitConfigCepApi retrofitConfigCepApi = new RetrofitConfigCepApi();

        imagemAdapter = new ImagemAdapter(this);
        imagemAdapter.setImagem(imageUriList);
        recyclerview.setAdapter(imagemAdapter);
        recyclerview.setInfinite(true);
        recyclerview.setFlat(true);

        carregarImagemPost(post);

        //PEGAR O TOKEN SALVO E APLICAR NA CONEXAO COM O END-POINT "retrofitConfig"


        //TRANSFERIR OS VALORES INT E DOUBLE DO POST PARA OS ET DA VIEW
        int numMoradores = Integer.valueOf(post.getPostMoradia().getDetalhesMoradia().getMoradores());
        int numCasa = Integer.valueOf(post.getPostMoradia().getNumCasa());
        double valorAluguel = Double.valueOf(post.getPostMoradia().getValorAluguel());


        comentarioEt.setText(post.getComentario());
        ruaEt.setText(post.getPostMoradia().getEndereco());
        numMoradoresEt.setText(String.valueOf(numMoradores));
        numCasaEt.setText(String.valueOf(numCasa));
        aluguelEt.setText(String.valueOf(valorAluguel));
        cepEt.setText(String.valueOf(post.getCep()));

        selecionarChips(post);
        registerResult();


        btAddImg.setOnClickListener(view -> pickImage());

        btRemoveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!imageUriList.isEmpty() && recyclerview.getSelectedPosition() >= 0 && recyclerview.getSelectedPosition() < imageUriList.size()) {

                    imageUriList.remove(recyclerview.getSelectedPosition());
                    if (imageUriList.isEmpty()){
                        // imgNotFound = Uri.parse("android.resource://"+  getContext().getPackageName()  + "/" + R.drawable.img_not_found_little);
                        imageUriList.add(imgNotFound);
                        // imagemAdapter.setImagem(imageUriList);
                    }
                    imagemAdapter.setImagem(imageUriList);

                }
            }
        });

        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("true ou false", "valor: "+ verificarChip(chipAp,  R.id.chip_ap_apart, R.id.chip_ap_casa));
                setarDados(post);


                Integer cep = Integer.valueOf(cepEt.getText().toString());
                Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
                callApi.enqueue(new Callback<CepApi>() {
                    @Override
                    public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                        if (response.isSuccessful()){
                            CepApi cepApiDados = response.body();
                            post.setCidade(cepApiDados.getCity());
                            post.setEstado(cepApiDados.getState());
                            post.setCep(cepApiDados.getCep());
                            EditarPostViaApi(post);
                            Log.e("BOTAO EDITAR CEP", "RETORNO BOM: "+ response.errorBody());

                        }
                        else {
                            Log.e("BOTAO EDITAR CEP", "RETORNO ERROR: "+ response.errorBody());

                        }

                    }

                    @Override
                    public void onFailure(Call<CepApi> call, Throwable t) {
                        Log.e("BOTAO EDITAR CEP", "INTERNAL ERROR: "+ t.getMessage().toString());

                    }
                });

                //Call<Void> call= new RetrofitConfig(new SecurityPreferences(getApplicationContext()).getAuthToken(TaskConstants.SHARED.TOKEN_KEY))
                //        .getPostService().updatePost(editPost,editPost.getUsuario().getId(), editPost.getPostMoradia().getId());


            }
        });

    }


    private File createImageFileFromBitmap(Bitmap bitmap) {
        File imageFile = null;
        FileOutputStream fos = null;

        try {
            // Crie um arquivo temporário no diretório de armazenamento externo
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            imageFile = File.createTempFile("image", ".jpg", storageDir);

            // Salve o Bitmap como um arquivo JPEG
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            // Lidar com exceções, se ocorrerem
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imageFile;
    }

    private void EditarPostViaApi(Post post) {
        SecurityPreferences securityPreferences = new SecurityPreferences(getApplicationContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        Long idMoradia = post.getPostMoradia().getId();

        //DAR UPDATE NO POST SELECIONADO
        Call<Void> call=retrofitConfig.getPostService().updatePostMoradia(post, id, idMoradia);
        Log.e("VALOR ID POSTMORADA", "deu ruim"+ post.getPostMoradia().getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    // Crie uma instância do seu Fragment

                    for(int i = 0; i < imagemLista.size(); i++){
                        salvarImagemViaApi(retrofitConfig, idMoradia, imagemLista.get(i));
                    }



                    Intent intent = new Intent(MoradiaUsuarioEditar.this, MainActivity.class);
                    intent.putExtra("editar_postMoradia_tag", "editPostTag");
                    startActivity(intent);

                    Log.e("EDITAR POSTMORADIA", "deu bom");
                }
                else{
                    Log.e("EDITAR POSTMORADIA", "deu ruim"+ post.getDataPost());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EDITAR POSTMORADIA", "deu erro:"+ t);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //retrofitConfigCepApi = null;
        //securityPreferences = null;
        //retrofitConfig = null;

    }

    private void iniciarViews(){
        cepEt = findViewById(R.id.et_cep_usuario);
        ruaEt = findViewById(R.id.et_rua_usuario) ;
        numCasaEt = findViewById(R.id.et_num_casa_usuario);
        comentarioEt= findViewById(R.id.et_comentario_anuncio) ;
        numMoradoresEt= findViewById(R.id.et_num_moradores_usuario) ;
        aluguelEt= findViewById(R.id.et_aluguel_usuario) ;
        chipAp = findViewById(R.id.chip_ap) ;
        chipPet = findViewById(R.id.chips_pet) ;
        chipGaragem = findViewById(R.id.chips_garagem) ;
        chipGeneroRep = findViewById(R.id.chips_genero) ;
        botaoEditar = findViewById(R.id.bt_publicar_edicao);
        btAddImg = findViewById(R.id.bt_add_img);
        btRemoveImg = findViewById(R.id.bt_remove_img);
        recyclerview = findViewById(R.id.iv_fotos_usuario);
    }

    private void setarDados(Post post){
        post.getPostMoradia().setEndereco(ruaEt.getText().toString());
        post.getPostMoradia().setNumCasa(Integer.valueOf(numCasaEt.getText().toString()));
        post.setComentario(comentarioEt.getText().toString());
        post.getPostMoradia().getDetalhesMoradia().setMoradores(Integer.valueOf(numMoradoresEt.getText().toString()));
        post.getPostMoradia().setValorAluguel(Double.valueOf(aluguelEt.getText().toString()));

        int escolhaAp = verificarChip(chipAp,  R.id.chip_ap_apart, R.id.chip_ap_casa);
        adicionarAp(post, escolhaAp);
        int escolhaPet = verificarChip(chipPet,  R.id.chips_pet_sim, R.id.chips_pet_nao);
        adicionarPet(post, escolhaPet);
        int escolhaGaragem = verificarChip(chipGaragem,  R.id.chips_garagem_sim, R.id.chips_garagem_nao);
        adicionarGaragem(post, escolhaGaragem);
        int escolhaGenero = verificarChip3Opcoes(chipGeneroRep, R.id.chips_genero_masc, R.id.chips_genero_fem, R.id.chips_genero_misto);
        adicionarGenero(post, escolhaGenero);
    }

    //---------------------LIDAR COM IMAGEM-----------------------------------

    private void carregarImagemPost(Post post){


        if(!carregarImgs) {
            carregarImgs = true;
            SecurityPreferences securityPreferences = new SecurityPreferences(MoradiaUsuarioEditar.this);
            Picasso picasso = new Picasso.Builder(MoradiaUsuarioEditar.this)
                    .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                    .build();

            //  picasso.load("http://192.168.1.107:8080/imagens/Imagem1.jpg").into(target);
            Log.e("IMG PICAASO", ": " + imgPicasso);

            int numImagens = post.getPostMoradia().getFotos().size();

            for (int i = 0; i < numImagens; i++) {
                String nomeImg = post.getPostMoradia().getFotos().get(i).getNomeFoto();
                picasso.load("http://192.168.1.107:8080/imagens/" + nomeImg).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        File imageFile = createImageFileFromBitmap(bitmap);

                        if (imageFile != null) {
                            imgPicasso = Uri.fromFile(imageFile);
                            imageUriList.add(imgPicasso);
                            imagemAdapter.setImagem(imageUriList);

                        } else {

                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }
    }

    private void salvarImagemViaApi(RetrofitConfig retrofitConfig,Long id, MultipartBody.Part imagem){

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
                            imagemAdapter.setImagem(imageUriList);

                            File imageFile = new File(getRealPathFromUri(MoradiaUsuarioEditar.this, imageUri));

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

    //-------------------BOTOES------------------------------------------

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

    private int verificarChip3Opcoes(ChipGroup chip, int opcao1, int opcao2,int opcao3)  {
        int escolha= 0;
        ChipGroup chipGroup = chip;
        int selectedChipId = chipGroup.getCheckedChipId(); // Obtém o ID do Chip selecionado
        if (selectedChipId != View.NO_ID) {
            Chip selectedChip = findViewById(selectedChipId); // Obtém a referência ao Chip selecionado
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

    private int verificarChip(ChipGroup chip, int opcao1, int opcao2)  {
        int escolha= 0;
        //Log.e("CHIPS", "OPCAO1 ID"+ opcao1);
        //Log.e("CHIPS", "OPCAO2 ID"+ opcao2);

        ChipGroup chipGroup = chip;
        int selectedChipId = chipGroup.getCheckedChipId(); // Obtém o ID do Chip selecionado
        Log.e("CHIPS", "OPCAO2 ID"+ chip);
        Log.e("CHIPS", "OPCAO2 ID"+ selectedChipId);
        Log.e("CHIPS", "OPCAO2 ID"+ chip.getCheckedChipId());

        if (selectedChipId != View.NO_ID) {
            Chip selectedChip = findViewById(selectedChipId); // Obtém a referência ao Chip selecionado
           // Log.e("CHIPS", "CHIPGROUP: "+ findViewById(selectedChipId));
            // Verifica qual Chip foi selecionado com base no ID
            if (selectedChip.getId() == opcao1) {
                escolha = 1;
             //   Log.e("CHIPS", "ESCOLHAOP1: "+ selectedChip.getId());
            } else if (selectedChip.getId() == opcao2) {
                escolha = 2;
              //  Log.e("CHIPS", "ESCOLHAOP2: "+ selectedChip.getId());
            }
        } else {
            escolha = 0;
        }
        //Log.e("CHIPS", "ESCOLHA FINAL: "+ selectedChipId);
       // Log.e("CHIPS", "ESCOLHA FINAL: "+ escolha);
        return escolha;
    }

    private void selecionarChips(Post post){
        //tipo da residencia
        if(post.getPostMoradia().isTipoResidencia()){
            Chip chipSelecionado = (Chip) chipAp.getChildAt(1);
            chipSelecionado.setChecked(true);
        }
        else{
            Chip chipSelecionado = (Chip) chipAp.getChildAt(0);
            chipSelecionado.setChecked(true);
        }
        //pets
        if(post.getPostMoradia().getDetalhesMoradia().isPets() == true){
            Chip chipSelecionado = (Chip) chipPet.getChildAt(0);
            chipSelecionado.setChecked(true);
        }
        else{
            Chip chipSelecionado = (Chip) chipPet.getChildAt(1);
            chipSelecionado.setChecked(true);
        }
        //garagem
        if(post.getPostMoradia().getDetalhesMoradia().isGaragem() == true){
            Chip chipSelecionado = (Chip) chipGaragem.getChildAt(0);
            chipSelecionado.setChecked(true);
        }
        else{
            Chip chipSelecionado = (Chip) chipGaragem.getChildAt(1);
            chipSelecionado.setChecked(true);
        }
        //genero
        if(post.getPostMoradia().getDetalhesMoradia().getGeneroMoradia().equals("MASCULINA")){
            Chip chipSelecionado = (Chip) chipGeneroRep.getChildAt(0);
            chipSelecionado.setChecked(true);
        }
        else if(post.getPostMoradia().getDetalhesMoradia().getGeneroMoradia().equals("FEMININA")){
            Chip chipSelecionado = (Chip) chipGeneroRep.getChildAt(1);
            chipSelecionado.setChecked(true);
        }
        else{
            Chip chipSelecionado = (Chip) chipGeneroRep.getChildAt(2);
            chipSelecionado.setChecked(true);
        }


    }

}