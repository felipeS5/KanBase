package com.fsmsh.kanbase.activities.edit;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.databinding.ActivityEditBinding;
import com.fsmsh.kanbase.model.Tarefa;
import com.fsmsh.kanbase.util.Database;
import com.fsmsh.kanbase.util.DateUtilities;
import com.fsmsh.kanbase.util.Helper;
import com.fsmsh.kanbase.util.MyPreferences;
import com.fsmsh.kanbase.util.NotificationHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

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
    int notifyBefore = -1;
    int prioridade = 4;
    Tarefa tarefa;
    List<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.preConfigs(this);

        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new Database(this);
        myPreferences = new MyPreferences(this);

        setSupportActionBar(binding.toolbarEdit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timeZone = TimeZone.getTimeZone("UTC-3");

        intencao = getIntent().getExtras();
        if (intencao.getBoolean("isNovo")) {
            dateStart = DateUtilities.getNextTime().toLocalDate();
            timeStart = DateUtilities.getNextTime().toLocalTime();
            binding.dateStartConteiner.setVisibility(View.VISIBLE);
            binding.notifyConteiner.setVisibility(View.VISIBLE);

            notifyBefore = MyPreferences.getDefaultNotify();
            if (notifyBefore != -1) {
                if (notifyBefore == 60) binding.notifyBebore.setText(R.string._1hr_antes);
                else binding.notifyBebore.setText(getString(R.string.x_mins_antes, notifyBefore));
                binding.notifyConteiner.setVisibility(View.VISIBLE);
            } else {
                binding.notifyConteiner.setVisibility(View.GONE);
            }

            tarefa = new Tarefa();
            String id = LocalDateTime.now().toString();
            tarefa.setId(id);
            salvar("adicionar", tarefa);
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

        binding.fabSalvar.setOnClickListener(view -> {

            if (binding.titulo.getText().toString().equals("")) {
                Toast.makeText(EditActivity.this, R.string.insira_o_nome_da_sua_tarefa, Toast.LENGTH_SHORT).show();
                return;
            }

            if (acao.equals("adicionar")) {
                int[] codes = Helper.createNoRepeatedCodes();
                tarefa.setBroadcastCodeStart(codes[0]);
                tarefa.setBroadcastCodeLimit(codes[1]);
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

            tarefa.setNotifyBefore(notifyBefore);

            tarefa.setNotified(0);

            // Salvando localmente
            boolean addSuccess;
            if (acao.equals("adicionar")) addSuccess = Database.addTarefa(tarefa);
            else addSuccess = Database.editTarefa(tarefa);

            if (addSuccess) {
                Toast.makeText(getApplicationContext(), getString(R.string.edit_sucesso_add_save, acao), Toast.LENGTH_SHORT).show();

                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.configurarChannel();
                if (notifyBefore != -1) notificationHelper.agendarNotificacao(tarefa);
                else notificationHelper.removerAgendamento(tarefa.getBroadcastCodeStart(), tarefa.getBroadcastCodeLimit());

                myPreferences.setSincronizado(false);

                boolean hasPermission = Helper.hasPermission(getApplicationContext());

                //todo Salvar nova tarefa com o estado do fragment no qual foi adicionada
                if (hasPermission) MyPreferences.setPermissionFirstDenied(false);
                else MyPreferences.setPermissionFirstDenied(true);

                if (!hasPermission && notifyBefore != -1) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditActivity.this);
                    builder.setTitle(R.string.atencao_);
                    builder.setMessage(R.string.notificacao_ligada_mas_permissao_negada);

                    builder.setPositiveButton(R.string.entendo, (dialogInterface, i) -> finalizar());
                    builder.setOnDismissListener(dialogInterface -> finalizar());

                    builder.show();
                } else {
                    finalizar();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.edit_erro_add_save, acao), Toast.LENGTH_SHORT).show();
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

        notifyBefore = tarefa.getNotifyBefore();
        if (notifyBefore == 60) binding.notifyBebore.setText(R.string._1hr_antes);
        else binding.notifyBebore.setText(getString(R.string.x_mins_antes, notifyBefore));

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
            String dataFormatada = DateUtilities.getFormattedDate(dateStart, false, getApplicationContext());
            binding.dtInicio.setText(dataFormatada);

            String tempoInicial = timeStart.format(formatoTempo);
            binding.timeInicio.setText(tempoInicial);
        }


        // Time Limit
        if (dateLimit != null) {

            String dataFinalFormatada = DateUtilities.getFormattedDate(dateLimit, false, getApplicationContext());
            binding.dtFim.setText( dataFinalFormatada );

            String tempoFinal = timeLimit.format(formatoTempo);
            binding.timeFim.setText(tempoFinal);
        }

    }

    public void showModalBottomSheet(View view) {
        boolean inicio = false;
        boolean limite = false;
        boolean notify = false;
        boolean prioridade = false;
        boolean categoria = false;

        if(dateStart != null) inicio = true;
        if(dateLimit != null) limite = true;
        if(notifyBefore != -1) notify = true;
        if(this.prioridade != 4) prioridade = true;
        if(tags.size() != 0) categoria = true;

        ModalBottomSheet modalBottomSheet = new ModalBottomSheet(inicio, limite, notify, prioridade, categoria, this, binding);
        modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG);
    }

    public void showNotifyBottomSheet(View view) {
        NotifyBottomSheet notifyBottomSheet = new NotifyBottomSheet(notifyBefore, this, binding);
        notifyBottomSheet.show(getSupportFragmentManager(), PriorityBottomSheet.TAG);
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

        if (view.getId() == R.id.btnCancelNotify) {
            binding.notifyConteiner.setVisibility(View.GONE);
            binding.notifyBebore.setText(R.string.notificacoes_desativadas);
            notifyBefore = -1;
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

        if (dateStart==null && dateLimit==null) { // Remove tbm o notify caso não tenha nenhuma data
            binding.notifyConteiner.setVisibility(View.GONE);
            binding.notifyBebore.setText(R.string.notificacoes_desativadas);
            notifyBefore = -1;
        }

    }

    public void checkDetails(Tarefa tarefa) {
        if (!tarefa.getCategoria().equals("")) binding.categoryConteiner.setVisibility(View.VISIBLE);
        if (tarefa.getPrioridade() != 4) binding.priorityConteiner.setVisibility(View.VISIBLE);
        if (tarefa.getNotifyBefore() != -1) binding.notifyConteiner.setVisibility(View.VISIBLE);

        if (!tarefa.getDateStart().equals("")) binding.dateStartConteiner.setVisibility(View.VISIBLE);
        if (!tarefa.getDateLimit().equals("")) binding.dateLimitConteiner.setVisibility(View.VISIBLE);
    }

    public void finalizar() {
        if (MyPreferences.isFirstTask()) MyPreferences.setShowTutorial(true);
        MyPreferences.setFirstOpen(false);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Verificar se o item selecionado é o botão de voltar
        if (item.getItemId() == android.R.id.home) {
            finish(); // Fecha a Activity atual, voltando para a anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}