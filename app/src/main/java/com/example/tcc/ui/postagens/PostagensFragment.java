package com.example.tcc.ui.postagens;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.tcc.R;
import com.example.tcc.databinding.FragmentPostagensBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.adapter.PostAdapter;
import com.example.tcc.ui.constants.TaskConstants;
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
    private MenuItem searchItem;
    private TextView searchEditText;
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPostagensBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cidadeProcurada = "";
        db.clear();
        iniciarRetrofit();
        createMenu();


        threadGetPesquisaCidade(container);

        // --------- recycler view  --------
        configAdapert(container);
        //getDbBack(container);


        return root;
    }

    private void threadGetPesquisaCidade(ViewGroup container) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cidade = (String) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH_POST);
                if (cidadeProcurada == "" && cidade == null) {
                    getDbBack(container);
                } else if (cidade != null) {
                    getPostCidadesBack(container, cidade);
                }

            }
        }).start();
    }

    private void iniciarRetrofit() {
        securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
    }

    private void createMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_search_posts, menu);
                createSearchBarOnMenu(menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void createSearchBarOnMenu(@NonNull Menu menu) {
        searchItem = menu.findItem(R.id.menu_search);
        searchEditText = (TextView) searchItem.getActionView();
        searchEditText.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.ic_lupa), null, null, null);
        searchEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar));
        searchEditText.setWidth(500);
        searchEditText.setHeight(100);
        searchEditText.setPadding(0, 22, 0, 0);
        searchEditText.setTextSize(17);
        searchEditText.setHint("Digite uma cidade");
        searchEditText.setHintTextColor(getResources().getColor(R.color.cinzaClaro, getContext().getTheme()));

        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchPostagemActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void configAdapert(ViewGroup container) {
        postAdapter = new PostAdapter(container.getContext());
        binding.rvPostagens.setAdapter(postAdapter);
    }


    private void getPostCidadesBack(ViewGroup container, String cidade) {
        Call<List<Post>> call = retrofitConfig.getService(PostService.class).getPostByCidade(cidade);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {

                    if (response.isSuccessful()) {
                        List<Post> tempDb = new ArrayList<>();
                        tempDb.clear();
                        tempDb = response.body();
                        for (int i = 0; tempDb.size() > i; i++) {
                            if (tempDb.get(i).getPostMoradia() == null) {
                                db.add(tempDb.get(i));
                            }
                        }
                        postAdapter.setPostagens(db);
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


    private void getDbBack(ViewGroup container) {
        Call<List<Post>> call = retrofitConfig.getService(PostService.class).getAllPost();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    List<Post> db = new ArrayList<>();
                    List<Post> tempDb;
                    tempDb = response.body();

                    for (Post post : tempDb) {
                        if (post.getPostMoradia() == null) {
                            db.add(post);
                        }
                    }
                    postAdapter.setPostagens(db);

                } else {
                    mostrarErro(container);
                }


            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                mostrarErro(container);
            }
        });

    }

    private void mostrarErro(ViewGroup container) {
        Toast.makeText(container.getContext(), "deu ruim !", Toast.LENGTH_SHORT).show();
    }


}