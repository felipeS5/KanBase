package com.fsmsh.checkpad.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fsmsh.checkpad.databinding.FragmentHomeBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.HomeAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Database database = new Database(getActivity());

        start();

        return root;
    }

    public void start() {

        List<Tarefa> tarefas = Database.getTarefas();

        // Recycler
        HomeAdapter homeAdapter = new HomeAdapter( Database.getTarefas() );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(homeAdapter);


        /*
        // get
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Tarefa> tarefas = Database.getTarefas();
                String linha = "";

                for (Tarefa tarefa : tarefas) {
                    linha += tarefa.getId() +" - "+ tarefa.getTarefaNome()+"\n";
                }

                binding.textHome.setText(linha);
            }
        });

        // edit
        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tarefa tarefa = new Tarefa();
                tarefa.setId(2);
                tarefa.setTarefaNome( "My test" );
                tarefa.setDescricao( "dstest" );
                tarefa.setProgresso( 2 );
                tarefa.setTimeStart( "0000000" );
                tarefa.setTimeLimit( "0002133" );
                tarefa.setCategoria( "categ0" );
                tarefa.setPrioridade( 1 );

                boolean editado = Database.editTarefa(tarefa);

                if (editado) {
                    Toast.makeText(getActivity(), "Sucesso ao editar", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Erro ao editar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // delete
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }
}