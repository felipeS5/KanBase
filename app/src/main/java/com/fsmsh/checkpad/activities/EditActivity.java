package com.fsmsh.checkpad.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    Database database;
    Bundle intencao;
    TimeZone timeZone;
    LocalDateTime timeStart;
    LocalDateTime timeLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new Database(this);
        setSupportActionBar(binding.toolbarEdit);
        timeZone = TimeZone.getTimeZone("UTC-3");

        intencao = getIntent().getExtras();
        if (intencao.getBoolean("isNovo")) {
            timeStart = LocalDateTime.now().atZone(timeZone.toZoneId()).toLocalDateTime();
            timeStart = timeStart.plusMinutes(30);
            // todo: arredondar tempo

            salvar("adicionar", new Tarefa());
        } else {
            edit();
        }

        // checagem pra verificar se o dt limit deve alterar timelimit para now() ou não
        if (timeLimit == null) {
            binding.dtFim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timeLimit = LocalDateTime.now().atZone(timeZone.toZoneId()).toLocalDateTime();
                    timeLimit = timeLimit.plusHours(1);
                    // todo: arredondar tempo

                    adjustCalendar();
                }
            });
        }


    }

    public void exibirDatePicker(View view) {

        LocalDateTime local;
        if (view.getId() == R.id.dtInicio) local = timeStart;
        else local = timeLimit;

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setSelection(local.atZone(timeZone.toZoneId()).toInstant().toEpochMilli())
                .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long aLong) {
                LocalDate localNew = Instant.ofEpochMilli(aLong)
                        .atZone(timeZone.toZoneId())
                        .toLocalDate();


                if (view.getId() == R.id.dtInicio) timeStart = localNew.atTime(timeStart.getHour(), timeStart.getMinute());
                else timeLimit = localNew.atTime(timeLimit.getHour(), timeLimit.getMinute());


                adjustCalendar();
            }
        });

        datePicker.show(getSupportFragmentManager(), "tag");

    }


    public void exibirTimePicker(View view) {

        boolean is24H = DateFormat.is24HourFormat(getApplicationContext());
        int format;
        if (is24H) format = TimeFormat.CLOCK_24H;
        else format = TimeFormat.CLOCK_12H;

        LocalDateTime local;
        if (view.getId() == R.id.timeInicio) local = timeStart;
        else local = timeLimit;

        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(format)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(local.getHour())
                .setMinute(local.getMinute())
                .setTitleText("Selecione o horário")
                .build();
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (view.getId() == R.id.timeInicio) {
                    LocalDate localDate = timeStart.toLocalDate();
                    timeStart = localDate.atTime(picker.getHour(), picker.getMinute());
                } else {
                    LocalDate localDate = timeLimit.toLocalDate();
                    timeLimit = localDate.atTime(picker.getHour(), picker.getMinute());
                }

                adjustCalendar();
            }
        });

        picker.show(getSupportFragmentManager(), "tag");

    }

    public void salvar(String acao, Tarefa tarefa) {
        adjustCalendar();

        binding.btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tarefa.setTarefaNome( binding.titulo.getText().toString() );
                tarefa.setDescricao( binding.descricao.getText().toString() );
                tarefa.setCategoria( binding.categoria.getText().toString() );
                //tarefa.setPrioridade( Integer.parseInt(binding.prioridade.getText().toString()) );

                // o set prioridade está assim para testes
                // vou concertar em breve
                ////////////////////////////////////////////////
                tarefa.setPrioridade( 0 );
                ////////////////////////////////////////////////

                long timeSt = timeStart
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
                tarefa.setTimeStart(Long.toString(timeSt));

                long timeLim;
                if (timeLimit != null) {
                    timeLim = timeLimit
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli();
                    tarefa.setTimeLimit(Long.toString(timeLim));
                } else {
                    tarefa.setTimeLimit("");
                }

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

        timeStart = Instant.ofEpochMilli(Long.parseLong(tarefa.getTimeStart()))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        if (!tarefa.getTimeLimit().equals("")) {
            timeLimit = Instant.ofEpochMilli(Long.parseLong(tarefa.getTimeLimit()))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }


        binding.titulo.setText(tarefa.getTarefaNome());
        binding.descricao.setText(tarefa.getDescricao());
        binding.categoria.setText(tarefa.getCategoria());
        binding.prioridade.setText(Integer.toString(tarefa.getPrioridade()));

        salvar("editar", tarefa);
    }

    public void adjustCalendar() {

        // Variáveis padrões
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        DateTimeFormatter formatoTempo = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate hoje = LocalDate.now();
        LocalDate amanha = hoje.plusDays(1);
        LocalDate ontem = hoje.minusDays(1);


        // Time Start
        String dataInicial = timeStart.format(formatoData);

        LocalDate localDateStart = timeStart.toLocalDate();

        if (localDateStart.toString().equals(hoje.toString())) dataInicial = "Hoje, "+dataInicial;
        else if (localDateStart.toString().equals(amanha.toString())) dataInicial = "Amanhã, "+dataInicial;
        else if (localDateStart.toString().equals(ontem.toString())) dataInicial = "Ontem, "+dataInicial;
        binding.dtInicio.setText(dataInicial);

        String tempoInicial = timeStart.format(formatoTempo);
        binding.timeInicio.setText(tempoInicial);


        // Time Limit
        if (timeLimit != null) {

            // Campo data/tempo final
            Drawable leftDrawable = getResources().getDrawable(R.drawable.baseline_today_24, null);
            binding.dtFim.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
            binding.dtFim.setCompoundDrawablePadding(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, this.getResources().getDisplayMetrics())));
            binding.dtFim.setTextSize(16);
            binding.timeFim.setVisibility(View.VISIBLE);

            // Exibe data final
            String dataFinal = timeLimit.format(formatoData);
            LocalDate localDateEnd = timeLimit.toLocalDate();

            if (localDateEnd.toString().equals(hoje.toString())) dataFinal = "Hoje, "+dataFinal;
            else if (localDateEnd.toString().equals(amanha.toString())) dataFinal = "Amanhã, "+dataFinal;
            else if (localDateEnd.toString().equals(ontem.toString())) dataFinal = "Ontem, "+dataFinal;
            binding.dtFim.setText(dataFinal);

            // Exibe tempo final
            String tempoFinal = timeLimit.format(formatoTempo);
            binding.timeFim.setText(tempoFinal);

            binding.dtFim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exibirDatePicker(view);
                }
            });

            binding.timeFim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exibirTimePicker(view);
                }
            });
        }

    }
}