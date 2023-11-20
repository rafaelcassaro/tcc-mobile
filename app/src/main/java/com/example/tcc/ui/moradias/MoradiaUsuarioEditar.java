package com.example.tcc.ui.moradias;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Fotos;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.ImageService;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.adapter.ImagemEditarMoradiaAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiaUsuarioEditar extends AppCompatActivity {

    private EditText cepEt, ruaEt, numCasaEt, comentarioEt, numMoradoresEt, aluguelEt;
    private ChipGroup chipGeneroRep;
    private CheckBox petsCb, garagemCb, quartoCb, residenciaCb;
    private Button botaoEditar;
    private ImageButton btAddImg, btRemoveImg;
    private TextView cidadeTv, estadoTv, generoEscolhaTv;
    private CarouselRecyclerview recyclerview;
    private ImageView backButton;

    private ActivityResultLauncher<Intent> resultLauncher;
    private List<MultipartBody.Part> multiPartImgList = new ArrayList<>();
    private List<MultipartBody.Part> multiPartImgList2 = new ArrayList<>();
    private ImagemEditarMoradiaAdapter imagemAdapter;
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;
    private RetrofitConfigCepApi retrofitConfigCepApi;
    private Post editedPost;
    private int escolhaGenero, fotosJaCadastradas, teste;
    private ArrayList<Boolean> listaBoolean = new ArrayList<>();
    private List<Uri> fotosAntigas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moradia_usuario_editar_layout);
        iniciarViews();
        fotosJaCadastradas = 0;
        //Pegar dados do post clicado com a constante EXTRA_SHOW
        editedPost = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);
        Log.e("onCreate(Bundle savedInstanceState", "editedPost: " + editedPost.getPostMoradia().getFotos().toString());
        iniciarRetrofit();
        configAdapterImage();
        multiPartImgList.clear();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoradiaUsuarioEditar.super.onBackPressed();
            }
        });
        teste = 0;
        if (editedPost.getPostMoradia().getFotos().size() > 0) {
            requestImages(editedPost.getPostMoradia().getFotos().get(0).getNomeFoto(), editedPost.getPostMoradia().getFotos().get(0).getNomeFoto(), editedPost.getPostMoradia().getFotos().size());
        }

        //------------------------------------------------setar dados pre-vindos do post no front-------------------------------------
        setDadosPostInViews();
        pegarCheckboxPrevio();
        selecionarChips();
        //------------------------------------------------setar dados pre-vindos do post no front-------------------------------------
        registerResult();

        cepEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Ação a ser executada quando o usuário pressionar "Submit" no teclado
                pegarCepViaApi();
                return false;
            }
        });

        btAddImg.setOnClickListener(view -> pickImage());

        btRemoveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("btRemoveImg", "recyclerview.getSelectedPosition: " + recyclerview.getSelectedPosition());
                Log.e("btRemoveImg", "listaBoolean: " + listaBoolean.size());
                Log.e("btRemoveImg", "multiPartImgList2.size(): " + multiPartImgList2.size());
                Log.e("btRemoveImg", "multiPartImgList.size(): " + multiPartImgList.size());

                for (int i = 0; i < fotosAntigas.size(); i++) {
                    if (fotosAntigas.get(i).equals(imagemAdapter.getDb().get(recyclerview.getSelectedPosition()))) {
                        listaBoolean.set(i, false);
                    }
                }


                imagemAdapter.removeImg(recyclerview.getSelectedPosition());
                Log.e("btRemoveImg", "multiPartImgList.size(): " + multiPartImgList.size());
                if (!multiPartImgList.isEmpty() && recyclerview.getSelectedPosition() >= 0 && recyclerview.getSelectedPosition() < multiPartImgList.size()) {
                    multiPartImgList.remove(recyclerview.getSelectedPosition());

                    fotosJaCadastradas--;
                }
                imagemAdapter.addImgvazia();
                recyclerview.smoothScrollToPosition(0);
            }
        });

        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolhaGenero = verificarChip3Opcoes(chipGeneroRep, R.id.chips_genero_masc, R.id.chips_genero_fem, R.id.chips_genero_misto);

                if (verificarDados()) {
                    verificarCepViaApi();
                }

            }
        });

    }

    private boolean verificarDados() {
        if (cepEt.getText().toString().length() == 0) {
            cepEt.requestFocus();
            cepEt.setError("Digite um CEP !");
            return false;
        } else if (ruaEt.getText().toString().length() == 0) {
            ruaEt.requestFocus();
            ruaEt.setError("Digite o nome da rua !");
            return false;
        } else if (numCasaEt.getText().toString().length() == 0) {
            numCasaEt.requestFocus();
            numCasaEt.setError("Digite um valor !");
            return false;
        } else if (comentarioEt.getText().toString().length() == 0) {
            comentarioEt.requestFocus();
            comentarioEt.setError("Digite um Comentario !");
            return false;
        } else if (numMoradoresEt.getText().toString().length() == 0) {
            numMoradoresEt.requestFocus();
            numMoradoresEt.setError("Digite um valor !");
            return false;
        } else if (aluguelEt.getText().toString().length() == 0) {
            aluguelEt.requestFocus();
            aluguelEt.setError("Digite um valor !");
            return false;
        } else if (escolhaGenero == 0) {
            Toast.makeText(MoradiaUsuarioEditar.this, "Escolha um gênero para a republica!", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imagemAdapter = null;
        securityPreferences = null;
        retrofitConfigCepApi = null;
        multiPartImgList.clear();
        multiPartImgList2.clear();
        imagemAdapter = null;
    }

    private void setDadosPostInViews() {
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
    }

    private void iniciarRetrofit() {
        retrofitConfigCepApi = new RetrofitConfigCepApi();
        securityPreferences = new SecurityPreferences(this);
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
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
        backButton = findViewById(R.id.iv_voltar);
        generoEscolhaTv = findViewById(R.id.tv_genero_escolha);
    }

    private void setarDados() {
        editedPost.getPostMoradia().setEndereco(ruaEt.getText().toString());
        editedPost.getPostMoradia().setNumCasa(Integer.valueOf(numCasaEt.getText().toString()));
        editedPost.setComentario(comentarioEt.getText().toString());
        editedPost.getPostMoradia().getDetalhesMoradia().setMoradores(Integer.valueOf(numMoradoresEt.getText().toString()));
        editedPost.getPostMoradia().setValorAluguel(Double.valueOf(aluguelEt.getText().toString()));

        escolhaGenero = verificarChip3Opcoes(chipGeneroRep, R.id.chips_genero_masc, R.id.chips_genero_fem, R.id.chips_genero_misto);
        adicionarGenero(escolhaGenero);
    }

    private void configAdapterImage() {
        imagemAdapter = new ImagemEditarMoradiaAdapter(this);
        recyclerview.setAdapter(imagemAdapter);
        imagemAdapter.addImgvazia();
        recyclerview.setInfinite(true);
        recyclerview.setFlat(true);
    }

    private void verificarCepViaApi() {

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
                    editarPostViaApi();
                    Log.e("BOTAO EDITAR CEP", "RETORNO BOM: " + response.errorBody());

                } else {
                    cepEt.requestFocus();
                    cepEt.setError("Digite um CEP valido!");
                    Log.e("BOTAO EDITAR CEP", "RETORNO ERROR: " + response.errorBody());

                }

            }

            @Override
            public void onFailure(Call<CepApi> call, Throwable t) {
                Log.e("BOTAO EDITAR CEP", "INTERNAL ERROR: " + t.getMessage().toString());

            }
        });
    }

    private void pegarCepViaApi() {

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
                    cepEt.requestFocus();
                    cepEt.setError("Digite um CEP valido!");
                    Log.e("BOTAO EDITAR CEP", "RETORNO ERROR: " + response.errorBody());

                }

            }

            @Override
            public void onFailure(Call<CepApi> call, Throwable t) {
                Log.e("BOTAO EDITAR CEP", "INTERNAL ERROR: " + t.getMessage().toString());

            }
        });
    }

    private void pegarCheckboxPrevio() {
        if (editedPost.getPostMoradia().getDetalhesMoradia().isGaragem()) {
            garagemCb.setChecked(true);
        }
        if (editedPost.getPostMoradia().getDetalhesMoradia().isPets()) {
            petsCb.setChecked(true);
        }
        if (editedPost.getPostMoradia().getDetalhesMoradia().isQuarto()) {
            quartoCb.setChecked(true);
        }
        if (editedPost.getPostMoradia().isTipoResidencia()) {
            residenciaCb.setChecked(true);
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        if (view.getId() == R.id.cb_pets) {
            if (checked) {
                editedPost.getPostMoradia().getDetalhesMoradia().setPets(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            } else {
                editedPost.getPostMoradia().getDetalhesMoradia().setPets(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_pets");
            }
        }
        if (view.getId() == R.id.cb_garagem) {
            if (checked) {
                editedPost.getPostMoradia().getDetalhesMoradia().setGaragem(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_garagem");
            } else {
                editedPost.getPostMoradia().getDetalhesMoradia().setGaragem(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_garagem");
            }
        }
        if (view.getId() == R.id.cb_quarto) {
            if (checked) {
                editedPost.getPostMoradia().getDetalhesMoradia().setQuarto(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_quarto");
            } else {
                editedPost.getPostMoradia().getDetalhesMoradia().setQuarto(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_quarto");
            }
        }
        if (view.getId() == R.id.cb_residencia) {
            if (checked) {
                editedPost.getPostMoradia().setTipoResidencia(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_residencia");
            } else {
                editedPost.getPostMoradia().setTipoResidencia(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_residencia");
            }
        }

    }

    private void editarPostViaApi() {
        setarDados();
        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
        Long idMoradia = editedPost.getPostMoradia().getId();
        Log.e("editarPostViaApi", "editedPost:" + editedPost.getPostMoradia().getFotos().toString());
        Log.e("editarPostViaApi", "fotosJaCadastradas:" + fotosJaCadastradas);
        Log.e("editarPostViaApi", "multiPartImgList:" + multiPartImgList.size());

        // listaBoolean = imagemAdapter.getListaBoolean();

        for (int i = 0; i < listaBoolean.size(); i++) {
            if (listaBoolean.get(i)) {

                editedPost.getPostMoradia().getFotos().get(i).setId(null);
                editedPost.getPostMoradia().getFotos().get(i).setNomeFoto(null);
                editedPost.getPostMoradia().getFotos().get(i).setCaminhoImagem(null);
            }
        }
        for (int i = 0; i < editedPost.getPostMoradia().getFotos().size(); i++) {
            if (editedPost.getPostMoradia().getFotos().get(i).getId() == null) {
                editedPost.getPostMoradia().getFotos().remove(i);
            }
        }
        for (int i = 0; i < editedPost.getPostMoradia().getFotos().size(); i++) {
            if (editedPost.getPostMoradia().getFotos().get(i).getId() == null) {
                editedPost.getPostMoradia().getFotos().remove(i);
            }
        }
        Log.e("editarPostViaApi", "editedPost.getPostMoradia().getFotos():" + editedPost.getPostMoradia().getFotos());

        //DAR UPDATE NO POST SELECIONADO
        Call<Void> call = retrofitConfig.getService(PostService.class).updatePostMoradia(editedPost, id, idMoradia);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    for (int i = fotosJaCadastradas; i < multiPartImgList.size(); i++) {
                        salvarImagemViaApi(idMoradia, multiPartImgList.get(i));
                    }

                    Intent intent = new Intent(MoradiaUsuarioEditar.this, MainActivity.class);
                    intent.putExtra("editar_postMoradia_tag", "editPostTag");
                    startActivity(intent);
                } else {
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EDITAR POSTMORADIA", "deu erro:" + t);
            }
        });
    }


    //---------------------LIDAR COM IMAGEM-----------------------------------

    private void requestImages(String i, String x, int tamanho) {
        Call<ResponseBody> call = retrofitConfig.getService(ImageService.class).getImage(i);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.e("pegarImgViaApi", "response.isSuccessful: " + x);
                        File imageFile = File.createTempFile("image_", ".png", getCacheDir());
                        RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
                        MultipartBody.Part imagemPart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
                        multiPartImgList.add(imagemPart);
                        multiPartImgList2.add(imagemPart);
                        listaBoolean.add(true);
                        fotosJaCadastradas++;
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        fos.write(response.body().bytes());
                        fos.close();
                        Uri tempImage = Uri.fromFile(imageFile);
                        imagemAdapter.nome(x);
                        imagemAdapter.addImagem(tempImage);
                        fotosAntigas.add(tempImage);
                        teste++;
                        if (teste < tamanho) {
                            requestImages(editedPost.getPostMoradia().getFotos().get(teste).getNomeFoto(), editedPost.getPostMoradia().getFotos().get(teste).getNomeFoto(), tamanho);
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("pegarImgViaApi", "onFailure: " + t.getMessage());
            }
        });


    }

    private void salvarImagemViaApi(Long id, MultipartBody.Part imagem) {
        Call<Fotos> call = retrofitConfig.getService(ImageService.class).uploadImage(imagem, id);
        call.enqueue(new Callback<Fotos>() {
            @Override
            public void onResponse(Call<Fotos> call, Response<Fotos> response) {
                if (response.isSuccessful()) {
                } else {
                }
            }

            @Override
            public void onFailure(Call<Fotos> call, Throwable t) {
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
                            File imageFile = new File(getRealPathFromUri(MoradiaUsuarioEditar.this, result.getData().getData()));
                            RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
                            MultipartBody.Part imagemPart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
                            multiPartImgList.add(imagemPart);
                            imagemAdapter.addImagem(result.getData().getData());

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

    //-------------------BOTOES------------------------------------------

    private void adicionarGenero(int escolha) {
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