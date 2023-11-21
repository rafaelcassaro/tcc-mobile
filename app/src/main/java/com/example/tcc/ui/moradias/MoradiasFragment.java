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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiasFragment extends Fragment {

    private FragmentMoradiasBinding binding;
    private MoradiasAdapter moradiasAdapter;
    private List<Post> db = new ArrayList<>();
    private List<Post> db2 = new ArrayList<>();
    private String cidadeProcurada;
    private SecurityPreferences securityPreferences;
    private RetrofitConfig retrofitConfig;
    private DetalhesBusca detalhesBusca;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMoradiasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

        setSearchBar();

        cidadeProcurada = "";
        db.clear();
        //--------RV---------------
        configAdapter();
        threadGetDataFromApi();

        return root;
    }

    private void threadGetDataFromApi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                detalhesBusca = new DetalhesBusca();
                detalhesBusca.setValorAluguelMaximo(100d);

                //  final Post post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH);
                String cidade = (String) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_SEARCH);
                db2 = (List<Post>) getActivity().getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW_FILTER);


                Log.e("MoradiasFragment", "EXTRA_SHOW_SEARCH: " + cidade);


                if (cidadeProcurada == "" && cidade == null && db2==null) {
                    getDbBack();
                } else if (cidade != null) {
                    getPostCidadesBack(cidade);
                }

                if (db2 !=null) {
                    Log.e("MoradiasFragment", "getDbBackFiltrar inicio: "+ db2.toString());
                    moradiasAdapter.setPostagens(db2);
                    Log.e("MoradiasFragment", "getDbBackFiltrar inicio: ");
                   // getDbBackFiltrar();
                }

            }
        }).start();
    }

    private void setSearchBar() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_search_posts, menu);
                MenuItem searchItem = menu.findItem(R.id.menu_search);
                MenuItem filterItem = menu.findItem(R.id.menu_space_left);
                TextView searchEditText = (TextView) searchItem.getActionView();

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
                        Intent intent = new Intent(getContext(), SearchMoradiaActivity.class);
                        startActivity(intent);
                    }
                });

                filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        Intent intent = new Intent(getContext(), FiltrarMoradiaActivity.class);
                        startActivity(intent);
                        return false;
                    }
                });


            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
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

    private void getPostCidadesBack(String cidade) {
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

    private void getDbBackFiltrar() {
        db.clear();

        Call<List<Post>> call = retrofitConfig.getService(PostService.class).getAllPost();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    int contador = 0;
                    List<Post> tempDb = new ArrayList<>();
                    tempDb.clear();
                    tempDb = response.body();

                    for (int i = 0; tempDb.size() > i; i++) {
                        if (tempDb.get(i).getPostMoradia() != null) {
                            db.add(tempDb.get(i));
                        }
                    }

                    db2.addAll(db);
                    db.removeAll(db);

                    for (int i = 0; db2.size() > i; i++) {
                        if (detalhesBusca.getValorAluguelMaximo() != null) {
                            if (db2.get(i).getPostMoradia().getValorAluguel() >= detalhesBusca.getValorAluguelMaximo()) {
                                db.add(db2.get(i));
                                contador++;
                            }
                        }
                    }

                    if (contador > 0) {
                        db2.removeAll(db2);
                        db2.addAll(db);
                        db.removeAll(db);
                    }
                    contador = 0;

                    for (int i = 0; db2.size() > i; i++) {
                        if (detalhesBusca.getGeneroMoradia() != null) {
                            if (db2.get(i).getPostMoradia().getDetalhesMoradia().getGeneroMoradia().equals(detalhesBusca.getGeneroMoradia())) {
                                db.add(db2.get(i));
                                contador++;
                            }
                        }
                    }

                    if (contador > 0) {
                        db2.removeAll(db2);
                        db2.addAll(db);
                        db.removeAll(db);
                    }
                    contador = 0;


                    Log.e("MoradiasFragment", "db: " + db.toString());
                    Log.e("MoradiasFragment", "dbsize: " + db.size());
                    Log.e("MoradiasFragment", "db2size: " + db2.size());
                    Log.e("MoradiasFragment", "tempDb: " + tempDb.toString());


                    moradiasAdapter.setPostagens(db2);
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

    private void getDbBack() {
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