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
import com.fsmsh.checkpad.util.MyPreferences;
import com.fsmsh.checkpad.util.NotificationHelper;
import com.google.android.material.chip.Chip;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    Database database;
    MyPreferences myPreferences;
    Bundle intencao;
    public TimeZone timeZone;
    public LocalDate dateStart;
    public LocalTime timeStart;
    public LocalDate dateLimit;
    public LocalTime timeLimit;
    int prioridade = 4;
    Tarefa tarefa;
    List<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new Database(this);
        myPreferences = new MyPreferences(this);
        setSupportActionBar(binding.toolbarEdit);
        timeZone = TimeZone.getTimeZone("UTC-3");

        intencao = getIntent().getExtras();
        if (intencao.getBoolean("isNovo")) {
            dateStart = DateUtilities.getNextTime().toLocalDate();
            timeStart = DateUtilities.getNextTime().toLocalTime();
            binding.dateStartConteiner.setVisibility(View.VISIBLE);

            salvar("adicionar", new Tarefa());
        } else {
            edit();
        }

    }

    public void exibirDatePicker(View view) {

        LocalDateTime local;
        if (view.getId() == R.id.dtInicio) local = dateStart.atStartOfDay();
        else local = dateLimit.atStartOfDay();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.calendar_selecione_a_data)
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
                .setTitleText(R.string.calendar_selecione_o_horario)
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

                if (acao.equals("adicionar")) {
                    String id = LocalDateTime.now().toString();
                    tarefa.setId(id);
                }

                tarefa.setTarefaNome(binding.titulo.getText().toString());
                tarefa.setDescricao(binding.descricao.getText().toString());
                tarefa.setPrioridade(prioridade);

                if (tags.size() != 0) {
                    String temp = "";

                    for (String s : tags) {
                        if (!s.equals("")) {
                            temp += s + "‖";
                        }
                    }

                    tarefa.setCategoria(temp);

                } else {
                    tarefa.setCategoria("");
                }

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


                // Salvando localmente
                boolean addSuccess;
                if (acao.equals("adicionar")) addSuccess = Database.addTarefa(tarefa);
                else addSuccess = Database.editTarefa(tarefa);

                if (addSuccess) {
                    Toast.makeText(getApplicationContext(), getString(R.string.edit_sucesso_add_save, acao), Toast.LENGTH_SHORT).show();

                    NotificationHelper notificationHelper = new NotificationHelper( getApplicationContext() );
                    notificationHelper.configurarChannel();
                    notificationHelper.removerAgendamento(tarefa.getId()); // Removendo o angendamento antigo (caso haja)
                    notificationHelper.agendarNotificação(tarefa);

                    myPreferences.setSincronizado(false);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.edit_erro_add_save, acao), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void edit() {
        tarefa = Database.getTarefa(intencao.getString("id"));

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

        if (!tarefa.getCategoria().equals("")) {
            String[] tagsArr = tarefa.getCategoria().split("‖");
            tags = Arrays.asList(tagsArr);

            setChipTags();
        }

        prioridade = tarefa.getPrioridade();
        if (prioridade == 0) binding.prioridade.setText(R.string.prioridade_urgente);
        if (prioridade == 1) binding.prioridade.setText(R.string.prioridade_importante);
        if (prioridade == 2) binding.prioridade.setText(R.string.prioridade_media);
        if (prioridade == 3) binding.prioridade.setText(R.string.prioridade_baixa);
        if (prioridade == 4) binding.prioridade.setText(R.string.prioridade_nenhuma);

        checkDetails(tarefa);

        salvar("editar", tarefa);
    }

    public void setChipTags() {
        if (tags.size() != 0) binding.editTagsChipGroup.setVisibility(View.VISIBLE);

        binding.editTagsChipGroup.removeAllViews();
        for (String s : tags) {
            Chip chip = new Chip(this);
            chip.setCheckable(false);
            chip.setChecked(true);
            chip.setText(s);

            if (!s.equals("")) binding.editTagsChipGroup.addView(chip);

        }
        
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
        if(this.prioridade != 4) prioridade = true;
        if(tags.size() != 0) categoria = true;

        ModalBottomSheet modalBottomSheet = new ModalBottomSheet(inicio, limite, prioridade, categoria, this, binding);
        modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG);
    }

    public void showPriorityBottomSheet(View view) {
        PriorityBottomSheet priorityBottomSheet = new PriorityBottomSheet(prioridade, this, binding);
        priorityBottomSheet.show(getSupportFragmentManager(), PriorityBottomSheet.TAG);
    }

    public void showTagsBottomSheet(View view) {
        TagsBottomSheet tagsBottomSheet = new TagsBottomSheet(this, binding, tags);
        tagsBottomSheet.show(getSupportFragmentManager(), TagsBottomSheet.TAG);
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
            binding.prioridade.setText(R.string.prioridade_nenhuma);
            prioridade = 4;
        }

        if (view.getId() == R.id.btnCancelCategory) {
            binding.categoryConteiner.setVisibility(View.GONE);
            tags = new ArrayList<>();
        }
    }

    public void checkDetails(Tarefa tarefa) {
        if (!tarefa.getCategoria().equals("")) binding.categoryConteiner.setVisibility(View.VISIBLE);
        if (tarefa.getPrioridade() != 4) binding.priorityConteiner.setVisibility(View.VISIBLE);

        if (!tarefa.getDateStart().equals("")) binding.dateStartConteiner.setVisibility(View.VISIBLE);
        if (!tarefa.getDateLimit().equals("")) binding.dateLimitConteiner.setVisibility(View.VISIBLE);
    }
}