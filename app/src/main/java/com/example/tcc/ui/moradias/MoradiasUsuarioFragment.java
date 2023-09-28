package com.example.tcc.ui.moradias;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.tcc.databinding.FragmentMoradiaUsuarioBinding;
import com.example.tcc.db.MoradiasDb;
import com.example.tcc.ui.adapter.MoradiasAdapter;

public class MoradiasUsuarioFragment extends Fragment {

    private FragmentMoradiaUsuarioBinding binding;
    private RecyclerView.Adapter adapter;
    public static final String EXTRA_SHOW = "EXTRA_SHOW";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // HomeViewModel homeViewModel =
        //        new ViewModelProvider(this).get(HomeViewModel.class);

        binding = com.example.tcc.databinding.FragmentMoradiaUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //-------------rv
        binding.rvMoradiasUsuario.setHasFixedSize(true);

        adapter = new MoradiasAdapter(container.getContext(), new MoradiasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), MoradiaExpandir.class);
                intent.putExtra(EXTRA_SHOW, MoradiasDb.myDataset.get(position));
                mStartForResult2.launch(intent);

            }
        });

        binding.rvMoradiasUsuario.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    ActivityResultLauncher<Intent> mStartForResult2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                adapter.notifyDataSetChanged();
            }
        }
    });

}