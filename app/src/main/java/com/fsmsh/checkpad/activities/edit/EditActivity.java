package com.fsmsh.checkpad.activities.edit;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.DateUtilities;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import android.text.format.DateFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    int prioridade = -1;
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
            binding.dateStartConteiner.setVisibility(View.VISIBLE);

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

        binding.fabSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tarefa.setTarefaNome(binding.titulo.getText().toString());
                tarefa.setDescricao(binding.descricao.getText().toString());
                tarefa.setCategoria(binding.categoria.getText().toString());
                tarefa.setPrioridade(prioridade);

                if (dateStart != null) {
                    tarefa.setDateStart(dateStart.toString());
                    tarefa.setTimeStart(timeStart.toString());
                } else {
                    tarefa.setDateStart("");
                    tarefa.setTimeStart("");
                }

                if (dateLimit != null) {
                    tarefa.setDateLimit(dateLimit.toString());
                    tarefa.setTimeLimit(timeLimit.toString());
                } else {
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
        if (!tarefa.getDateStart().equals("")) {
            dateStart = DateUtilities.toLocalDate(tarefa.getDateStart());
            timeStart = DateUtilities.toLocalTime(tarefa.getTimeStart());
        }

        if (!tarefa.getDateLimit().equals("")) {
            dateLimit = DateUtilities.toLocalDate( tarefa.getDateLimit() );
            timeLimit = DateUtilities.toLocalTime( tarefa.getTimeLimit() );
        }


        binding.titulo.setText(tarefa.getTarefaNome());
        binding.descricao.setText(tarefa.getDescricao());
        binding.categoria.setText(tarefa.getCategoria());

        prioridade = tarefa.getPrioridade();
        if (prioridade == 0) binding.prioridade.setText("Urgente");
        if (prioridade == 1) binding.prioridade.setText("Importante");
        if (prioridade == 2) binding.prioridade.setText("Prioridade média");
        if (prioridade == 3) binding.prioridade.setText("Prioridade baixa");
        if (prioridade == -1) binding.prioridade.setText("Nenhuma prioridade");

        checkDetails(tarefa);

        salvar("editar", tarefa);
    }

    public void adjustCalendar() {

        DateTimeFormatter formatoTempo = DateTimeFormatter.ofPattern("HH:mm");

        // Time Start
        if (dateStart != null) {
            String dataFormatada = DateUtilities.getFormattedDate(dateStart, false);
            binding.dtInicio.setText(dataFormatada);

            String tempoInicial = timeStart.format(formatoTempo);
            binding.timeInicio.setText(tempoInicial);
        }


        // Time Limit
        if (dateLimit != null) {

            String dataFinalFormatada = DateUtilities.getFormattedDate(dateLimit, false);
            binding.dtFim.setText( dataFinalFormatada );

            String tempoFinal = timeLimit.format(formatoTempo);
            binding.timeFim.setText(tempoFinal);
        }

    }

    public void showModalBottomSheet(View view) {
        boolean inicio = false;
        boolean limite = false;
        boolean prioridade = false;
        boolean categoria = false;

        if(dateStart != null) inicio = true;
        if(dateLimit != null) limite = true;
        if(this.prioridade != -1) prioridade = true;
        if( !binding.categoria.getText().toString().equals("") ) categoria = true;

        ModalBottomSheet modalBottomSheet = new ModalBottomSheet(inicio, limite, prioridade, categoria, this, binding);
        modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG);
    }

    public void showPriorityBottomSheet(View view) {
        PriorityBottomSheet priorityBottomSheet = new PriorityBottomSheet(prioridade, this, binding);
        priorityBottomSheet.show(getSupportFragmentManager(), PriorityBottomSheet.TAG);
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
            binding.prioridade.setText("Nenhuma Prioridade");
            prioridade = -1;
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