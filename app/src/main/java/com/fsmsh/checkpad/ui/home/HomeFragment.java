package com.fsmsh.checkpad.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fsmsh.checkpad.databinding.FragmentHomeBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        start();

        return root;
    }

    public void start() {

        binding.textHome.setText("Hello Test");

        // Add
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tarefa tarefa = new Tarefa();
                tarefa.setTarefaNome( "Test" );
                tarefa.setDescricao( "Desc" );
                tarefa.setProgresso( 1 );
                tarefa.setTimeStart( "1234567890" );
                tarefa.setTimeLimit( "1234567890" );
                tarefa.setCategoria( "categ" );
                tarefa.setPrioridade( 3 );

                boolean addSuccess = Database.addTarefa(tarefa);

                if (addSuccess) {
                    Toast.makeText(getActivity(), "Sucesso ao adicionar", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Erro ao adicionar", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}