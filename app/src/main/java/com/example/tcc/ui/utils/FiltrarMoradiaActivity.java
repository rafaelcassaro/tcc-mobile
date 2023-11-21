package com.example.tcc.ui.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
    private CheckBox petsCb, garagemCb, quartoCb, residenciaCb;
    private ChipGroup chipGeneroRep;
    private DetalhesBusca detalhesBusca;
    private Button filtrar;
    private int escolhaGenero, vaisefude;
    private List<Post> db = new ArrayList<>();
    private List<Post> db2 = new ArrayList<>();
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;
    private boolean verificar;
    private int vaisefude2 = 0;
    private int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filtrar_moradia);
        detalhesBusca = new DetalhesBusca();
        securityPreferences = new SecurityPreferences(FiltrarMoradiaActivity.this);
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        iniciarViews();
        Intent intent = new Intent(this, MainActivity.class);

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
        petsCb = findViewById(R.id.cb_pets);
        garagemCb = findViewById(R.id.cb_garagem);
        quartoCb = findViewById(R.id.cb_quarto);
        residenciaCb = findViewById(R.id.cb_residencia);
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
        if (view.getId() == R.id.cb_pets) {
            if (checked) {
                detalhesBusca.setPets(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_pets");
            } else {
                detalhesBusca.setPets(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_pets");
            }
        }
        if (view.getId() == R.id.cb_garagem) {
            if (checked) {
                detalhesBusca.setGaragem(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_garagem");
            } else {
                detalhesBusca.setGaragem(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_garagem");
            }
        }
        if (view.getId() == R.id.cb_quarto) {
            if (checked) {
                detalhesBusca.setQuarto(true);
                Log.e("onCheckboxClicked", "CheckBoxON cb_quarto");
            } else {
                detalhesBusca.setQuarto(false);
                Log.e("onCheckboxClicked", "CheckBoxOFF cb_quarto");
            }
        }
        if (view.getId() == R.id.cb_residencia) {
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