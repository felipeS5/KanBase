package com.fsmsh.checkpad.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new Database(this);
        setSupportActionBar(binding.toolbarEdit);

    }

    public void salvar(View v) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTarefaNome( binding.titulo.getText().toString() );
        tarefa.setDescricao( binding.descricao.getText().toString() );
        tarefa.setProgresso( 0 );
        tarefa.setTimeStart( binding.dtInicio.getText().toString() );
        tarefa.setTimeLimit( binding.dtFim.getText().toString() );
        tarefa.setCategoria( binding.categoria.getText().toString() );
        tarefa.setPrioridade( Integer.parseInt(binding.prioridade.getText().toString()) );

        boolean addSuccess = Database.addTarefa(tarefa);

        if (addSuccess) {
            Toast.makeText(getApplicationContext(), "Sucesso ao adicionar", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Erro ao adicionar", Toast.LENGTH_SHORT).show();
        }
    }
}