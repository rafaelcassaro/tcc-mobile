package com.example.tcc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tcc.databinding.ActivityMainBinding;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Usuario;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.UserService;
import com.example.tcc.ui.constants.TaskConstants;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private RetrofitConfig retrofitConfig;
    private SecurityPreferences securityPreferences;
    private Usuario usuario;
    private TextView nameHeader, emailHeader;
    private CircleImageView ivPerfil;
    private Picasso picasso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerview = navigationView.getHeaderView(0);
        ivPerfil = headerview.findViewById(R.id.iv_foto_perfil_header);
        usuario = new Usuario();


        startRetrofit();
        headerNewThread(headerview);
        getUserViaApi();
        Log.e("MAIN onResponse", "pos call: " + usuario.toString());


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_moradias, R.id.nav_postagens, R.id.nav_perfil, R.id.nav_anuncio_usuario, R.id.nav_postagens_usuario,
                R.id.nav_novo_anuncio, R.id.nav_feedback, R.id.nav_planos, R.id.nav_nova_postagem)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        Menu navMenu = navigationView.getMenu();
        MenuItem logoutItem = navMenu.findItem(R.id.nav_logout);

        logoutOption(logoutItem);
        enviarTelaCorreta(navigationView);

    }


    @Override
    protected void onResume() {
        super.onResume();
       // nameHeader.setText(usuario.getNome());
       // emailHeader.setText(usuario.getEmail());
       // String nome = usuario.getNomeFotoPerfil();
       // picasso.load("http://192.168.1.107:8080/usuarios/fotoperfil/" + nome).noFade()
       //         .placeholder(R.drawable.img_not_found_little).into(ivPerfil);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        nameHeader.setText(usuario.getNome());
        emailHeader.setText(usuario.getEmail());

        picasso.load(securityPreferences.getAuthToken(TaskConstants.PATH.URL)+"/usuarios/fotoperfil/" + usuario.getNomeFotoPerfil()).noFade()
                .placeholder(R.drawable.img_not_found_little).into(ivPerfil);
        Log.e("onCreateOptionsMenu","ivPerfil");
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void enviarTelaCorreta(NavigationView navigationView) {
        if (getIntent().hasExtra("editar_post_tag")) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_postagens_usuario, 0);
        } else if (getIntent().hasExtra("editar_postMoradia_tag")) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_anuncio_usuario, 0);
        } else if (getIntent().hasExtra("novo_post_tag")) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_postagens_usuario, 0);
        } else if (getIntent().hasExtra("novo_postMoradia_tag")) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_anuncio_usuario, 0);
        } else if (getIntent().hasExtra("Search_post_cidades")) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_postagens, 0);
        }
    }

    private void logoutOption(MenuItem logoutItem) {
        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                AlertDialog.Builder confirmarSaida = new AlertDialog.Builder(MainActivity.this);
                confirmarSaida.setTitle("Atenção!");
                confirmarSaida.setMessage("Deseja sair da sua conta ? \n");
                confirmarSaida.setCancelable(true);
                confirmarSaida.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        finish(); // Encerrar a atividade atual

                    }
                });
                confirmarSaida.setNegativeButton("Não", null);
                confirmarSaida.create().show();

                return true;
            }
        });
    }

    private void headerNewThread(View headerview) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                nameHeader = headerview.findViewById(R.id.tv_name_header);
                emailHeader = headerview.findViewById(R.id.tv_email_header);


                picasso = new Picasso.Builder(binding.getRoot().getContext())
                        .downloader(new OkHttp3Downloader(retrofitConfig.getOkHttpClientWithAuthorization(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY))))
                        .build();
                Log.e("headerNewThread","ivPerfil");
            }
        }).start();
    }

    private void getUserViaApi() {
        String idUser = securityPreferences.getAuthToken(TaskConstants.SHARED.PERSON_KEY);
        Call<Usuario> call = retrofitConfig.getService(UserService.class).getUserById(Long.parseLong(idUser));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Usuario tempUser = response.body();
                    usuario = tempUser;
                } else {
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private void startRetrofit() {
        securityPreferences = new SecurityPreferences(binding.getRoot().getContext());
        retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
        Log.e("startRetrofit","ivPerfil");

    }



}
