package com.example.tcc.ui.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.adapter.SearchAdapter;
import com.example.tcc.ui.constants.TaskConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPostagemActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SearchAdapter adapter;
    private SearchView searchView;
    private Set<String> cidades = new HashSet<>();
    private List<String> listaCidades = new ArrayList<>();
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        iniciarComponentes();
        Intent intent = new Intent(this, MainActivity.class);

        configAdapter(intent);
        setSearchView(intent);

        getDbBack();
        setBtVoltar();


    }

    private void iniciarComponentes() {
        searchView = findViewById(R.id.edit_procurar);
        rv = findViewById(R.id.rv_moradias_search);
        backButton = findViewById(R.id.iv_voltar);
    }

    private void setBtVoltar() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPostagemActivity.super.onBackPressed();
            }
        });
    }

    private void setSearchView(Intent intent) {
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                for (String escolha : listaCidades) {
                    if (escolha.toLowerCase().equals(query.toLowerCase())) {
                        intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH_POST, escolha);
                        intent.putExtra("Search_post_cidades", "searchPostTag");
                        break;
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
    }

    private void configAdapter(Intent intent) {
        adapter = new SearchAdapter(this, new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH_POST, adapter.getDb().get(position));
                intent.putExtra("Search_post_cidades", "searchPostTag");
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);
    }

    //atualizar lista enquanto digita
    private void search(String query) {
        List<String> resultadosDaPesquisa = new ArrayList<>();

        for (String item : listaCidades) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                resultadosDaPesquisa.add(item);
            }
        }

        adapter.setDb(resultadosDaPesquisa);
    }

    private void getDbBack() {
        SecurityPreferences securityPreferences = new SecurityPreferences(this);
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Call<List<Post>> call = retrofitConfig.getService(PostService.class).getAllPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    List<Post> tempDb = new ArrayList<>();
                    listaCidades = new ArrayList<>();
                    tempDb.clear();
                    tempDb = response.body();

                    for (Post post : tempDb) {
                        if (post.getPostMoradia() == null) {
                            cidades.add(post.getCidade());
                        }
                    }

                    Iterator<String> iterator = cidades.iterator();
                    while (iterator.hasNext()) {
                        listaCidades.add(iterator.next());
                    }
                    adapter.setDb(listaCidades);

                } else {

                }


            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

                Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
            }
        });

    }


}