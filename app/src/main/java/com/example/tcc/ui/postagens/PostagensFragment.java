package com.example.tcc.ui.postagens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.example.tcc.R;
import com.example.tcc.databinding.FragmentPostagensBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigToken;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.PostAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.utils.SearchMoradiaActivity;
import com.example.tcc.ui.utils.SearchPostagemActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostagensFragment extends Fragment {

    private FragmentPostagensBinding binding;
    private PostAdapter postAdapter;
    private String cidadeProcurada;
    private List<Post> db = new ArrayList<>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentPostagensBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_search_posts, menu);

                MenuItem searchItem = menu.findItem(R.id.menu_search);
                TextView searchEditText = (TextView) searchItem.getActionView();


                searchEditText.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.ic_lupa),null,null,null);
                searchEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar));
                searchEditText.setWidth(500);
                searchEditText.setHeight(100);
                searchEditText.setPadding(0,10,0,0);
                searchEditText.setTextSize(20);
                searchEditText.setHint("Digite uma cidade");
                searchEditText.setHintTextColor(getResources().getColor(R.color.cinzaClaro, getContext().getTheme() ));

                searchEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), SearchPostagemActivity.class);
                        startActivity(intent);
                    }
                });






            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Intent intent = new Intent(getContext(), SearchActivity.class);
                //  startActivity(intent);
                //  binding.editProcurar.showSearch();
                // Intent intent = new Intent(getContext(), SearchActivity.class);
                // startActivity(intent);
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        cidadeProcurada = "";
        db.clear();


        new Thread(new Runnable() {
            @Override
            public void run() {
                String cidade = (String) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH_POST);
                if (cidadeProcurada == "" && cidade == null){
                    getDbBack(container);
                }
                else if (cidade != null){
                    getPostCidadesBack(container, cidade);
                }

            }
        }).start();

        // --------- recycler view  --------
        configAdapert(container);
        //getDbBack(container);




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void configAdapert(ViewGroup container){

        postAdapter = new PostAdapter(container.getContext());
        binding.rvPostagens.setAdapter(postAdapter);
       // Log.e("rv", "dados db:" + db.toString());
    }


    private void getPostCidadesBack(ViewGroup container, String cidade) {
        //PEGAR TOKEN
        SecurityPreferences securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Call<List<Post>> call = retrofitConfig.getPostService().getPostByCidade(cidade);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()){

                    if (response.isSuccessful()) {
                        List<Post> tempDb = new ArrayList<>();
                        tempDb.clear();

                        tempDb = response.body();

                        Log.e("Response body", "dados db local:" + db.toString());
                        for (int i = 0; tempDb.size()> i; i++){
                            if(tempDb.get(i).getPostMoradia() == null){
                                db.add(tempDb.get(i));

//                            Log.e("Response body", "dados ResponseBody:" + db.get(i).getPostMoradia().getFotos().toString());
                            }
                        }
                        postAdapter.setPostagens(db);

                        //for(int i =0 ; i < db.size(); i++){
                        //    //Fotos fotos = new Fotos();
                        //     Log.e("Response body", "dados ResponseBody:" + db.get(i).getPostMoradia().getFotos().toString());
                        // }


                        Log.e("Response body", "dados ResponseBody:" + response.body());


                    } else {
                        mostrarErro(container);
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });

    }



    private void getDbBack(ViewGroup container){
        SecurityPreferences securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        RetrofitConfigToken retrofitConfigToken = new RetrofitConfigToken();
        retrofitConfigToken.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Call<List<Post>> call = retrofitConfigToken.getPostService().getAllPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()){
                    List<Post> db = new ArrayList<>();
                    List<Post> tempDb ;
                    tempDb = response.body();

                    for (Post post:tempDb) {
                        if(post.getPostMoradia() == null){
                            db.add(post);
                        }
                    }

                    Log.e("Response body", "dados db local:" + db.toString());
                    postAdapter.setPostagens(db);
                    Log.e("Response body", "dados ResponseBody:" + response.body());

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