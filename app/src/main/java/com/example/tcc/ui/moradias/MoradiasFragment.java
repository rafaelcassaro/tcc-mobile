package com.example.tcc.ui.moradias;

import android.app.Activity;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.tcc.R;
import com.example.tcc.databinding.FragmentMoradiasBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.DetalhesBusca;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.adapter.MoradiasAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.utils.FiltrarMoradiaActivity;
import com.example.tcc.ui.utils.SearchMoradiaActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiasFragment extends Fragment {

    private FragmentMoradiasBinding binding;
    private MoradiasAdapter moradiasAdapter;
    private List<Post> db;
    private List<Post> dbCidadesSegurar;
    private List<Post> db2;
    private List<Post> db3;
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;

    private String cidade , cidade2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMoradiasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = new ArrayList<>();
        db2 = new ArrayList<>();
        db3 = new ArrayList<>();

        dbCidadesSegurar = new ArrayList<>();
        cidade = null;
        cidade2 = null;
        Log.e("onCreateView", "dbCidadesSegurar inicio "+ dbCidadesSegurar);
        Log.e("onCreateView", "db2 inicio "+ db2);
        /*if(db2 != null){
            if(db2.size()==0 && dbCidadesSegurar.size() == 0){
                db2=null;
            }
        }*/
        Log.e("onCreateView", "db2 inicio pos null "+ db2);
        threadGetDataFromApi();
        setSearchBar();
        //db.clear();
        //db2.clear();
        //--------RV---------------

        return root;
    }


    private void threadGetDataFromApi() {

        Log.e("threadGetDataFromApi", "dbCidadesSegurar inicio "+ dbCidadesSegurar);
        Log.e("threadGetDataFromApi", "db2 inicio "+ db2);
        securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        configAdapter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                cidade = null;
                cidade = (String) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH);
                Log.e("setSearchBar", "(String) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH); "+ cidade);
                Log.e("setSearchBar", "EXTRA_SHOW_SEARCH); "+ (String) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH));
                db2 = (List<Post>) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_FILTER);
                db2 = db2 != null ? db2 : new ArrayList<>();
                Log.e("threadGetDataFromApi", "db2 getActivity "+ db2);
                cidade2 = securityPreferences.getAuthToken(TaskConstants.SHARED.EXTRA_SHOW_CIT);
               // dbCidadesSegurar.clear();

                Log.e("setSearchBar", "(String) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH); "+ cidade);
                if (cidade == null && db2.isEmpty() && cidade2==null) {
                    Log.e("threadGetDataFromApi", "cidade == null && db2==null && cidade2==null ");
                    //dbCidadesSegurar = null;
                    getDbBack();
                } else if(cidade2!=null && cidade ==null && !db2.isEmpty()){
                    Log.e("threadGetDataFromApi", "cidade2!=null && cidade ==null && db2 != null ");
                    getPostCidadesBack2(cidade2);
                }
                else if (cidade != null && cidade2 == null) {
                    Log.e("threadGetDataFromApi", "cidade != null && cidade2 == null ");
                    getPostCidadesBack(cidade);
                }
                else if (cidade ==null && cidade2 == null && !db2.isEmpty()) {
                    Log.e("threadGetDataFromApi", "db2 !=null && cidade ==null && cidade2 == null && dbCidadesSegurar ==null ");
                    db.clear();
                    db.addAll(db2);
                    moradiasAdapter.setPostagens(db);
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        securityPreferences.store(TaskConstants.SHARED.EXTRA_SHOW_SEARCH, null);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        db2 = null;
        cidade = null;
    }

    private void setSearchBar() {

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                db2 = db2 != null ? db2 : new ArrayList<>();
                dbCidadesSegurar = dbCidadesSegurar != null ? dbCidadesSegurar : new ArrayList<>();
                menuInflater.inflate(R.menu.menu_search_posts, menu);
                MenuItem searchItem = menu.findItem(R.id.menu_search);
                MenuItem filterItem = menu.findItem(R.id.menu_space_left);
                MenuItem closeCitSerchIcon = menu.findItem(R.id.menu_close_cit_filter);
                MenuItem closeIcon = menu.findItem(R.id.menu_close_icon);

                TextView searchEditText = (TextView) searchItem.getActionView();
                TextView filterEditText = (TextView) filterItem.getActionView();
                Log.e("setSearchBar", "dbCidadesSegurar inicio "+ dbCidadesSegurar);
                Log.e("setSearchBar", "db2 inicio "+ db2);

                /*if(db2 != null){
                    if(db2.size()==0 && dbCidadesSegurar.size() == 0){
                        db2=null;
                    }
                }*/
                barraPesquisaPadrao(searchEditText, R.drawable.button_filtrar, "Digite uma cidade", R.color.cinzaClaro);

                searchEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //securityPreferences.store(TaskConstants.SHARED.EXTRA_SHOW_CIT, null);
                        Intent intent = new Intent(getContext(), SearchMoradiaActivity.class);
                        intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_CIT, cidade);
                        startActivity(intent);
                    }
                });


                filterEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar));
                filterEditText.setWidth(150);
                filterEditText.setHeight(100);
                filterEditText.setPadding(30,25,10,10);
                filterEditText.setTextSize(14);
                filterEditText.setHint("Filtrar");
                filterEditText.setHintTextColor(getResources().getColor(R.color.black, getContext().getTheme()));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        filterEditText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(cidade != null){
                                    Intent intent = new Intent(getContext(), FiltrarMoradiaActivity.class);
                                    intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_CIT, cidade);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(getContext(), FiltrarMoradiaActivity.class);
                                    intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW_CIT, cidade2);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }).start();

                if(db2.isEmpty()){
                    closeIcon.setIcon(R.drawable.ic_invisivel);
                }else if (!db2.isEmpty() && dbCidadesSegurar.isEmpty() ){

                        Log.e("setSearchBar", "db2 != null && dbCidadesSegurar.size() =" +
                                "= 0 | dbCidadesSegurar "+ dbCidadesSegurar);
                        Log.e("setSearchBar", "db2 != null && dbCidadesSegurar.size() == 0  |  db2.size()"+ db2.size());
                        closeIcon.setIcon(R.drawable.ic_close);
                        filterEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar_selecionado));

                        closeIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                Log.e("setSearchBar", "closeIcon.setOnMenuItemClickListener db2 != null && dbCidadesSegurar.size() == 0 "+ db2);
                                db2.clear();
                                getDbBack();

                                filterEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar));
                                closeIcon.setIcon(R.drawable.ic_invisivel);
                                Log.e("setSearchBar", "closeIcon.setOnMenuItemClickListener db2 != null && dbCidadesSegurar.size() == 0 "+ db2);
                                return false;
                            }
                        });

                }else if (!db2.isEmpty() && !dbCidadesSegurar.isEmpty()){

                        Log.e("setSearchBar", "db2 != null && dbCidadesSegurar.size() > 0 |dbCidadesSegurar "+ dbCidadesSegurar);
                        closeIcon.setIcon(R.drawable.ic_close);
                        filterEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar_selecionado));

                        // FECHAR BAR CIDADE E FILTRO
                        closeIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                Log.e("setSearchBar", "closeIcon.setOnMenuItemClickListener db2 != null && dbCidadesSegurar.size() > 0 "+ db2);
                                db.clear();
                                db.addAll(dbCidadesSegurar);
                                db2.clear();

                                moradiasAdapter.setPostagens(db);
                                filterEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar));
                                closeIcon.setIcon(R.drawable.ic_invisivel);
                                Log.e("setSearchBar", "closeIcon.setOnMenuItemClickListener db2 != null && dbCidadesSegurar.size() > 0 "+ db2);
                                return false;
                            }
                        });
                }


                if(cidade == null && cidade2 ==null){
                    closeCitSerchIcon.setIcon(R.drawable.ic_invisivel);
                }else if(cidade != null){
                    closeCitSerchIcon.setIcon(R.drawable.ic_close);
                    barraPesquisaPadrao(searchEditText, R.drawable.button_filtrar_selecionado, cidade, R.color.black);

                    closeCitSerchIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {

                            cidade = null;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getDbBack();
                                    securityPreferences.store(TaskConstants.SHARED.EXTRA_SHOW_CIT, null);
                                    securityPreferences.store(TaskConstants.SHARED.EXTRA_SHOW_SEARCH, null);
                                    cidade = null;
                                }
                            }).start();
                            closeCitSerchIcon.setIcon(R.drawable.ic_invisivel);
                            barraPesquisaPadrao(searchEditText, R.drawable.button_filtrar, "Digite uma cidade", R.color.cinzaClaro);
                            filterEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar));
                            closeIcon.setIcon(R.drawable.ic_invisivel);
                            cidade = null;
                            return false;
                        }
                    });
                }
                else if(cidade2 != null && cidade == null){
                    closeCitSerchIcon.setIcon(R.drawable.ic_close);
                    barraPesquisaPadrao(searchEditText, R.drawable.button_filtrar_selecionado, cidade2, R.color.black);
                    closeCitSerchIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {
                            cidade = null;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getDbBack();
                                    securityPreferences.store(TaskConstants.SHARED.EXTRA_SHOW_CIT, null);
                                    securityPreferences.store(TaskConstants.SHARED.EXTRA_SHOW_SEARCH, null);
                                    cidade = null;
                                }
                            }).start();

                            closeCitSerchIcon.setIcon(R.drawable.ic_invisivel);
                            barraPesquisaPadrao(searchEditText, R.drawable.button_filtrar, "Digite uma cidade", R.color.cinzaClaro);
                            filterEditText.setBackground(getContext().getDrawable(R.drawable.button_filtrar));
                            closeIcon.setIcon(R.drawable.ic_invisivel);
                            cidade = null;
                            return false;
                        }
                    });
                }




            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void barraPesquisaPadrao(TextView searchEditText, int button_filtrar, String Digite_uma_cidade, int cinzaClaro) {
        searchEditText.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.ic_lupa), null, null, null);
        searchEditText.setBackground(getContext().getDrawable(button_filtrar));
        searchEditText.setWidth(500);
        searchEditText.setHeight(100);
        searchEditText.setPadding(0, 22, 0, 0);
        searchEditText.setTextSize(17);
        searchEditText.setHint(Digite_uma_cidade);
        searchEditText.setHintTextColor(getResources().getColor(cinzaClaro, getContext().getTheme()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                moradiasAdapter.notifyDataSetChanged();
            }
        }
    });

    private void configAdapter() {
        moradiasAdapter = new MoradiasAdapter(getContext(), new MoradiasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), MoradiaExpandir.class);
                intent.putExtra(TaskConstants.SHARED.EXTRA_SHOW, db.get(position));
                Log.e("INTENT INICIADO", ":" + db.get(position).toString());
                mStartForResult.launch(intent);
            }
        });
        binding.rvMoradias.setAdapter(moradiasAdapter);
    }


    private void getPostCidadesBack2(String cidade) {
        db3.clear();
        db.clear();
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
                            if (tempDb.get(i).getPostMoradia() != null) {
                                db.add(tempDb.get(i));
                            }
                        }

                        Log.e("getPostCidadesBack2", "IF MOSTRAR: db "+ db.size());
                        Log.e("getPostCidadesBack2", "IF MOSTRAR: db2 "+ db2.size());

                        dbCidadesSegurar.clear();
                        dbCidadesSegurar.addAll(db);
                        for(int i = 0; i< db2.size(); i++){
                            if(db2.get(i).getCidade().equals(cidade)){
                                db3.add(db2.get(i));
                            }
                        }

                        db.clear();
                        db.addAll(db3);

                        moradiasAdapter.setPostagens(db);

                    } else {
                        mostrarErro();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
            }
        });

    }


    private void getPostCidadesBack(String cidade) {
        db.clear();
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
                            if (tempDb.get(i).getPostMoradia() != null) {
                                db.add(tempDb.get(i));
                            }
                        }
                        moradiasAdapter.setPostagens(db);

                    } else {
                        mostrarErro();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
            }
        });

    }



    private void getDbBack() {
        db.clear();
        db2.clear();
        cidade =null;
        cidade2=null;
       // if(dbCidadesSegurar!=null){
       //     dbCidadesSegurar.clear();
       // }

        Call<List<Post>> call = retrofitConfig.getService(PostService.class).getAllPost();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {

                    List<Post> tempDb = new ArrayList<>();

                    tempDb.clear();
                    tempDb = response.body();

                    for (int i = 0; tempDb.size() > i; i++) {
                        if (tempDb.get(i).getPostMoradia() != null) {

                            db.add(tempDb.get(i));

                        }
                    }

                    moradiasAdapter.setPostagens(db);
                } else {
                    mostrarErro();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                mostrarErro();
                Log.e("CEPService   ", "Erro ao buscar o cep:" + t.getMessage());
            }
        });

    }

    private void mostrarErro() {
        Toast.makeText(getContext(), "deu ruim !", Toast.LENGTH_SHORT).show();
    }


}