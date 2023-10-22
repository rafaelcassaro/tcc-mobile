package com.example.tcc.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tcc.MainActivity;
import com.example.tcc.databinding.FragmentPerfilBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.login.FormLogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private Usuario usuario = new Usuario();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);


        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pegarDadosViaApi();

        binding.btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecurityPreferences securityPreferences = new SecurityPreferences(getContext());
                RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
                retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
                Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));
                usuario = atualizarDadosUsuario();


                Call<Usuario> call = retrofitConfig.getUserService().atualizarDados(id,usuario );

                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if(response.isSuccessful()){
                            Intent intent = new Intent(getContext(), FormLogin.class);
                            startActivity(intent);
                            Log.e("ATT DADOS", "IS SUCCESSFUL");
                        }
                        else {
                            Log.e("ATT DADOS", "not SUCCESSFUL");
                        }

                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Log.e("ATT DADOS", "DEU RUIM: "+ t);

                    }
                });

            }
        });


       // final TextView textView = binding.textSlideshow;
        //slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setarDados(){
        pegarDadosViaApi();
        Log.e("DADOS", ": "+ usuario.toString());
        binding.etNome.setText(usuario.getNome());
        binding.etEmail.setText(usuario.getEmail());
        binding.etCelular.setText(usuario.getCelular());
        binding.etLink1.setText(usuario.getLink1());
        binding.etLink2.setText(usuario.getLink2());
        binding.etLink3.setText(usuario.getLink3());
    }

    private void pegarDadosViaApi(){

        SecurityPreferences securityPreferences = new SecurityPreferences(getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        Long id = Long.valueOf(securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY));

        Call<Usuario> call = retrofitConfig.getUserService().getUserById(id);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()){
                    usuario = response.body();
                    Log.e("BODY   ", ": " + response.body());
                    usuario.setSenha("");
                    Log.e("DADOS", ": "+ usuario.toString());
                    binding.etNome.setText(usuario.getNome());
                    binding.etEmail.setText(usuario.getEmail());
                    binding.etCelular.setText(usuario.getCelular());
                    binding.etLink1.setText(usuario.getLink1());
                    binding.etLink2.setText(usuario.getLink2());
                    binding.etLink3.setText(usuario.getLink3());

                }
                else {

                }


            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
            }
        });

    }

    private Usuario atualizarDadosUsuario(){
        Usuario tempUsuario = new Usuario();
        tempUsuario.setNome(binding.etNome.getText().toString());
        tempUsuario.setEmail(binding.etEmail.getText().toString());
        tempUsuario.setSenha(binding.etSenha.getText().toString());
        tempUsuario.setCelular(binding.etCelular.getText().toString());
        tempUsuario.setLink1(binding.etLink1.getText().toString());
        tempUsuario.setLink2(binding.etLink2.getText().toString());
        tempUsuario.setLink3(binding.etLink3.getText().toString());

        return tempUsuario;
    }




}