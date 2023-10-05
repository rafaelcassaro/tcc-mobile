package com.example.tcc.ui.postagens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.MainActivity;
import com.example.tcc.databinding.FragmentPostagensBinding;
import com.example.tcc.db.MoradiasDb;
import com.example.tcc.db.PostDb;
import com.example.tcc.db.PostagemDb;
import com.example.tcc.db.models.Postagem;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.ui.adapter.MoradiasAdapter;
import com.example.tcc.ui.adapter.PostAdapter;
import com.example.tcc.ui.adapter.PostagemAdapter;
import com.example.tcc.ui.moradias.MoradiaExpandir;
import com.example.tcc.ui.moradias.TesteActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostagensFragment extends Fragment {


    private FragmentPostagensBinding binding;
    private RecyclerView.Adapter adapter;

    //private PostDb db = new PostDb();
    private List<Post> db = new ArrayList<>();
    public static final String EXTRA_SHOW = "EXTRA_SHOW";


    /*ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        }
    });*/

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentPostagensBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // --------- recycler view  --------
        //uploadDbMockado(container);
        getDbBack(container);


        Log.e("fim do codigo", "fim do codigo:" + db.size());
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void uploadDbMockado(ViewGroup container){
        //adapter2 = new PostagemAdapter(getContext(), new PostagemDb());
        //binding.rvPostagens.setAdapter(adapter2);

       /* adapter2.setOnItemClickListener(new PostagemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {


                // Inicie a atividade desejada ao clicar em um item
                Intent intent = new Intent(getContext(), TesteActivity.class);
                intent.putExtra("EXTRA_SHOW", PostagemDb.myDataset.get(position)); // Passe os dados necessários para a próxima atividade
                mStartForResult.launch(intent);
            }
        });*/

        binding.rvPostagens.setHasFixedSize(true);
        adapter = new PostagemAdapter(container.getContext(), new PostagemDb());
        binding.rvPostagens.setAdapter(adapter);

    }

    private void configAdapert(ViewGroup container){
        //binding.rvPostagens.set;
        adapter = new PostAdapter(container.getContext(),db);
        binding.rvPostagens.setAdapter(adapter);
        Log.e("rv", "dados db:" + db.toString());
    }

    private void getDbBack(ViewGroup container){

        Call<List<Post>> call = new RetrofitConfig().getPostService().getAllPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                db = response.body();


                configAdapert(container);

                //adapter = new PostAdapter(container.getContext(),db);
                //binding.rvPostagens.setAdapter(adapter);
                Log.e("Response body", "dados:" + response.body());
                Log.e("Response body", "dados db:" + db.toString());
                Log.e("Response body", "celular user:" + db.get(0).getUsuario().getCelular());

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());

            }
        });

    }


}