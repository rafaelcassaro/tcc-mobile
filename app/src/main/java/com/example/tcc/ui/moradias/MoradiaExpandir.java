package com.example.tcc.ui.moradias;

//import static com.example.tcc.ui.moradias.MoradiasFragment.EXTRA_SHOW;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.tcc.R;
import com.example.tcc.network.RetrofitConfig;
import com.example.tcc.network.entities.Post;
import com.example.tcc.network.repositories.SecurityPreferences;
import com.example.tcc.network.services.PostService;
import com.example.tcc.ui.adapter.ImagemAdapter;
import com.example.tcc.ui.constants.TaskConstants;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoradiaExpandir extends AppCompatActivity {

    private TextView comentarioTv, tipoMoradiaTv, cidadeTv, estadoTv, generoTv, ruaTv, numCasaTv;
    private TextView moradoresTv, quartoTv, garagemTv, petTv, valorTv;
    private CarouselRecyclerview recyclerview;
    private ImagemAdapter adapter;
    private Button whatsappBt;
    private ImageButton denunciarIb;
    private ImageView backButton;
    private TextView imgButton;
    private Post post;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moradia_expandir);

        iniciarViews();
        post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);
        setViewData();
        setDetalhasData();
        setAdapterImage();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoradiaExpandir.super.onBackPressed();
            }
        });

        whatsappBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroTelefone = "+5521980302642";
                openSocialMediaLink("https://wa.me/" + numeroTelefone);

            }
        });

        denunciarIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    SecurityPreferences securityPreferences = new SecurityPreferences(MoradiaExpandir.this);
                    RetrofitConfig retrofitConfig = new RetrofitConfig(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));
                    retrofitConfig.setToken(securityPreferences.getAuthToken(TaskConstants.SHARED.TOKEN_KEY));

                    Call<Void> addDenuncia = retrofitConfig.getService(PostService.class).denunciarPost(post.getId());
                    addDenuncia.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(MoradiaExpandir.this, "Denúncia enviada!", Toast.LENGTH_SHORT).show();
                            denunciarIb.setClickable(false);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });



            }
        });

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSocialMediaMenu(view);
            }
        });
    }

    private void setSocialMediaMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MoradiaExpandir.this, view);
        getMenuInflater().inflate(R.menu.custom_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setIcon(R.drawable.logo);
                // Handle item clicks here
                if (item.getItemId() == R.id.facebook) {
                    if (post.getUsuario().getLink1().length() > 0) {
                        openSocialMediaLink("https://www.facebook.com/" + post.getUsuario().getLink1());
                    } else {
                        Toast.makeText(MoradiaExpandir.this, "Usuario não possui facebook!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.instagram) {
                    if (post.getUsuario().getLink2().length() > 0) {
                        openSocialMediaLink("https://www.instagram.com/" + post.getUsuario().getLink2());
                    } else {
                        Toast.makeText(MoradiaExpandir.this, "Usuario não possui instagram!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.X) {
                    if (post.getUsuario().getLink3().length() > 0) {
                        openSocialMediaLink("https://www.x.com/" + post.getUsuario().getLink3());
                    } else {
                        Toast.makeText(MoradiaExpandir.this, "Usuario não possui Twitter!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                // Add more conditions for other images  MoradiaExpandir.this
                return false;

            }
        });

        popupMenu.show();
    }

    private void setAdapterImage() {
        adapter = new ImagemAdapter(MoradiaExpandir.this);
        adapter.setDbPost(post.getPostMoradia().getFotos());
        recyclerview.setAdapter(adapter);
    }

    private void setDetalhasData() {
        if (post.getPostMoradia().isTipoResidencia()) {
            tipoMoradiaTv.setText(" Casa");
        } else {
            tipoMoradiaTv.setText(" Apartamento");
        }
        if (post.getPostMoradia().getDetalhesMoradia().isGaragem()) {
            garagemTv.setText(" possui");
        } else {
            garagemTv.setText(" não possui");
        }
        if (post.getPostMoradia().getDetalhesMoradia().isPets()) {
            petTv.setText(" possui");
        } else {
            petTv.setText(" não possui");
        }

        if (post.getPostMoradia().getDetalhesMoradia().isQuarto()) {
            quartoTv.setText(" individual");
        } else {
            quartoTv.setText(" compartilhado");
        }
    }

    private void setViewData() {
        comentarioTv.setText(post.getComentario());
        cidadeTv.setText(post.getCidade());
        ruaTv.setText(post.getPostMoradia().getEndereco());
        numCasaTv.setText(String.valueOf(post.getPostMoradia().getNumCasa()));
        moradoresTv.setText(String.valueOf(post.getPostMoradia().getDetalhesMoradia().getMoradores()));
        valorTv.setText(String.valueOf(post.getPostMoradia().getValorAluguel()));
        estadoTv.setText(post.getEstado());
        generoTv.setText(post.getPostMoradia().getDetalhesMoradia().getGeneroMoradia());
    }

    private void openSocialMediaLink(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Caso não haja aplicativo disponível para abrir o link
                startActivity(intent);
                //Toast.makeText(this, "Nenhum aplicativo disponível para abrir o link", Toast.LENGTH_SHORT).show();
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            // Lida com exceção, se ocorrer
        }
    }

    private void iniciarViews() {
        tipoMoradiaTv = findViewById(R.id.tv_tipo_moradia);
        cidadeTv = findViewById(R.id.tv_cidade_usuario_post);
        estadoTv = findViewById(R.id.tv_estado_usuario);
        generoTv = findViewById(R.id.tv_genero_moradia);
        ruaTv = findViewById(R.id.tv_rua);
        numCasaTv = findViewById(R.id.tv_num_casa);
        moradoresTv = findViewById(R.id.tv_ic_moradores);
        quartoTv = findViewById(R.id.tv_ic_quarto);
        garagemTv = findViewById(R.id.tv_ic_garagem);
        petTv = findViewById(R.id.tv_ic_pet);
        valorTv = findViewById(R.id.tv_valor);
        recyclerview = findViewById(R.id.crv_fotos_moradia);
        whatsappBt = findViewById(R.id.bt_wpp);
        imgButton = findViewById(R.id.spiner);
        comentarioTv = findViewById(R.id.tv_comentario);
        backButton = findViewById(R.id.iv_voltar);
        denunciarIb = findViewById(R.id.ib_denunciar_moradia_expandir);
    }
}