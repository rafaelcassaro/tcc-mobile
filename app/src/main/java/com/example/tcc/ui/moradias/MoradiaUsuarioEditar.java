package com.example.tcc.ui.moradias;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.ImagemAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.postagens.PostagensUsuarioEditar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
    private CarouselRecyclerview recyclerview;
    private List<MultipartBody.Part> imagemLista = new ArrayList<>();
    private List<Uri> imageUriList = new ArrayList<>();
    private ImagemAdapter imagemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moradia_usuario_editar_layout);

        //Pegar dados do post clicado com a constante EXTRA_SHOW
        Post post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);
        final RetrofitConfigCepApi retrofitConfigCepApi = new RetrofitConfigCepApi();

        imagemAdapter = new ImagemAdapter(this);
        imagemAdapter.setImagem(imageUriList);
        recyclerview.setAdapter(imagemAdapter);
        recyclerview.setInfinite(true);
        recyclerview.setFlat(true);


        iniciarViews();
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
        imagemAdapter.setImagem(imageUriList);

        btAddImg.setOnClickListener(view -> pickImage());


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
                            salvarViaApi(post);


                        }
                        else {

                        }

                    }

                    @Override
                    public void onFailure(Call<CepApi> call, Throwable t) {

                    }
                });

                //Call<Void> call= new RetrofitConfig(new SecurityPreferences(getApplicationContext()).getAuthToken(TaskConstants.SHARED.TOKEN_KEY))
                //        .getPostService().updatePost(editPost,editPost.getUsuario().getId(), editPost.getPostMoradia().getId());


            }
        });

    }

    private void salvarViaApi(Post post) {
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