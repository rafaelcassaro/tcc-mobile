package com.example.tcc.ui.postagens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcc.databinding.FragmentPostagensBinding;
import com.example.tcc.db.PostagemDb;
import com.example.tcc.db.models.Postagem;
import com.example.tcc.ui.adapter.PostagemAdapter;
import com.example.tcc.ui.moradias.TesteActivity;

public class PostagensFragment extends Fragment {


    private FragmentPostagensBinding binding;
    private RecyclerView.Adapter adapter;
    private PostagemAdapter adapter2;
    public static final String EXTRA_SHOW = "EXTRA_SHOW";


    ActivityResultLauncher<Intent> mStartForResult2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentPostagensBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // --------- recycler view  --------

        adapter2 = new PostagemAdapter(getContext(), new PostagemDb());
        binding.rvPostagens.setAdapter(adapter2);

        adapter2.setOnItemClickListener(new PostagemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {


                // Inicie a atividade desejada ao clicar em um item
                Intent intent = new Intent(getContext(), TesteActivity.class);
                intent.putExtra("EXTRA_SHOW", PostagemDb.myDataset.get(position)); // Passe os dados necessários para a próxima atividade
                mStartForResult2.launch(intent);
            }
        });

        binding.rvPostagens.setHasFixedSize(true);
        adapter = new PostagemAdapter(container.getContext(), new PostagemDb());
        binding.rvPostagens.setAdapter(adapter);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}