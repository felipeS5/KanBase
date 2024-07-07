package com.fsmsh.checkpad.activities.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.EditActivity;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

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
    EditActivity parent;
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
        view = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false);

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

        if (inicioChip.isChecked()) binding.dateStartConteiner.setVisibility(View.VISIBLE);
        else binding.dateStartConteiner.setVisibility(View.GONE);

        if (limiteChip.isChecked()) binding.dateLimitConteiner.setVisibility(View.VISIBLE);
        else binding.dateLimitConteiner.setVisibility(View.GONE);

        if (prioridadeChip.isChecked()) binding.prioridade.setVisibility(View.VISIBLE);
        else {
            binding.prioridade.setVisibility(View.GONE);
            binding.prioridade.setText("-1");
        }

        if (categoriaChip.isChecked()) binding.categoria.setVisibility(View.VISIBLE);
        else {
            binding.categoria.setVisibility(View.GONE);
            binding.categoria.setText("");
        }

        this.dismiss();
    }

}