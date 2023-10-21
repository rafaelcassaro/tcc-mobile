package com.example.tcc.ui.moradias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiaUsuarioEditar extends AppCompatActivity {

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
    //private Post postagemEviar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moradia_usuario_editar_layout);

        //Pegar dados do post clicado com a constante EXTRA_SHOW
        Post post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);

        iniciarViews();
        //PEGAR O TOKEN SALVO E APLICAR NA CONEXAO COM O END-POINT "retrofitConfig"
        SecurityPreferences securityPreferences = new SecurityPreferences(getApplicationContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        //TRANSFERIR OS VALORES INT E DOUBLE DO POST PARA OS ET DA VIEW
        int numMoradores = Integer.valueOf(post.getPostMoradia().getDetalhesMoradia().getMoradores());
        int numCasa = Integer.valueOf(post.getPostMoradia().getNumCasa());
        double valorAluguel = Double.valueOf(post.getPostMoradia().getValorAluguel());

        comentarioEt.setText(post.getComentario());
        ruaEt.setText(post.getPostMoradia().getEndereco());
        numMoradoresEt.setText(String.valueOf(numMoradores));
        numCasaEt.setText(String.valueOf(numCasa));
        aluguelEt.setText(String.valueOf(valorAluguel));
        selecionarChips(post);



        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("true ou false", "valor: "+ verificarChip(chipAp,  R.id.chip_ap_apart, R.id.chip_ap_casa));
                setarDados(post);


                //Call<Void> call= new RetrofitConfig(new SecurityPreferences(getApplicationContext()).getAuthToken(TaskConstants.SHARED.TOKEN_KEY))
                //        .getPostService().updatePost(editPost,editPost.getUsuario().getId(), editPost.getPostMoradia().getId());
                Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
                Long idMoradia = post.getPostMoradia().getId();

                //DAR UPDATE NO POST SELECIONADO
                Call<Void> call=retrofitConfig.getPostService().updatePostMoradia(post, id, idMoradia);
                //Log.e("VALOR ID POSTMORADA", "deu ruim"+ post.getPostMoradia().getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            // Crie uma instância do seu Fragment
                            Intent intent = new Intent(MoradiaUsuarioEditar.this, MainActivity.class);
                            //intent.putExtra("fragment_tag", "SeuFragmentTag");
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
        });
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

    //verificarChip(chipAp,  R.id.chip_ap_apart, R.id.chip_ap_casa)


    private void iniciarViews(){
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