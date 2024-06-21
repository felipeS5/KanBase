package com.fsmsh.checkpad.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    Database database;
    Bundle intencao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new Database(this);
        setSupportActionBar(binding.toolbarEdit);

        intencao = getIntent().getExtras();
        if (intencao.getBoolean("isNovo")) salvar("adicionar", null);
        else edit();


    }

    public void salvar(String acao, Tarefa tarefa) {
        binding.btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tarefa.setTarefaNome( binding.titulo.getText().toString() );
                tarefa.setDescricao( binding.descricao.getText().toString() );
                tarefa.setTimeStart( binding.dtInicio.getText().toString() );
                tarefa.setTimeLimit( binding.dtFim.getText().toString() );
                tarefa.setCategoria( binding.categoria.getText().toString() );
                tarefa.setPrioridade( Integer.parseInt(binding.prioridade.getText().toString()) );

                boolean addSuccess;
                if (acao.equals("adicionar")) addSuccess = Database.addTarefa(tarefa);
                else addSuccess = Database.editTarefa(tarefa);

                if (addSuccess) {
                    Toast.makeText(getApplicationContext(), "Sucesso ao "+acao, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao "+acao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void edit() {
        Tarefa tarefa = Database.getTarefa(intencao.getInt("id"));

        binding.titulo.setText(tarefa.getTarefaNome());
        binding.descricao.setText(tarefa.getDescricao());
        binding.dtInicio.setText(tarefa.getTimeStart());
        binding.dtFim.setText(tarefa.getTimeLimit());
        binding.categoria.setText(tarefa.getCategoria());
        binding.prioridade.setText(Integer.toString(tarefa.getPrioridade()));

        salvar("editar", tarefa);
    }
}