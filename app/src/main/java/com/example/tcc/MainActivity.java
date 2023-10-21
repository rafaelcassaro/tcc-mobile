package com.example.tcc;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


import com.example.tcc.databinding.ActivityMainBinding;
import com.example.tcc.network.SessaoManager;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.ui.moradias.MoradiasUsuarioFragment;
import com.example.tcc.ui.postagens.PostagensUsuarioFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;

    private String testMain = "aloualoau";
    private ActivityMainBinding binding;


    public String getStringTeste(){
        return this.testMain;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar.toolbar);
        SessaoManager sessaoManager = SessaoManager.getInstance();
        sessaoManager.setLoggedIn(true);


        if(getIntent().hasExtra("editar_post_tag")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, new PostagensUsuarioFragment())
                    .commit();
        }


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
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



    }

    /*@Override
    protected void onResume() {
        super.onResume();
        String fragmentTag = getIntent().getStringExtra("fragment_tag");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if ("SeuFragmentTag".equals(fragmentTag)) {
            MoradiasUsuarioFragment seuFragment = new MoradiasUsuarioFragment();
            transaction.replace(R.id.nav_anuncio_usuario, seuFragment);
            transaction.addToBackStack(null); // Opcional para permitir a navegação de volta
        }

        transaction.commit();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //EMBAIXO: MENU DE SETTINGS

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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