package com.fsmsh.checkpad.activities.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.util.DateUtilities;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class ModalBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ModalBottomSheet";
    public boolean inicio;
    public boolean limite;
    public boolean prioridade;
    public boolean categoria;

    Chip inicioChip;
    Chip limiteChip;
    Chip prioridadeChip;
    Chip categoriaChip;
    Button button;
    View view;
    public EditActivity parent;
    ActivityEditBinding binding;

    public ModalBottomSheet(boolean inicio, boolean limite, boolean prioridade, boolean categoria, EditActivity parent, ActivityEditBinding binding) {
        this.inicio = inicio;
        this.limite = limite;
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
        prioridadeChip = view.findViewById(R.id.chip_priority);
        categoriaChip = view.findViewById(R.id.chip_category);
        button = view.findViewById(R.id.modal_sheet_buttom);

        inicioChip.setChecked(inicio);
        limiteChip.setChecked(limite);
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


        if (prioridadeChip.isChecked()) binding.priorityConteiner.setVisibility(View.VISIBLE);
        else {
            binding.priorityConteiner.setVisibility(View.GONE);
            binding.prioridade.setText("Nenhuma Prioridade");
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
