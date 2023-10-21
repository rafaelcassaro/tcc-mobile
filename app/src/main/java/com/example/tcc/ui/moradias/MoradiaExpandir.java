package com.example.tcc.ui.moradias;

//import static com.example.tcc.ui.moradias.MoradiasFragment.EXTRA_SHOW;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.tcc.R;
import com.example.tcc.network.entities.Post;
import com.example.tcc.ui.constants.TaskConstants;

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
        final Post post = (Post) getIntent().getSerializableExtra(TaskConstants.SHARED.EXTRA_SHOW);


        comentarioTv.setText(post.getComentario());

        cidadeTv.setText(post.getCidade());
        ruaTv.setText(post.getPostMoradia().getEndereco());
        numCasaTv.setText(String.valueOf(post.getPostMoradia().getNumCasa()));
        moradoresTv.setText(String.valueOf(post.getPostMoradia().getDetalhesMoradia().getMoradores()));


        if(post.getPostMoradia().isTipoResidencia()){
            tipoMoradiaTv.setText(" Casa");
        }
        else{
            tipoMoradiaTv.setText(" Apartamento");
        }
        if(post.getPostMoradia().getDetalhesMoradia().isGaragem()){
            garagemTv.setText(" possui");
        }
        else{
            garagemTv.setText(" não possui");
        }
        if(post.getPostMoradia().getDetalhesMoradia().isPets()){
            petTv.setText(" possui");
        }
        else{
            petTv.setText(" não possui");
        }

        if(post.getPostMoradia().getDetalhesMoradia().isQuarto()){
            quartoTv.setText(" individual");
        }
        else{
            quartoTv.setText(" compartilhado");
        }

        valorTv.setText(String.valueOf(post.getPostMoradia().getValorAluguel()));
        estadoTv.setText(post.getEstado());
        generoTv.setText(post.getPostMoradia().getDetalhesMoradia().getGeneroMoradia());


    }


    private void iniciarViews(){
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

    }
}