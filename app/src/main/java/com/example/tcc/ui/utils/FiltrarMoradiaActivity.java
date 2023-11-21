package com.example.tcc.ui.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.DetalhesBusca;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FiltrarMoradiaActivity extends AppCompatActivity {

    private EditText aluguelMinimoEt, aluguelMaximoEt, numMoradoresEt;
    private CheckBox petsSimCb, garagemSimCb, quartoSimCb, residenciaSimCb;
    private CheckBox petsNaoCb, garagemNaoCb, quartoNaoCb, residenciaNaoCb;
    private ChipGroup chipGeneroRep;
    private DetalhesBusca detalhesBusca;
    private Button filtrar;
    private ImageView backButton;
    private int escolhaGenero, vaisefude;
    private List<Post> db = new ArrayList<>();
    private List<Post> db2 = new ArrayList<>();
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;
    private boolean verificar;
    private int vaisefude2 = 0;
    private int contador = 0;
    private int contResidencia, contGaragem,contQuarto,contPets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filtrar_moradia);
        detalhesBusca = new DetalhesBusca();
        securityPreferences = new SecurityPreferences(FiltrarMoradiaActivity.this);
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        contResidencia = 0;
        contGaragem = 0;
        contQuarto = 0;
        contPets = 0;
        iniciarViews();
        Intent intent = new Intent(this, MainActivity.class);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltrarMoradiaActivity.super.onBackPressed();
            }
        });

        filtrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDbBackFiltrar(intent);
                Log.e("FiltrarMoradiaActivity", "verificar: " + vaisefude);

                //  intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_FILTER, (Serializable) db2);
                //  startActivity(intent);
            }
        });

    }

    private void iniciarViews() {
        filtrar = findViewById(R.id.bt_publicar_edicao);
        aluguelMaximoEt = findViewById(R.id.et_aluguel_maximo);
        aluguelMinimoEt = findViewById(R.id.et_aluguel_minimo);
        numMoradoresEt = findViewById(R.id.et_num_moradores_usuario);
        petsSimCb = findViewById(R.id.cb_pets_sim);
        garagemSimCb = findViewById(R.id.cb_garagem_sim);
        quartoSimCb = findViewById(R.id.cb_quarto_sim);
        residenciaSimCb = findViewById(R.id.cb_residencia_sim);
        petsNaoCb = findViewById(R.id.cb_pets_nao);
        garagemNaoCb = findViewById(R.id.cb_garagem_nao);
        quartoNaoCb = findViewById(R.id.cb_quarto_nao);
        residenciaNaoCb = findViewById(R.id.cb_residencia_nao);
        backButton = findViewById(R.id.iv_voltar);
        chipGeneroRep = findViewById(R.id.chips_genero);
    }


    private boolean getDbBackFiltrar(Intent intent) {
        setarDados();
        Call<List<Post>> call = retrofitConfig.getService(PostService.class).getAllPost();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    db2.removeAll(db2);
                    db.removeAll(db);

                    List<Post> tempDb = new ArrayList<>();
                    tempDb.clear();
                    tempDb = response.body();

                    for (int i = 0; tempDb.size() > i; i++) {
                        if (tempDb.get(i).getPostMoradia() != null) {
                            db.add(tempDb.get(i));
                        }
                    }

                    db2.addAll(db);
                    db.removeAll(db);
                    //===============================================================ALUGUEL MAX ====================================================
                    if (detalhesBusca.getValorAluguelMaximo() != null) {

                        for (int i = 0; db2.size() > i; i++) {
                            if (db2.get(i).getPostMoradia().getValorAluguel() <= detalhesBusca.getValorAluguelMaximo()) {
                                contador++;
                                db.add(db2.get(i));
                            }
                        }

                        if (contador == 0) {
                            vaisefude2 = 0;
                            aluguelMaximoEt.setError("Nenhum resultado encontrado!");
                            aluguelMaximoEt.requestFocus();
                            return;
                        } else if (contador > 0) {
                            vaisefude2 = 1;
                            db2.removeAll(db2);
                            db2.addAll(db);
                            db.removeAll(db);
                        }
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMaximo contador: " + contador);
                        contador = 0;
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMaximo db2: " + db2.toString());
                        Log.e("FiltrarMoradiaActivity", "rAluguelMaximo minimo: " + detalhesBusca.getValorAluguelMinimo());
                    }
                    //===============================================================GENERO MORADIA ====================================================
                    if (detalhesBusca.getGeneroMoradia() != null) {
                        for (int i = 0; db2.size() > i; i++) {
                            if (db2.get(i).getPostMoradia().getDetalhesMoradia().getGeneroMoradia().equals(detalhesBusca.getGeneroMoradia())) {
                                db.add(db2.get(i));
                                contador++;
                            }
                        }
                        if (contador == 0) {
                            vaisefude2 = 0;
                            Toast.makeText(FiltrarMoradiaActivity.this, "Nenhum resultado encontrado para o genero escolhido!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (contador > 0) {
                            vaisefude2 = 1;
                            db2.removeAll(db2);
                            db2.addAll(db);
                            db.removeAll(db);
                        }
                        contador = 0;

                    }
                    //===============================================================NUM MORADORES ====================================================
                    if (detalhesBusca.getMoradores() != null) {
                        for (int i = 0; db2.size() > i; i++) {
                            if (db2.get(i).getPostMoradia().getDetalhesMoradia().getMoradores() <= detalhesBusca.getMoradores()) {
                                db.add(db2.get(i));
                                contador++;
                            }
                        }
                        if (contador == 0) {
                            vaisefude2 = 0;
                            numMoradoresEt.setError("Nenhum resultado encontrado!");
                            numMoradoresEt.requestFocus();
                            return;
                        } else if (contador > 0) {
                            vaisefude2 = 1;
                            db2.removeAll(db2);
                            db2.addAll(db);
                            db.removeAll(db);
                        }
                        contador = 0;
                    }
                    //===============================================================ALUGUEL MINIMO ====================================================
                    if (detalhesBusca.getValorAluguelMinimo() != null) {
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo db2: " + db2.toString());
                        for (int i = 0; db2.size() > i; i++) {
                            if (db2.get(i).getPostMoradia().getValorAluguel() >= detalhesBusca.getValorAluguelMinimo()) {
                                contador++;
                                db.add(db2.get(i));
                            }
                        }

                        if (contador == 0) {
                            vaisefude2 = 0;
                            aluguelMinimoEt.setError("Nenhum resultado encontrado!");
                            aluguelMinimoEt.requestFocus();
                            return;
                        } else if (contador > 0) {
                            vaisefude2 = 1;
                            db2.removeAll(db2);
                            db2.addAll(db);
                            db.removeAll(db);
                        }
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo contador: " + contador);
                        contador = 0;

                    }
                    //===============================================================PETS ====================================================
                    if (contPets == 1) {
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo db2: " + db2.toString());
                        for (int i = 0; db2.size() > i; i++) {
                            if (db2.get(i).getPostMoradia().getDetalhesMoradia().isPets() == detalhesBusca.isPets()) {
                                contador++;
                                db.add(db2.get(i));
                            }
                        }

                        if (contador == 0) {
                            vaisefude2 = 0;
                            Toast.makeText(FiltrarMoradiaActivity.this, "Nenhum resultado encontrado para PETS!", Toast.LENGTH_SHORT).show();
                            aluguelMinimoEt.requestFocus();
                            return;
                        } else if (contador > 0) {
                            vaisefude2 = 1;
                            db2.removeAll(db2);
                            db2.addAll(db);
                            db.removeAll(db);
                        }
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo contador: " + contador);
                        contador = 0;

                    }
                    //===============================================================RESIDENCIA ====================================================
                    if (contResidencia == 1) {
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo db2: " + db2.toString());
                        for (int i = 0; db2.size() > i; i++) {
                            if (db2.get(i).getPostMoradia().isTipoResidencia() == detalhesBusca.isTipoResidencia()) {
                                contador++;
                                db.add(db2.get(i));
                            }
                        }

                        if (contador == 0) {
                            vaisefude2 = 0;
                            Toast.makeText(FiltrarMoradiaActivity.this, "Nenhum resultado encontrado para tipo da RESIDENCIA!", Toast.LENGTH_SHORT).show();
                            aluguelMinimoEt.requestFocus();
                            return;
                        } else if (contador > 0) {
                            vaisefude2 = 1;
                            db2.removeAll(db2);
                            db2.addAll(db);
                            db.removeAll(db);
                        }
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo contador: " + contador);
                        contador = 0;

                    }
                    //===============================================================GARAGEM ====================================================
                    if (contGaragem == 1) {
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo db2: " + db2.toString());
                        for (int i = 0; db2.size() > i; i++) {
                            if (db2.get(i).getPostMoradia().getDetalhesMoradia().isGaragem() == detalhesBusca.isGaragem()) {
                                contador++;
                                db.add(db2.get(i));
                            }
                        }

                        if (contador == 0) {
                            vaisefude2 = 0;
                            Toast.makeText(FiltrarMoradiaActivity.this, "Nenhum resultado encontrado para tipo da GARAGEM!", Toast.LENGTH_SHORT).show();
                            aluguelMinimoEt.requestFocus();
                            return;
                        } else if (contador > 0) {
                            vaisefude2 = 1;
                            db2.removeAll(db2);
                            db2.addAll(db);
                            db.removeAll(db);
                        }
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo contador: " + contador);
                        contador = 0;

                    }
                    //===============================================================QUARTO ====================================================
                    if (contQuarto == 1) {
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo db2: " + db2.toString());
                        for (int i = 0; db2.size() > i; i++) {
                            if (db2.get(i).getPostMoradia().getDetalhesMoradia().isQuarto() == detalhesBusca.isQuarto()) {
                                contador++;
                                db.add(db2.get(i));
                            }
                        }

                        if (contador == 0) {
                            vaisefude2 = 0;
                            Toast.makeText(FiltrarMoradiaActivity.this, "Nenhum resultado encontrado para tipo do QUARTO!", Toast.LENGTH_SHORT).show();
                            aluguelMinimoEt.requestFocus();
                            return;
                        } else if (contador > 0) {
                            vaisefude2 = 1;
                            db2.removeAll(db2);
                            db2.addAll(db);
                            db.removeAll(db);
                        }
                        Log.e("FiltrarMoradiaActivity", "detalhesBusca.getValorAluguelMinimo contador: " + contador);
                        contador = 0;

                    }



                    if (vaisefude2 == 1) {
                        Log.e("FiltrarMoradiaActivity", "onFailureverificar: " + vaisefude);
                        intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_FILTER, (Serializable) db2);
                        startActivity(intent);
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
            }

        });

        return true;
    }

    public void onCheckboxFilterClicked(View view){
        boolean checked = ((CheckBox) view).isChecked();

        // --------PETS--------------
        if (view.getId() == R.id.cb_pets_sim) {
            if (checked) {
                contPets =1 ;
                detalhesBusca.setPets(true);
                petsNaoCb.setChecked(false);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            }
        }
        if (view.getId() == R.id.cb_pets_nao) {
            if (checked) {
                contPets =1 ;
                detalhesBusca.setPets(false);
                petsSimCb.setChecked(false);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            }
        }
        if(!petsSimCb.isChecked() && !petsNaoCb.isChecked()){
            contPets = 0 ;
            DetalhesBusca x = new DetalhesBusca();
            detalhesBusca.setPets(x.isPets());
        }
        // --------GARAGEM--------------
        if (view.getId() == R.id.cb_garagem_sim) {
            if (checked) {
                contPets =1 ;
                detalhesBusca.setGaragem(true);
                garagemNaoCb.setChecked(false);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            }
        }
        if (view.getId() == R.id.cb_garagem_nao) {
            if (checked) {
                contGaragem =1 ;
                detalhesBusca.setGaragem(false);
                garagemSimCb.setChecked(false);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            }
        }
        if(!garagemSimCb.isChecked() && !garagemNaoCb.isChecked()){
            contGaragem = 0 ;
        }
        // -----------QUARTO--------------
        if (view.getId() == R.id.cb_quarto_sim) {
            if (checked) {
                contQuarto =1 ;
                detalhesBusca.setQuarto(true);
                quartoNaoCb.setChecked(false);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            }
        }
        if (view.getId() == R.id.cb_quarto_nao) {
            if (checked) {
                contQuarto =1 ;
                detalhesBusca.setQuarto(false);
                quartoSimCb.setChecked(false);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            }
        }
        if(!quartoSimCb.isChecked() && !quartoNaoCb.isChecked()){
            contQuarto = 0 ;
        }
        // --------RESIDENCIA--------------
        if (view.getId() == R.id.cb_residencia_sim) {
            if (checked) {
                contResidencia =1 ;
                detalhesBusca.setTipoResidencia(true);
                residenciaNaoCb.setChecked(false);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            }
        }
        if (view.getId() == R.id.cb_residencia_nao) {
            if (checked) {
                contResidencia =1 ;
                detalhesBusca.setTipoResidencia(false);
                residenciaSimCb.setChecked(false);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            }
        }
        if(!residenciaSimCb.isChecked() && !residenciaNaoCb.isChecked()){
            contResidencia = 0 ;
        }


    }

    private void setarDados() {

        if (!numMoradoresEt.getText().toString().isEmpty()) {
            detalhesBusca.setMoradores(Integer.valueOf(numMoradoresEt.getText().toString()));
        }
        if (!aluguelMaximoEt.getText().toString().isEmpty()) {
            detalhesBusca.setValorAluguelMaximo(Double.valueOf(aluguelMaximoEt.getText().toString()));
        }
        if (!aluguelMinimoEt.getText().toString().isEmpty()) {
            detalhesBusca.setValorAluguelMinimo(Double.valueOf(aluguelMinimoEt.getText().toString()));
        }

        escolhaGenero = verificarChip3Opcoes(chipGeneroRep, R.id.chips_genero_masc, R.id.chips_genero_fem, R.id.chips_genero_misto);
        adicionarGenero(escolhaGenero);
    }

    private void adicionarGenero(int escolha) {
        if (escolha == 1) {
            detalhesBusca.setGeneroMoradia("MASCULINA");
        } else if (escolha == 2) {
            detalhesBusca.setGeneroMoradia("FEMININA");
        } else if (escolha == 3) {
            detalhesBusca.setGeneroMoradia("MISTA");
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

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        if (view.getId() == R.id.cb_pets_sim) {
            if (checked) {
                detalhesBusca.setPets(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            } else {
                detalhesBusca.setPets(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_pets");
            }
        }
        if (view.getId() == R.id.cb_garagem_sim) {
            if (checked) {
                detalhesBusca.setGaragem(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_garagem");
            } else {
                detalhesBusca.setGaragem(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_garagem");
            }
        }
        if (view.getId() == R.id.cb_quarto_sim) {
            if (checked) {
                detalhesBusca.setQuarto(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_quarto");
            } else {
                detalhesBusca.setQuarto(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_quarto");
            }
        }
        if (view.getId() == R.id.cb_residencia_sim) {
            if (checked) {
                detalhesBusca.setTipoResidencia(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_residencia");
            } else {
                detalhesBusca.setTipoResidencia(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_residencia");
            }
        }

    }


}