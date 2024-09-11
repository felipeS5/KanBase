package com.fsmsh.checkpad.activities.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.util.DateUtilities;
import com.fsmsh.checkpad.util.MyPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class ModalBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ModalBottomSheet";
    public boolean inicio;
    public boolean limite;
    public boolean notify;
    public boolean prioridade;
    public boolean categoria;

    Chip inicioChip;
    Chip limiteChip;
    Chip notifyChip;
    Chip prioridadeChip;
    Chip categoriaChip;
    Button button;
    View view;
    public EditActivity parent;
    ActivityEditBinding binding;

    public ModalBottomSheet(boolean inicio, boolean limite, boolean notify, boolean prioridade, boolean categoria, EditActivity parent, ActivityEditBinding binding) {
        this.inicio = inicio;
        this.limite = limite;
        this.notify = notify;
        this.prioridade = prioridade;
        this.categoria = categoria;
        this.parent = parent;
        this.binding = binding;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_modal, container, false);

        inicioChip = view.findViewById(R.id.chip_start);
        limiteChip = view.findViewById(R.id.chip_limit);
        notifyChip = view.findViewById(R.id.chip_notify);
        prioridadeChip = view.findViewById(R.id.chip_priority);
        categoriaChip = view.findViewById(R.id.chip_category);
        button = view.findViewById(R.id.modal_sheet_buttom);

        inicioChip.setChecked(inicio);
        limiteChip.setChecked(limite);
        notifyChip.setChecked(notify);
        prioridadeChip.setChecked(prioridade);
        categoriaChip.setChecked(categoria);

        button.setOnClickListener(v -> confirmChips());

        return view;
    }

    public void confirmChips() {

        if (inicioChip.isChecked()) {
            if ( !(inicio && inicioChip.isChecked()) ) {
                parent.dateStart = DateUtilities.getNextTime().toLocalDate();
                parent.timeStart = DateUtilities.getNextTime().toLocalTime();
            }
            binding.dateStartConteiner.setVisibility(View.VISIBLE);
        } else {
            binding.dateStartConteiner.setVisibility(View.GONE);
            parent.dateStart = null;
            parent.timeStart = null;
        }

        if (limiteChip.isChecked()) {
            if ( !(limite && limiteChip.isChecked()) ) {
                // time limit adiciona uma hora do inicio caso haja ou um dia caso não haja
                parent.dateLimit = DateUtilities.getNextLimitTime(this).toLocalDate();
                parent.timeLimit = DateUtilities.getNextLimitTime(this).toLocalTime();
            }
            binding.dateLimitConteiner.setVisibility(View.VISIBLE);
        } else{
            binding.dateLimitConteiner.setVisibility(View.GONE);
            parent.dateLimit = null;
            parent.timeLimit = null;
        }

        parent.adjustCalendar();


        if (notifyChip.isChecked()) {
            binding.notifyConteiner.setVisibility(View.VISIBLE);
            parent.notifyBefore = MyPreferences.getDefaultNotify();

            int notifyBefore = MyPreferences.getDefaultNotify();
            if (notifyBefore != -1) {
                if (notifyBefore == 60) binding.notifyBebore.setText(R.string._1hr_antes);
                else binding.notifyBebore.setText(getString(R.string.x_mins_antes, notifyBefore));

            } else {
                binding.notifyBebore.setText(R.string.notificacoes_desativadas);

            }

        } else {
            binding.notifyConteiner.setVisibility(View.GONE);
            binding.notifyBebore.setText(R.string.notificacoes_desativadas);
            parent.notifyBefore = -1;
        }

        if (prioridadeChip.isChecked()) binding.priorityConteiner.setVisibility(View.VISIBLE);
        else {
            binding.priorityConteiner.setVisibility(View.GONE);
            binding.prioridade.setText(R.string.prioridade_nenhuma);
            parent.prioridade = 4;
        }

        if (categoriaChip.isChecked()) binding.categoryConteiner.setVisibility(View.VISIBLE);
        else {
            binding.categoryConteiner.setVisibility(View.GONE);
            parent.tags = new ArrayList<>();
        }

        this.dismiss();
    }

}
