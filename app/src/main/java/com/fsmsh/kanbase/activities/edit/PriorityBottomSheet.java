package com.fsmsh.kanbase.activities.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.databinding.ActivityEditBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PriorityBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PriorityBottomSheet";

    CheckBox urgenteCheck;
    CheckBox importanteCheck;
    CheckBox mediaCheck;
    CheckBox baixaCheck;
    CheckBox nenhumaCheck;
    int prioridadeAtual;
    View view;
    EditActivity parent;
    ActivityEditBinding binding;

    public PriorityBottomSheet(int prioridadeAtual, EditActivity parent, ActivityEditBinding binding) {
        this.prioridadeAtual = prioridadeAtual;
        this.parent = parent;
        this.binding = binding;
    }

    public PriorityBottomSheet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_priority, container, false);

        // Verifica se há parent (ao mudar o tema por exemplo)
        if (parent==null) {
            this.dismiss();
            return view;
        }

        urgenteCheck = view.findViewById(R.id.checkBox);
        importanteCheck = view.findViewById(R.id.checkBox1);
        mediaCheck = view.findViewById(R.id.checkBox2);
        baixaCheck = view.findViewById(R.id.checkBox3);
        nenhumaCheck = view.findViewById(R.id.checkBox4);

        if (prioridadeAtual == 0) urgenteCheck.setChecked(true);
        if (prioridadeAtual == 1) importanteCheck.setChecked(true);
        if (prioridadeAtual == 2) mediaCheck.setChecked(true);
        if (prioridadeAtual == 3) baixaCheck.setChecked(true);
        if (prioridadeAtual == 4) nenhumaCheck.setChecked(true);

        urgenteCheck.setOnCheckedChangeListener((compoundButton, b) -> confirmPriority(compoundButton, b));
        importanteCheck.setOnCheckedChangeListener((compoundButton, b) -> confirmPriority(compoundButton, b));
        mediaCheck.setOnCheckedChangeListener((compoundButton, b) -> confirmPriority(compoundButton, b));
        baixaCheck.setOnCheckedChangeListener((compoundButton, b) -> confirmPriority(compoundButton, b));
        nenhumaCheck.setOnCheckedChangeListener((compoundButton, b) -> confirmPriority(compoundButton, b));

        return view;
    }

    public void confirmPriority(CompoundButton button, boolean b) {

        if (button.getId() == urgenteCheck.getId()) {
            binding.prioridade.setText(R.string.prioridade_urgente);
            parent.prioridade = 0;
            button.setChecked(true);
        }
        if (button.getId() == importanteCheck.getId()) {
            binding.prioridade.setText(R.string.prioridade_importante);
            parent.prioridade = 1;
            button.setChecked(true);
        }
        if (button.getId() == mediaCheck.getId()) {
            binding.prioridade.setText(R.string.prioridade_media);
            parent.prioridade = 2;
            button.setChecked(true);
        }
        if (button.getId() == baixaCheck.getId()) {
            binding.prioridade.setText(R.string.prioridade_baixa);
            parent.prioridade = 3;
            button.setChecked(true);
        }
        if (button.getId() == nenhumaCheck.getId()) {
            binding.prioridade.setText(R.string.prioridade_nenhuma);
            parent.prioridade = 4;
            button.setChecked(true);
            binding.priorityConteiner.setVisibility(View.GONE);
        }

        this.dismiss();
    }

}
