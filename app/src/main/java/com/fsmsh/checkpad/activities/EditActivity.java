package com.fsmsh.checkpad.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.ModalBottomSheet;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.DateUtilities;
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
    public TimeZone timeZone;
    public LocalDate dateStart;
    public LocalTime timeStart;
    public LocalDate dateLimit;
    public LocalTime timeLimit;
    Tarefa tarefa;

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
            // todo: arredondar tempo
            dateStart = LocalDate.now();
            timeStart = LocalTime.now();

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
        tarefa = Database.getTarefa(intencao.getInt("id"));

        // Config data
        dateStart = DateUtilities.toLocalDate( tarefa.getDateStart() );
        timeStart = DateUtilities.toLocalTime( tarefa.getTimeStart() );

        if (!tarefa.getDateLimit().equals("")) {
            dateLimit = DateUtilities.toLocalDate( tarefa.getDateLimit() );
            timeLimit = DateUtilities.toLocalTime( tarefa.getTimeLimit() );
        }


        binding.titulo.setText(tarefa.getTarefaNome());
        binding.descricao.setText(tarefa.getDescricao());
        binding.categoria.setText(tarefa.getCategoria());
        binding.prioridade.setText(Integer.toString(tarefa.getPrioridade()));

        checkDetails(tarefa);

        salvar("editar", tarefa);
    }

    public void adjustCalendar() {

        DateTimeFormatter formatoTempo = DateTimeFormatter.ofPattern("HH:mm");

        String dataFormatada = DateUtilities.getFormattedDate(dateStart, false);
        binding.dtInicio.setText( dataFormatada );

        String tempoInicial = timeStart.format(formatoTempo);
        binding.timeInicio.setText(tempoInicial);


        // Time Limit
        if (dateLimit != null) {

            // Exibe data final
            String dataFinalFormatada = DateUtilities.getFormattedDate(dateLimit, false);
            binding.dtFim.setText( dataFinalFormatada );

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

    public void showModalBottomSheet(View view) {
        boolean inicio = false;
        boolean limite = false;
        boolean prioridade = false;
        boolean categoria = false;

        if(dateStart != null) inicio = true;
        if(dateLimit != null) limite = true;
        if(Integer.parseInt(binding.prioridade.getText().toString()) != -1) prioridade = true;
        if( !binding.categoria.getText().toString().equals("") ) categoria = true;

        ModalBottomSheet modalBottomSheet = new ModalBottomSheet(inicio, limite, prioridade, categoria, this, binding);
        modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG);
    }

    public void clearConteiner(View view) {
        if (view.getId() == R.id.btnCancelDateSt) {
            binding.dateStartConteiner.setVisibility(View.GONE);
            dateStart = null;
            timeStart = null;
        }

        if (view.getId() == R.id.btnCancelDateLm) {
            binding.dateLimitConteiner.setVisibility(View.GONE);
            dateLimit = null;
            timeLimit = null;
        }

        if (view.getId() == R.id.btnCancelPriority) {
            binding.priorityConteiner.setVisibility(View.GONE);
            binding.prioridade.setText("-1");
        }

        if (view.getId() == R.id.btnCancelCategory) {
            binding.categoryConteiner.setVisibility(View.GONE);
            binding.categoria.setText("");
        }
    }

    public void checkDetails(Tarefa tarefa) {
        if (!tarefa.getCategoria().equals("")) binding.categoryConteiner.setVisibility(View.VISIBLE);
        if (tarefa.getPrioridade() != -1) binding.priorityConteiner.setVisibility(View.VISIBLE);

        if (!tarefa.getDateStart().equals("")) binding.dateStartConteiner.setVisibility(View.VISIBLE);
        if (!tarefa.getDateLimit().equals("")) binding.dateLimitConteiner.setVisibility(View.VISIBLE);
    }
}