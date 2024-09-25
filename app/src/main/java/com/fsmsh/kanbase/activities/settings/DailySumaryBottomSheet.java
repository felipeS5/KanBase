package com.fsmsh.kanbase.activities.settings;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.databinding.ActivitySettingsBinding;
import com.fsmsh.kanbase.util.MyPreferences;
import com.fsmsh.kanbase.util.NotificationHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DailySumaryBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "DailySumaryBottomSheet";

    View view;
    SettingsActivity parent;
    ActivitySettingsBinding binding;

    LocalTime localTime;
    MaterialSwitch mySwitch;
    ConstraintLayout containerHorario;
    TextView lblTextView9;
    TextView lblhorario;
    MaterialSwitch sumaryNoTasksActive;
    Button btnConfirm;

    public DailySumaryBottomSheet(SettingsActivity parent, ActivitySettingsBinding binding) {
        this.parent = parent;
        this.binding = binding;
    }

    public DailySumaryBottomSheet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_daily_sumary, container, false);

        // Verifica se há parent (ao mudar o tema por exemplo)
        if (parent==null) {
            this.dismiss();
            return view;
        }

        mySwitch = view.findViewById(R.id.switch2);
        containerHorario = view.findViewById(R.id.container_daily_sumary);
        lblTextView9 = view.findViewById(R.id.textView9);
        lblhorario = view.findViewById(R.id.lbl_daily_time);
        sumaryNoTasksActive = view.findViewById(R.id.switch3);
        btnConfirm = view.findViewById(R.id.btn_salvar_daily_sumary);

        // Recupera as configs
        localTime = MyPreferences.getDailySumaryLocalTime();
        mySwitch.setChecked(MyPreferences.isDailySumaryActive());
        sumaryNoTasksActive.setChecked(MyPreferences.isSumaryNoTasksActive());

        if (!mySwitch.isChecked()) {
            lblTextView9.setEnabled(false);
            lblhorario.setEnabled(false);
            sumaryNoTasksActive.setEnabled(false);
        }

        DateTimeFormatter formatoTempo = DateTimeFormatter.ofPattern("HH:mm");
        String tempo = localTime.format(formatoTempo);
        lblhorario.setText(tempo);


        // OnClicks
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    lblTextView9.setEnabled(true);
                    lblhorario.setEnabled(true);
                    sumaryNoTasksActive.setEnabled(true);

                } else {
                    lblTextView9.setEnabled(false);
                    lblhorario.setEnabled(false);
                    sumaryNoTasksActive.setEnabled(false);

                }
            }
        });

        lblhorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirTimePicker();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPreferences.setDailySumaryActive(mySwitch.isChecked());
                MyPreferences.setDailySumaryLocalTime(localTime);
                MyPreferences.setSumaryNoTasksActive(sumaryNoTasksActive.isChecked());

                if (mySwitch.isChecked()) {
                    agendar();
                } else {
                    new NotificationHelper(parent.getApplicationContext()).removerAgendamentoDiario();
                }

                DailySumaryBottomSheet.this.dismiss();
            }
        });


        return view;
    }

    public void exibirTimePicker() {
        boolean is24H = DateFormat.is24HourFormat(parent.getApplicationContext());
        int format;
        if (is24H) format = TimeFormat.CLOCK_24H;
        else format = TimeFormat.CLOCK_12H;

        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(format)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(localTime.getHour())
                .setMinute(localTime.getMinute())
                .setTitleText(R.string.calendar_selecione_o_horario)
                .build();
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localTime = LocalTime.of(picker.getHour(), picker.getMinute());

                DateTimeFormatter formatoTempo = DateTimeFormatter.ofPattern("HH:mm");
                String tempo = localTime.format(formatoTempo);

                lblhorario.setText(tempo);

            }
        });

        picker.show(parent.getSupportFragmentManager(), "tag");

    }

    public void agendar() {
        NotificationHelper notificationHelper = new NotificationHelper(parent.getApplicationContext());
        notificationHelper.configurarChannel();
        notificationHelper.removerAgendamentoDiario();
        notificationHelper.agendarNotificacaoDiaria();
    }

}
