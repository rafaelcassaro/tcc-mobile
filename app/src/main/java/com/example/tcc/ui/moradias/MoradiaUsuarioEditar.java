package com.example.tcc.ui.moradias;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Fotos;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.ImagemEditarMoradiaAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiaUsuarioEditar extends AppCompatActivity {

    private EditText cepEt, ruaEt, numCasaEt, comentarioEt, numMoradoresEt, aluguelEt;
    private ChipGroup  chipGeneroRep;
    private CheckBox petsCb, garagemCb, quartoCb, residenciaCb;
    private Button botaoEditar;
    private ImageButton btAddImg, btRemoveImg;
    private ImageView ivTeste;
    private TextView cidadeTv, estadoTv;

    private ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUri;
    private Uri imgNotFound;
    //private Uri imgPicasso;
    private CarouselRecyclerview recyclerview;
    private List<MultipartBody.Part> multiPartImgList = new ArrayList<>();
    private ImagemEditarMoradiaAdapter imagemAdapter;
    // private boolean carregarImgs = false;
    private SecurityPreferences securityPreferences;//= new SecurityPreferences(MoradiaUsuarioEditar.this);
    private Picasso picasso;//= new Picasso.Builder(MoradiaUsuarioEditar.this)
    //.downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
    // .build();
    private RetrofitConfigCepApi retrofitConfigCepApi;//= new RetrofitConfigCepApi();
    private Uri text;
    private Post editedPost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moradia_usuario_editar_layout);
        iniciarViews();
        //Pegar dados do post clicado com a constante EXTRA_SHOW
        editedPost = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);

        securityPreferences = new SecurityPreferences(MoradiaUsuarioEditar.this);
        picasso = new Picasso.Builder(MoradiaUsuarioEditar.this)
                .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                .build();
        retrofitConfigCepApi = new RetrofitConfigCepApi();

        configAdapter(this);
        multiPartImgList.clear();


        ImageView backButton = findViewById(R.id.iv_voltar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoradiaUsuarioEditar.super.onBackPressed();
            }
        });

        //PEGAR O TOKEN SALVO E APLICAR NA CONEXAO COM O END-POINT "retrofitConfig"


        SecurityPreferences securityPreferences = new SecurityPreferences(getApplicationContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        pegarImgViaApi(retrofitConfig, this);
        // ivTeste.setImageURI(text);

        //TRANSFERIR OS VALORES INT E DOUBLE DO POST PARA OS ET DA VIEW
        //------------------------------------------------setar dados pre-vindos do post no front-------------------------------------
        int numMoradores = Integer.valueOf(editedPost.getPostMoradia().getDetalhesMoradia().getMoradores());
        int numCasa = Integer.valueOf(editedPost.getPostMoradia().getNumCasa());
        double valorAluguel = Double.valueOf(editedPost.getPostMoradia().getValorAluguel());

        comentarioEt.setText(editedPost.getComentario());
        ruaEt.setText(editedPost.getPostMoradia().getEndereco());
        numMoradoresEt.setText(String.valueOf(numMoradores));
        numCasaEt.setText(String.valueOf(numCasa));
        aluguelEt.setText(String.valueOf(valorAluguel));
        cepEt.setText(String.valueOf(editedPost.getCep()));
        cidadeTv.setText(editedPost.getCidade());
        estadoTv.setText(editedPost.getEstado());

        pegarCheckboxPrevio();
        selecionarChips();
        //------------------------------------------------setar dados pre-vindos do post no front-------------------------------------

        registerResult();



        cepEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.e("setOnEditorActionListener", "onEditorAction: ");

                    // Ação a ser executada quando o usuário pressionar "Submit" no teclado
                    // Você pode chamar um método ou executar qualquer ação desejada aqui
                    pegarCepViaApi();
                    Log.e("setOnEditorActionListener", "onEditorAction: if");
                    //return true; // Retorna true para indicar que o evento foi tratado

                Log.e("setOnEditorActionListener", "onEditorAction: else");
                return false;
            }
        });

        btAddImg.setOnClickListener(view -> pickImage());

        btRemoveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("POSICAO RV", "btRemoveImg: " + recyclerview.getSelectedPosition());

                imagemAdapter.removeImg(recyclerview.getSelectedPosition());

                if (!multiPartImgList.isEmpty() && recyclerview.getSelectedPosition() >= 0 && recyclerview.getSelectedPosition() < multiPartImgList.size()) {
                    multiPartImgList.remove(recyclerview.getSelectedPosition());
                }

                imagemAdapter.addImgvazia();
                recyclerview.smoothScrollToPosition(0);

            }
        });

        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("true ou false", "valor: "+ verificarChip(chipAp,  R.id.chip_ap_apart, R.id.chip_ap_casa));

                EditarPostViaApi();

               /* Integer cep = Integer.valueOf(cepEt.getText().toString());
                Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
                callApi.enqueue(new Callback<CepApi>() {
                    @Override
                    public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                        if (response.isSuccessful()) {

                            CepApi cepApiDados = response.body();
                            editedPost.setCidade(cepApiDados.getCity());
                            editedPost.setEstado(cepApiDados.getState());
                            editedPost.setCep(cepApiDados.getCep());
                            cidadeTv.setText(cepApiDados.getCity());
                            estadoTv.setText(cepApiDados.getState());
                            //EditarPostViaApi(post);
                            Log.e("BOTAO EDITAR CEP", "RETORNO BOM: " + response.errorBody());

                        } else {

                            Log.e("BOTAO EDITAR CEP", "RETORNO ERROR: " + response.errorBody());

                        }

                    }

                    @Override
                    public void onFailure(Call<CepApi> call, Throwable t) {
                        Log.e("BOTAO EDITAR CEP", "INTERNAL ERROR: " + t.getMessage().toString());

                    }
                });*/

                //Call<Void> call= new RetrofitConfig(new SecurityPreferences(getApplicationContext()).getAuthToken(TaskConstants.SHARED.TOKEN_KEY))
                //        .getPostService().updatePost(editPost,editPost.getUsuario().getId(), editPost.getPostMoradia().getId());


            }
        });

    }

    //================================================FIM DO ONCREATE================================================
    private void configAdapter(Context context) {

        imagemAdapter = new ImagemEditarMoradiaAdapter(context);
        // imagemAdapter.setImagem(imageUriList);
        //imagemAdapter.setImagem(imageUriList);
        recyclerview.setAdapter(imagemAdapter);
        imagemAdapter.addImgvazia();
        recyclerview.setInfinite(true);
        recyclerview.setFlat(true);


    }

    private void pegarCepViaApi(){

        Integer cep = Integer.valueOf(cepEt.getText().toString());
        Call<CepApi> callApi = retrofitConfigCepApi.getCepService().getCidadeEstadoByCEP(cep);
        callApi.enqueue(new Callback<CepApi>() {
            @Override
            public void onResponse(Call<CepApi> call, Response<CepApi> response) {
                if (response.isSuccessful()) {

                    CepApi cepApiDados = response.body();
                    editedPost.setCidade(cepApiDados.getCity());
                    editedPost.setEstado(cepApiDados.getState());
                    editedPost.setCep(cepApiDados.getCep());
                    cidadeTv.setText(cepApiDados.getCity());
                    estadoTv.setText(cepApiDados.getState());
                    //EditarPostViaApi(post);
                    Log.e("BOTAO EDITAR CEP", "RETORNO BOM: " + response.errorBody());

                } else {

                    Log.e("BOTAO EDITAR CEP", "RETORNO ERROR: " + response.errorBody());

                }

            }

            @Override
            public void onFailure(Call<CepApi> call, Throwable t) {
                Log.e("BOTAO EDITAR CEP", "INTERNAL ERROR: " + t.getMessage().toString());

            }
        });
    }

    private void pegarCheckboxPrevio(){
        if (editedPost.getPostMoradia().getDetalhesMoradia().isGaragem()){
            garagemCb.setChecked(true);
        }
        if (editedPost.getPostMoradia().getDetalhesMoradia().isPets()){
            petsCb.setChecked(true);
        }
        if (editedPost.getPostMoradia().getDetalhesMoradia().isQuarto()){
            quartoCb.setChecked(true);
        }
        if (editedPost.getPostMoradia().isTipoResidencia()){
            residenciaCb.setChecked(true);
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        if (view.getId() == R.id.cb_pets){
            if (checked){
                editedPost.getPostMoradia().getDetalhesMoradia().setPets(true);
                Log.e("onCheckboxClicked","CheckBoxON cb_pets");
            }
            else {
                editedPost.getPostMoradia().getDetalhesMoradia().setPets(false);
                Log.e("onCheckboxClicked","CheckBoxOFF cb_pets");
            }}
        if (view.getId() == R.id.cb_garagem){
            if (checked){
                editedPost.getPostMoradia().getDetalhesMoradia().setGaragem(true);
                Log.e("onCheckboxClicked","CheckBoxON cb_garagem");
            }
            else {
                editedPost.getPostMoradia().getDetalhesMoradia().setGaragem(false);
                Log.e("onCheckboxClicked","CheckBoxOFF cb_garagem");
            }}
        if (view.getId() == R.id.cb_quarto){
            if (checked){
                editedPost.getPostMoradia().getDetalhesMoradia().setQuarto(true);
                Log.e("onCheckboxClicked","CheckBoxON cb_quarto");
            }
            else {
                editedPost.getPostMoradia().getDetalhesMoradia().setQuarto(false);
                Log.e("onCheckboxClicked","CheckBoxOFF cb_quarto");
            }}
        if (view.getId() == R.id.cb_residencia){
            if (checked){
                editedPost.getPostMoradia().setTipoResidencia(true);
                Log.e("onCheckboxClicked","CheckBoxON cb_residencia");
            }
            else {
                editedPost.getPostMoradia().setTipoResidencia(false);
                Log.e("onCheckboxClicked","CheckBoxOFF cb_residencia");
            }}

    }

    private void EditarPostViaApi() {
        setarDados();
        SecurityPreferences securityPreferences = new SecurityPreferences(getApplicationContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));


        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        Long idMoradia = editedPost.getPostMoradia().getId();

        //DAR UPDATE NO POST SELECIONADO
        Call<Void> call = retrofitConfig.getPostService().updatePostMoradia(editedPost, id, idMoradia);
        Log.e("VALOR ID POSTMORADA", "deu ruim" + editedPost.getPostMoradia().getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Crie uma instância do seu Fragment

                    for (int i = 0; i < multiPartImgList.size(); i++) {
                        salvarImagemViaApi(retrofitConfig, idMoradia, multiPartImgList.get(i));
                    }


                    Intent intent = new Intent(MoradiaUsuarioEditar.this, MainActivity.class);
                    intent.putExtra("editar_postMoradia_tag", "editPostTag");
                    startActivity(intent);

                    Log.e("EDITAR POSTMORADIA", "deu bom");
                } else {
                    Log.e("EDITAR POSTMORADIA", "deu ruim" + editedPost.getDataPost());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EDITAR POSTMORADIA", "deu erro:" + t);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // imageUriList = null;
        // imagemLista = null;
        imagemAdapter = null;
        securityPreferences = null;
        picasso = null;

        retrofitConfigCepApi = null;

        // retrofitConfig = null;

    }

    private void iniciarViews() {
        cepEt = findViewById(R.id.et_cep_usuario);
        ruaEt = findViewById(R.id.et_rua_usuario);
        numCasaEt = findViewById(R.id.et_num_casa_usuario);
        comentarioEt = findViewById(R.id.et_comentario_anuncio);
        numMoradoresEt = findViewById(R.id.et_num_moradores_usuario);
        aluguelEt = findViewById(R.id.et_aluguel_usuario);
        petsCb = findViewById(R.id.cb_pets);
        garagemCb = findViewById(R.id.cb_garagem);
        quartoCb = findViewById(R.id.cb_quarto);
        residenciaCb = findViewById(R.id.cb_residencia);
        chipGeneroRep = findViewById(R.id.chips_genero);
        botaoEditar = findViewById(R.id.bt_publicar_edicao);
        btAddImg = findViewById(R.id.ib_adicionar_img);
        btRemoveImg = findViewById(R.id.ib_remover_img);
        recyclerview = findViewById(R.id.crv_fotos_moradia);
        cidadeTv = findViewById(R.id.tv_cidade_usuario);
        estadoTv = findViewById(R.id.tv_estado_usuario);
        // ivTeste = findViewById(R.id.iv_teste);
    }

    private void setarDados() {
        editedPost.getPostMoradia().setEndereco(ruaEt.getText().toString());
        editedPost.getPostMoradia().setNumCasa(Integer.valueOf(numCasaEt.getText().toString()));
        editedPost.setComentario(comentarioEt.getText().toString());
        editedPost.getPostMoradia().getDetalhesMoradia().setMoradores(Integer.valueOf(numMoradoresEt.getText().toString()));
        editedPost.getPostMoradia().setValorAluguel(Double.valueOf(aluguelEt.getText().toString()));

        int escolhaGenero = verificarChip3Opcoes(chipGeneroRep, R.id.chips_genero_masc, R.id.chips_genero_fem, R.id.chips_genero_misto);
        adicionarGenero(escolhaGenero);
    }

    //---------------------LIDAR COM IMAGEM-----------------------------------





    /*private void carregarImagemPost(Post post, Picasso picasso) {


        if (!carregarImgs) {
            carregarImgs = true;


            //  picasso.load("http://192.168.1.107:8080/imagens/Imagem1.jpg").into(target);
            //Log.e("IMG PICAASO", ": " + imgPicasso);

            //int numImagens = post.getPostMoradia().getFotos().size();

          //  List<Uri> tempUriList = new ArrayList<>();

            for (int i = 0; i < post.getPostMoradia().getFotos().size(); i++) {
               // String nomeImg = post.getPostMoradia().getFotos().get(i).getNomeFoto();
                picasso.load("http://192.168.1.107:8080/imagens/" + post.getPostMoradia().getFotos().get(i).getNomeFoto()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        File imageFile = createImageFileFromBitmap(bitmap);

                        if (imageFile != null) {
                            //imgPicasso = Uri.fromFile(imageFile);
                            //imageUriList.add(Uri.fromFile(imageFile));
                            imageUriList.add(Uri.fromFile(imageFile));
                            imagemAdapter.notifyItemInserted(imageUriList.size()-1);


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
            recyclerview.scrollToPosition(imageUriList.size()-1);
            //imagemAdapter.setImagem(imageUriList);
            //imagemAdapter.notifyDataSetChanged();
            //recyclerview.setAdapter(imagemAdapter);

          //  imagemAdapter = new ImagemAdapter(this);
           // imagemAdapter.setImagem(imageUriList);
          //  recyclerview.setAdapter(imagemAdapter);
          //  recyclerview.setInfinite(true);
          //  recyclerview.setFlat(true);
            //imagemAdapter.setImagem(imageUriList);
            //recyclerview.setAdapter(imagemAdapter);
            //recyclerview.
        }
    }*/

    private void pegarImgViaApi(RetrofitConfig retrofitConfig, Context context) {

        for (int i = 0; i < editedPost.getPostMoradia().getFotos().size(); i++) {


            Call<ResponseBody> call = retrofitConfig.getImageService().getImage(editedPost.getPostMoradia().getFotos().get(i).getNomeFoto());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // File x = response.body();
                    try {
                        File imageFile = File.createTempFile("image_", ".png", context.getCacheDir());

                        RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
                        MultipartBody.Part imagemPart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
                        multiPartImgList.add(imagemPart);


                        FileOutputStream fos = new FileOutputStream(imageFile);
                        fos.write(response.body().bytes());
                        fos.close();


                        Uri tempImage = Uri.fromFile(imageFile);
                        imagemAdapter.addImagem(tempImage);
                        // ivTeste.setImageURI(text);


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //  x.getAbsolutePath();


                    //  text = Uri.parse(response.body().getAbsolutePath());
                    Log.e("pegarImgViaApi", "onResponse: " + response.body());
                    Log.e("pegarImgViaApi", "onResponse text" + text);
                    // ivTeste.setImageURI(text);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("pegarImgViaApi", "onFailure: " + t.getMessage());

                }
            });

        }

    }

    private void salvarImagemViaApi(RetrofitConfig retrofitConfig, Long id, MultipartBody.Part imagem) {

        Call<Fotos> call = retrofitConfig.getImageService().uploadImage(imagem, id);
        call.enqueue(new Callback<Fotos>() {
            @Override
            public void onResponse(Call<Fotos> call, Response<Fotos> response) {
                // Lidar com a resposta do servidor, se houver
                if (response.isSuccessful()) {
                    Fotos resposta = response.body();
                    // Faça algo com a resposta do servidor, se necessário
                    Log.e("json bom", ": " + resposta);
                } else {
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

    private void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);

    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            //imageUri = result.getData().getData();
                            //result.getData().getData();
                            //tirar a foto padrao
                            //  if (imageUriList.get(0) == imgNotFound) {
                            //      imageUriList.remove(0);
                            //  }
                            //Log.e("IMG", ": " + imageUri.toString());
                            // binding.ivFotosUsuario.setImageURI(imageUri);
                            // imagemAdapter.setImagem(imageUriList);

                            File imageFile = new File(getRealPathFromUri(MoradiaUsuarioEditar.this, result.getData().getData()));

                            Log.e("onActivityResult", "getRealPathFromUri: " + imageFile.getName());
                            Log.e("onActivityResult", "getRealPathFromUri: " + imageFile.getAbsolutePath());


                            // RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                            RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
                            MultipartBody.Part imagemPart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
                            multiPartImgList.add(imagemPart);
                            imagemAdapter.addImagem(result.getData().getData());
                            // Enviar a imagem usando Retrofit

                        } catch (Exception e) {
                            Log.e("registerForActivityResult", "sem" + e.toString());
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


    private void adicionarGenero( int escolha) {
        if (escolha == 1) {
            editedPost.getPostMoradia().getDetalhesMoradia().setGeneroMoradia("MASCULINA");
        } else if (escolha == 2) {
            editedPost.getPostMoradia().getDetalhesMoradia().setGeneroMoradia("FEMININA");
        } else if (escolha == 3) {
            editedPost.getPostMoradia().getDetalhesMoradia().setGeneroMoradia("MISTA");
        }
    }

    private int verificarChip3Opcoes(ChipGroup chip, int opcao1, int opcao2, int opcao3) {
        int escolha = 0;
        ChipGroup chipGroup = chip;
        int selectedChipId = chipGroup.getCheckedChipId(); // Obtém o ID do Chip selecionado
        if (selectedChipId != View.NO_ID) {
            Chip selectedChip = findViewById(selectedChipId); // Obtém a referência ao Chip selecionado
            // Verifica qual Chip foi selecionado com base no ID
            if (selectedChip.getId() == opcao1) {
                escolha = 1;
            } else if (selectedChip.getId() == opcao2) {
                escolha = 2;
            } else if (selectedChip.getId() == opcao3) {
                escolha = 3;
            }
        } else {
            escolha = 0;
        }
        return escolha;
    }


    private void selecionarChips() {
        //tipo da residencia

        //genero
        if (editedPost.getPostMoradia().getDetalhesMoradia().getGeneroMoradia().equals("MASCULINA")) {
            Chip chipSelecionado = (Chip) chipGeneroRep.getChildAt(0);
            chipSelecionado.setChecked(true);
        } else if (editedPost.getPostMoradia().getDetalhesMoradia().getGeneroMoradia().equals("FEMININA")) {
            Chip chipSelecionado = (Chip) chipGeneroRep.getChildAt(1);
            chipSelecionado.setChecked(true);
        } else {
            Chip chipSelecionado = (Chip) chipGeneroRep.getChildAt(2);
            chipSelecionado.setChecked(true);
        }


    }

}