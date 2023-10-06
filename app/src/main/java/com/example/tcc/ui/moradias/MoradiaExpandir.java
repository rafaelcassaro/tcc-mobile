package com.example.tcc.ui.moradias;

import static com.example.tcc.ui.moradias.MoradiasFragment.EXTRA_SHOW;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tcc.R;
import com.example.tcc.databinding.ActivityMoradiaExpandirBinding;
import com.example.tcc.network.entities.Post;
import com.example.tcc.ui.postagens.GalleryViewModel;

public class MoradiaExpandir extends AppCompatActivity {

    private TextView comentarioTv;
    private TextView tipoMoradiaTv;
    private TextView cidadeTv;
    private TextView estadoTv;
    private TextView generoTv;
    private TextView ruaTv;
    private TextView numCasaTv;
    private TextView moradoresTv;
    private TextView quartoTv;
    private TextView garagemTv;
    private TextView petTv;
    private TextView valorTv;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // sharedElementEnterTransition =
        setContentView(R.layout.activity_moradia_expandir);
        iniciarViews();
        comentarioTv = findViewById(R.id.tv_comentario);
        final Post post = (Post) getIntent().getSerializableExtra(EXTRA_SHOW);


        comentarioTv.setText(post.getComentario());
        tipoMoradiaTv.setText(post.getPostMoradia().getTipoResidencia());
        cidadeTv.setText(post.getCidade());
        ruaTv.setText(post.getPostMoradia().getEndereco());
        numCasaTv.setText(String.valueOf(post.getPostMoradia().getNumCasa()));
        moradoresTv.setText(String.valueOf(post.getPostMoradia().getDetalhesMoradia().getMoradores()));
        quartoTv.setText(post.getPostMoradia().getDetalhesMoradia().getQuarto());
        garagemTv.setText(post.getPostMoradia().getDetalhesMoradia().getGaragem());
        petTv.setText(post.getPostMoradia().getDetalhesMoradia().getPets());
        valorTv.setText(String.valueOf(post.getPostMoradia().getValorAluguel()));
        estadoTv.setText(post.getEstado());
        generoTv.setText(post.getPostMoradia().getDetalhesMoradia().getGeneroMoradia());


    }


    private void iniciarViews(){
        tipoMoradiaTv = findViewById(R.id.tv_tipo_moradia);
        cidadeTv = findViewById(R.id.tv_cidade);
        estadoTv = findViewById(R.id.tv_estado);
        generoTv = findViewById(R.id.tv_genero_moradia);
        ruaTv = findViewById(R.id.tv_rua);
        numCasaTv = findViewById(R.id.tv_num_casa);
        moradoresTv = findViewById(R.id.tv_ic_moradores);
        quartoTv = findViewById(R.id.tv_ic_quarto);
        garagemTv = findViewById(R.id.tv_ic_garagem);
        petTv = findViewById(R.id.tv_ic_pet);
        valorTv = findViewById(R.id.tv_valor);

    }
}