package com.example.tcc.ui.moradias;

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
import com.example.tcc.ui.postagens.GalleryViewModel;

public class MoradiaExpandir extends AppCompatActivity {

    private TextView tvComentario;
    private ActivityMoradiaExpandirBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // sharedElementEnterTransition =
        setContentView(R.layout.activity_moradia_expandir);

        tvComentario = findViewById(R.id.tv_comentario);

       // tvComentario.setText("alou");



    }
}