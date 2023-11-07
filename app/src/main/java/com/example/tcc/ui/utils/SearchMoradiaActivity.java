package com.example.tcc.ui.utils;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.tcc.MainActivity;
import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.adapter.MoradiasAdapter;
import com.example.tcc.ui.adapter.SearchAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.moradias.MoradiaExpandir;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMoradiaActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SearchAdapter adapter;
    private SearchView searchView;
    private Set<String> cidades = new HashSet<>();
    private List<String> listaCidades = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.edit_procurar);
        rv = findViewById(R.id.rv_moradias_search);
        Intent intent = new Intent(this, MainActivity.class);
       // adapter = new SearchAdapter(this);

        adapter = new SearchAdapter(this, new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH, listaCidades.get(position));
                Log.e("INTENT INICIADO", ":" + listaCidades.get(position).toString());
                startActivity(intent);

            }
        });

        rv.setAdapter(adapter);
        searchView.requestFocus();



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                for (String escolha : listaCidades) {
                    if (escolha.toLowerCase().equals(query.toLowerCase())) {
                        intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH, escolha);
                        startActivity(intent);
                        // Objeto encontrado
                        break; // Se você só precisa encontrar a primeira ocorrência, saia do loop
                    }
                }
                Log.e("onQueryTextSubmit", ": "+ query);




                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("onQueryTextChange", ": "+ newText);
                search(newText);
                return false;
            }
        });
        getDbBack();

        ImageView backButton = findViewById(R.id.iv_voltar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchMoradiaActivity.super.onBackPressed();
            }
        });


    }

    private void search(String query) {
        List<String> resultadosDaPesquisa = new ArrayList<>();

        // Substitua SeuItem, seuAdapter e suaRecyclerView pelos nomes reais da sua classe, adaptador e RecyclerView.
        for (String item : listaCidades) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                resultadosDaPesquisa.add(item);
            }
        }

        adapter.setDb(resultadosDaPesquisa);
    }


    private void ConfigAdapter(){

    }


    private void getDbBack() {
        //PEGAR TOKEN
        SecurityPreferences securityPreferences = new SecurityPreferences(this);
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Call<List<Post>> call = retrofitConfig.getPostService().getAllPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    List<Post> tempDb = new ArrayList<>();
                    listaCidades = new ArrayList<>();
                    tempDb.clear();
                    tempDb = response.body();

                    for (int i = 0; tempDb.size()> i; i++){
                        if(tempDb.get(i).getPostMoradia() != null){
                            cidades.add(tempDb.get(i).getCidade());
                        }
                        //String estado = tempDb.get(i).getCidade() +"-"+ tempDb.get(i).getEstado();
                    }

                    Iterator<String> iterator = cidades.iterator();
                    while (iterator.hasNext()){
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


    /*private void getDbBack(ViewGroup container, String cidade) {
        //PEGAR TOKEN
        SecurityPreferences securityPreferences = new SecurityPreferences(this);
        RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        Call<List<Post>> call = retrofitConfig.getPostService().getPostByCidade(cidade);

       call.enqueue(new Callback<List<Post>>() {
           @Override
           public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
               if (response.isSuccessful()){

                   List<Post> tempDb = new ArrayList<>();
                   tempDb.clear();

                   tempDb = response.body();

                   for (int i = 0; tempDb.size()> i; i++){

                           cidades.add(tempDb.get(i).getCidade());

//                            Log.e("Response body", "dados ResponseBody:" + db.get(i).getPostMoradia().getFotos().toString());

                   }
                   adapter.setDb(cidades);

               }
           }

           @Override
           public void onFailure(Call<List<Post>> call, Throwable t) {

           }
       });

    }*/


}