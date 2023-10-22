package com.example.tcc.ui.moradias;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.databinding.FragmentNovoAnuncioBinding;
import com.example.tcc.databinding.FragmentPostagensUsuarioBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigCepApi;
import com.example.tcc.network.entities.CepApi;
import com.example.tcc.network.entities.Detalhes;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.entities.PostMoradia;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NovoAnuncioFragment extends Fragment {

    private FragmentNovoAnuncioBinding binding;




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


        binding.btPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String endereco = binding.etRuaUsuario.getText().toString();
                String teste = "fasdfasdf";
                post.setCidade(binding.etRuaUsuario.getText().toString());
               // post.getPostMoradia().setEndereco(endereco);
                Log.e("VERIFICAR ", "deu bom "+ binding.etRuaUsuario.getText().toString());
                Log.e("VERIFICAR POST ", "deu bom "+ post.toString());
                Log.e("VERIFICAR POST ", "deu bom "+ post.toString());
                Log.e("VERIFICAR string recebida ", "deu bom "+ endereco);*/



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
                            salvarViaApi(post);

                        }
                        else {

                        }

                    }

                    @Override
                    public void onFailure(Call<CepApi> call, Throwable t) {

                    }
                });

                //ChipGroup chipGroup = root.findViewById(R.id.chip_ap);

               // post.getPostMoradia().getDetalhesMoradia().setGeneroMoradia("MASCULINA");
               // Long idMoradia = post.getPostMoradia().getId();




            }
        });




        // Inflate the layout for this fragment
        return root;
    }

    private void salvarViaApi(Post post) {
        SecurityPreferences securityPreferences = new SecurityPreferences(getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));

        Call<Void> callSave = retrofitConfig.getPostService().createPost(post, id);
        callSave.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.e("msg", "deu bom");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("novo_postMoradia_tag", "editPostTag");
                    startActivity(intent);
                }
                else{
                    Log.e("msg", "deu bom ruim");
                }



            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("msg", "deu ruim");
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }





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