package com.fsmsh.checkpad.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.EditActivity;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.HomeAdapter;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        textView = view.findViewById(R.id.textView2);

        return view;
    }

    public void start() {

        // Recycler
        HomeAdapter homeAdapter = new HomeAdapter( Database.getTarefas(0) );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(homeAdapter);

        textView.setText(this.getChildFragmentManager().toString());

        /*/ delete
        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(2);

                boolean deletado = Database.deleteTarefa(tarefa);

                if (deletado) {
                    Toast.makeText(getActivity(), "Sucesso ao deletar", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Erro ao deletar", Toast.LENGTH_SHORT).show();
                }
            }
        });

         */

    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }
}