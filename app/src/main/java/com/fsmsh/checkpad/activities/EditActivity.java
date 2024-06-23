package com.fsmsh.checkpad.activities;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.google.android.material.datepicker.DayViewDecorator;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    Database database;
    Bundle intencao;
    TimeZone timeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new Database(this);
        setSupportActionBar(binding.toolbarEdit);
        binding.dtFim.setKeyListener(null);
        timeZone = TimeZone.getTimeZone("UTC-3");

        intencao = getIntent().getExtras();
        if (intencao.getBoolean("isNovo")) salvar("adicionar", null);
        else edit();


    }

    public void exibirDatePicker(View view) {

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long aLong) {
                SimpleDateFormat dtf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault());
                dtf.setTimeZone(timeZone);


                binding.dtInicio.setText(dtf.format(new Date(aLong)));

                LocalDate localDate = Instant.ofEpochMilli(aLong)
                        .atZone(timeZone.toZoneId())
                        .toLocalDate();


                Log.i("tag", localDate.toString());
            }
        });

        datePicker.show(getSupportFragmentManager(), "tag");

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