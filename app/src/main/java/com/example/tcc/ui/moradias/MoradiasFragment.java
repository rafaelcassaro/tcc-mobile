package com.example.tcc.ui.moradias;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.databinding.FragmentMoradiasBinding;
import com.example.tcc.db.models.Moradias;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigToken;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.MoradiasAdapter;
import com.example.tcc.ui.constants.TaskConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiasFragment extends Fragment {

    private FragmentMoradiasBinding binding;
    private RecyclerView.Adapter adapter;
    private MoradiasAdapter moradiasAdapter;
    private List<Post> db = new ArrayList<>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentMoradiasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        //--------RV---------------
        configAdapter(container);
        getDbBack(container);

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                adapter.notifyDataSetChanged();
            }
        }
    });

    private void configAdapter(ViewGroup container) {

        moradiasAdapter = new MoradiasAdapter(container.getContext(), new MoradiasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), MoradiaExpandir.class);
                intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW, db.get(position));
                mStartForResult.launch(intent);

            }
        });
        binding.rvMoradias.setAdapter(moradiasAdapter);
        Log.e("rv", "dados db:" + db.toString());
    }

    private void getDbBack(ViewGroup container) {
        //PEGAR TOKEN
        SecurityPreferences securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Call<List<Post>> call = retrofitConfig.getPostService().getAllPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {

                    db = response.body();
                    Log.e("Response body", "dados db local:" + db.toString());
                    moradiasAdapter.setPostagens(db);
                    Log.e("Response body", "dados ResponseBody:" + response.body());
                    Log.e("Response body", "dados ResponseBody:" + db.get(0).getPostMoradia());

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