package com.example.tcc.ui.moradias;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.example.tcc.databinding.FragmentMoradiaUsuarioBinding;
import com.example.tcc.db.MoradiasDb;
import com.example.tcc.network.RetrofitConfigToken;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.MoradiasAdapter;
import com.example.tcc.ui.adapter.MoradiasUsuarioAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.login.FormCadastro;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiasUsuarioFragment extends Fragment {

    private FragmentMoradiaUsuarioBinding binding;
    private RecyclerView.Adapter adapter;
    private MoradiasUsuarioAdapter moradiasAdapter;
    private List<Post> db = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //HomeViewModel homeViewModel =  new ViewModelProvider(this).get(HomeViewModel.class);
        binding = com.example.tcc.databinding.FragmentMoradiaUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        //-------------rv
        configAdapter(container);
        getMoradiasUsuario(container);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void configAdapter(ViewGroup container){
        moradiasAdapter = new MoradiasUsuarioAdapter(container.getContext(), new MoradiasUsuarioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), MoradiaUsuarioEditar.class);
                intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW, db.get(position));
                mStartForResult.launch(intent);

            }
        });
        binding.rvMoradiasUsuario.setAdapter(moradiasAdapter);
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        }
    });

    private void getMoradiasUsuario(ViewGroup container){
        //PEGAR TOKEN
        SecurityPreferences securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        RetrofitConfigToken retrofitConfigToken = new RetrofitConfigToken();
        retrofitConfigToken.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        //Long.parseLong(TaskConstants.SHARED.PERSON_KEY)
        String idUser = securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY);
        Call<List<Post>> call = retrofitConfigToken.getPostService().getPostByUserId(Long.parseLong(idUser));
        Log.e("VALOR DO ID USER", "dados db local:" + Long.parseLong(idUser));

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {

                    db = response.body();
                    moradiasAdapter.setPostagens(db);
                    Log.e("UserMoradiasResponseBody", "dados db local:" + db.toString());
                    Log.e("UserMoradiasResponseBody", "dados ResponseBody:" + response.body());
                    Log.e("UserMoradiasResponseBody", "dados ResponseBody:" + db.get(0).getPostMoradia());

                } else {
                    mostrarErro(container);
                }

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                mostrarErro(container);
                Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
            }
        });
    }

    private void mostrarErro(ViewGroup container) {
        Toast.makeText(container.getContext(), "deu ruim !", Toast.LENGTH_SHORT).show();
    }




}