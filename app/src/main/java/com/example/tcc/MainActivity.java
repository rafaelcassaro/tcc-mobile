package com.example.tcc;

import static kotlin.concurrent.ThreadsKt.thread;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tcc.databinding.ActivityMainBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.RetrofitConfigToken;
import com.example.tcc.network.SessaoManager;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.constants.TaskConstants;
import com.example.tcc.ui.moradias.MoradiasUsuarioFragment;
import com.example.tcc.ui.postagens.PostagensUsuarioFragment;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;

    private String testMain = "aloualoau";
    private ActivityMainBinding binding;
    private RetrofitConfig retrofitConfig;
    private SecurityPreferences securityPreferences;
    private Usuario usuario;
    private TextView nameHeader;
    private TextView emailHeader;
    private CircleImageView ivPerfil;
    private Picasso picasso;

    public String getStringTeste(){
        return this.testMain;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar.toolbar);
        //setSupportActionBar(findViewById(R.id.toobar_pica));
        SessaoManager sessaoManager = SessaoManager.getInstance();
        sessaoManager.setLoggedIn(true);
        usuario = new Usuario();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        View headerview = navigationView.getHeaderView(0);

        startRetrofit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                nameHeader = headerview.findViewById(R.id.tv_name_header);
                emailHeader = headerview.findViewById(R.id.tv_email_header);
                ivPerfil = headerview.findViewById(R.id.iv_foto_perfil_header);


                picasso = new Picasso.Builder(binding.getRoot().getContext())
                        .downloader(new OkHttp3Downloader(getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                        .build();
            }
        }).start();

        getUserViaApi();
        Log.e("MAIN onResponse", "pos call: "+usuario.toString());
        //nameHeader.setText(usuario.getNome());




        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_moradias, R.id.nav_postagens, R.id.nav_perfil,R.id.nav_anuncio_usuario, R.id.nav_postagens_usuario,
                R.id.nav_novo_anuncio, R.id.nav_feedback, R.id.nav_planos,R.id.nav_nova_postagem)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        if(getIntent().hasExtra("editar_post_tag")){
            navigationView.getMenu().performIdentifierAction(R.id.nav_postagens_usuario, 0);
        } else if (getIntent().hasExtra("editar_postMoradia_tag")) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_anuncio_usuario, 0);
        } else if (getIntent().hasExtra("novo_post_tag")) {
        navigationView.getMenu().performIdentifierAction(R.id.nav_postagens_usuario, 0);
        }else if (getIntent().hasExtra("novo_postMoradia_tag")) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_anuncio_usuario, 0);
        }



        Log.e("MAIN FOTO", "CHAMADA:");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //EMBAIXO: MENU DE SETTINGS
        nameHeader.setText(usuario.getNome());
        emailHeader.setText(usuario.getEmail());

        String nome = usuario.getNomeFotoPerfil();
        picasso.load("http://192.168.1.107:8080/usuarios/fotoperfil/"+ nome).noFade()
                .placeholder(R.drawable.img_not_found_little).into(ivPerfil);

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void getUserViaApi(){
        String idUser = securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY);
        Call<Usuario> call = retrofitConfig.getUserService().getUserById(Long.parseLong(idUser));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()){
                    Usuario tempUser = response.body();
                    usuario= tempUser;
                }
                else {}
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private void startRetrofit(){
        securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

    }

    private OkHttpClient getOkHttpClientWithAuthorization(final String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                okhttp3.Request original = chain.request();
                Request request = original
                        .newBuilder()
                        .addHeader("Authorization", token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        return httpClient.build();

    }

}






//float action button
/*
binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
        }
        });*/