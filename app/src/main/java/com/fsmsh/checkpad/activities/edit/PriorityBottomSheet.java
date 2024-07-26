package com.fsmsh.checkpad.activities.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.priority_botton_sheet_content, container, false);

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
            binding.prioridade.setText("Urgente");
            parent.prioridade = 0;
            button.setChecked(true);
        }
        if (button.getId() == importanteCheck.getId()) {
            binding.prioridade.setText("Importante");
            parent.prioridade = 1;
            button.setChecked(true);
        }
        if (button.getId() == mediaCheck.getId()) {
            binding.prioridade.setText("Prioridade média");
            parent.prioridade = 2;
            button.setChecked(true);
        }
        if (button.getId() == baixaCheck.getId()) {
            binding.prioridade.setText("Prioridade baixa");
            parent.prioridade = 3;
            button.setChecked(true);
        }
        if (button.getId() == nenhumaCheck.getId()) {
            binding.prioridade.setText("Nenhuma prioridade");
            parent.prioridade = 4;
            button.setChecked(true);
            binding.priorityConteiner.setVisibility(View.GONE);
        }

        this.dismiss();
    }

}
