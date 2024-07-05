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
    LocalDate dateStart;
    LocalTime timeStart;
    LocalDate dateLimit;
    LocalTime timeLimit;

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
            LocalDateTime agora = LocalDateTime.now().atZone(timeZone.toZoneId()).toLocalDateTime();
            agora.plusMinutes(15);
            // todo: arredondar tempo
            dateStart = agora.toLocalDate();
            timeStart = agora.toLocalTime();

            salvar("adicionar", new Tarefa());
        } else {
            edit();
        }

        /*/ checagem pra verificar se o dt limit deve alterar timelimit para now() ou não
        if (timeLimit == null) {
            binding.dtFim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LocalDateTime agora = LocalDateTime.now().atZone(timeZone.toZoneId()).toLocalDateTime();
                    agora.plusMinutes(30);
                    // todo: arredondar tempo
                    dateLimit = agora.toLocalDate();
                    timeLimit = agora.toLocalTime();

                    adjustCalendar();
                }
            });
        }

         */


    }

    public void exibirDatePicker(View view) {

        LocalDateTime local;
        if (view.getId() == R.id.dtInicio) local = dateStart.atStartOfDay();
        else local = dateLimit.atStartOfDay();

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


                if (view.getId() == R.id.dtInicio) dateStart = localNew;
                else dateLimit = localNew;


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

        LocalTime local;
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

                if (view.getId() == R.id.timeInicio)
                    timeStart = LocalTime.of(picker.getHour(), picker.getMinute());
                else
                    timeLimit = LocalTime.of(picker.getHour(), picker.getMinute());

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
                tarefa.setTarefaNome(binding.titulo.getText().toString());
                tarefa.setDescricao(binding.descricao.getText().toString());
                tarefa.setCategoria(binding.categoria.getText().toString());
                //tarefa.setPrioridade( Integer.parseInt(binding.prioridade.getText().toString()) );

                // o set prioridade está assim para testes
                // vou concertar em breve
                ////////////////////////////////////////////////
                tarefa.setPrioridade(-1);
                ////////////////////////////////////////////////

                tarefa.setDateStart(dateStart.toString());
                tarefa.setTimeStart(timeStart.toString());

                if (dateLimit != null) {
                    tarefa.setDateLimit(dateLimit.toString());
                    tarefa.setTimeLimit(timeLimit.toString());
                }
                else {
                    tarefa.setDateLimit("");
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

        // Config data
        String[] dtTemp = tarefa.getDateStart().split("-");
        int[] dtI = {Integer.parseInt(dtTemp[0]), Integer.parseInt(dtTemp[1]), Integer.parseInt(dtTemp[2])};
        String[] tiTemp = tarefa.getTimeStart().split(":");
        int[] tiI = {Integer.parseInt(tiTemp[0]), Integer.parseInt(tiTemp[1])};

        dateStart = LocalDate.of(dtI[0], dtI[1], dtI[2]);
        timeStart = LocalTime.of(tiI[0], tiI[1]);

        if (!tarefa.getDateLimit().equals("")) {
            String[] dtTemp2 = tarefa.getDateLimit().split("-");
            int[] dtI2 = {Integer.parseInt(dtTemp2[0]), Integer.parseInt(dtTemp2[1]), Integer.parseInt(dtTemp2[2])};
            String[] tiTemp2 = tarefa.getTimeLimit().split(":");
            int[] tiI2 = {Integer.parseInt(tiTemp2[0]), Integer.parseInt(tiTemp2[1])};

            dateLimit = LocalDate.of(dtI2[0], dtI2[1], dtI2[2]);
            timeLimit = LocalTime.of(tiI2[0], tiI2[1]);
        }


        binding.titulo.setText(tarefa.getTarefaNome());
        binding.descricao.setText(tarefa.getDescricao());
        binding.categoria.setText(tarefa.getCategoria());
        binding.prioridade.setText(Integer.toString(tarefa.getPrioridade()));

        checkDetails(tarefa);

        salvar("editar", tarefa);
    }

    public void adjustCalendar() {

        // Variáveis padrões
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd 'de' MMM 'de' yyyy");
        DateTimeFormatter formatoTempo = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate hoje = LocalDate.now();
        LocalDate amanha = hoje.plusDays(1);
        LocalDate ontem = hoje.minusDays(1);


        // Time Start
        String dataInicial = dateStart.format(formatoData);

        if (dateStart.toString().equals(hoje.toString())) dataInicial = "Hoje, "+dataInicial;
        else if (dateStart.toString().equals(amanha.toString())) dataInicial = "Amanhã, "+dataInicial;
        else if (dateStart.toString().equals(ontem.toString())) dataInicial = "Ontem, "+dataInicial;
        binding.dtInicio.setText(dataInicial);

        String tempoInicial = timeStart.format(formatoTempo);
        binding.timeInicio.setText(tempoInicial);


        // Time Limit
        if (dateLimit != null) {

            // Exibe data final
            String dataFinal = dateLimit.format(formatoData);

            if (dateLimit.toString().equals(hoje.toString())) dataFinal = "Hoje, "+dataFinal;
            else if (dateLimit.toString().equals(amanha.toString())) dataFinal = "Amanhã, "+dataFinal;
            else if (dateLimit.toString().equals(ontem.toString())) dataFinal = "Ontem, "+dataFinal;
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

    public void checkDetails(Tarefa tarefa) {
        if (!tarefa.getDescricao().equals("")) binding.descricao.setVisibility(View.VISIBLE);
        if (!tarefa.getCategoria().equals("")) binding.categoria.setVisibility(View.VISIBLE);
        if (tarefa.getPrioridade() != -1) binding.prioridade.setVisibility(View.VISIBLE);

        if (!tarefa.getTimeStart().equals("")) binding.dtInicio.setVisibility(View.VISIBLE);
        if (!tarefa.getTimeStart().equals("")) binding.timeInicio.setVisibility(View.VISIBLE);

        if (!tarefa.getTimeLimit().equals("")) binding.dtFim.setVisibility(View.VISIBLE);
        if (!tarefa.getTimeLimit().equals("")) binding.timeFim.setVisibility(View.VISIBLE);
    }
}