package com.example.tcc.ui.postagens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tcc.R;

import com.example.tcc.databinding.FragmentPostagensUsuarioBinding;
import com.example.tcc.db.PostagemDb;
import com.example.tcc.network.RetrofitConfigToken;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;

import com.example.tcc.ui.adapter.PostAdapter;
import com.example.tcc.ui.adapter.PostUsuarioAdapter;
import com.example.tcc.ui.adapter.PostagemAdapter;
import com.example.tcc.ui.constants.TaskConstants;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostagensUsuarioFragment extends Fragment {

    private FragmentPostagensUsuarioBinding binding;
    private RecyclerView.Adapter adapter;
    private PostUsuarioAdapter postAdapter;
    private List<Post> db = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPostagensUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // --------- recycler view  --------
        configAdapter(container);
        getPostsByUserId(container);

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void configAdapter(ViewGroup container){


        postAdapter = new PostUsuarioAdapter(container.getContext(), new PostUsuarioAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), PostagensUsuarioEditar.class);
                intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW, db.get(position));
                mStartForResult.launch(intent);
            }
        });


        //postAdapter = new PostAdapter(container.getContext());
        binding.rvPostagensUsuario.setAdapter(postAdapter);
    }


    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        }
    });



    private void getPostsByUserId(ViewGroup container){
        SecurityPreferences securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        RetrofitConfigToken retrofitConfigToken = new RetrofitConfigToken();
        retrofitConfigToken.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        String idUser = securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY);

        Call<List<Post>> call = retrofitConfigToken.getPostService().getPostByUserId(Long.parseLong(idUser));

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()){

                    List<Post> tempDb ;
                    tempDb = response.body();

                    for (Post post:tempDb) {
                        if(post.getPostMoradia() == null){
                            db.add(post);
                        }
                    }
                   // Log.e("Response body", "dados db local:" + db.toString());
                    postAdapter.setPostagens(db);
                   // Log.e("Response body", "dados ResponseBody:" + response.body());

                }
                else{
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

    private void mostrarErro(ViewGroup container){
        Toast.makeText(container.getContext(), "deu ruim !",Toast.LENGTH_SHORT).show();
    }
}