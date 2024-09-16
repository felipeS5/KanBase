package com.fsmsh.checkpad.activities.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NotifyBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PriorityBottomSheet";

    CheckBox _1hr;
    CheckBox _30min;
    CheckBox _15min;
    CheckBox _5min;
    CheckBox _desativar;
    int notifyBefore;
    View view;
    EditActivity parent;
    ActivityEditBinding binding;

    public NotifyBottomSheet(int notifyBefore, EditActivity parent, ActivityEditBinding binding) {
        this.notifyBefore = notifyBefore;
        this.parent = parent;
        this.binding = binding;
    }

    public NotifyBottomSheet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_notify, container, false);

        // Verifica se há parent (ao mudar o tema por exemplo)
        if (parent==null) {
            this.dismiss();
            return view;
        }

        _1hr = view.findViewById(R.id.t1hr_antes);
        _30min = view.findViewById(R.id.t30min_antes);
        _15min = view.findViewById(R.id.t15min_antes);
        _5min = view.findViewById(R.id.t5min_antes);
        _desativar = view.findViewById(R.id._desativar);

        if (notifyBefore == 60) _1hr.setChecked(true);
        if (notifyBefore == 30) _30min.setChecked(true);
        if (notifyBefore == 15) _15min.setChecked(true);
        if (notifyBefore == 5) _5min.setChecked(true);
        if (notifyBefore == -1) _desativar.setChecked(true);

        _1hr.setOnCheckedChangeListener((compoundButton, b) -> confirmTime(compoundButton, b));
        _30min.setOnCheckedChangeListener((compoundButton, b) -> confirmTime(compoundButton, b));
        _15min.setOnCheckedChangeListener((compoundButton, b) -> confirmTime(compoundButton, b));
        _5min.setOnCheckedChangeListener((compoundButton, b) -> confirmTime(compoundButton, b));
        _desativar.setOnCheckedChangeListener((compoundButton, b) -> confirmTime(compoundButton, b));

        return view;
    }

    public void confirmTime(CompoundButton button, boolean b) {

        if (button.getId() == _1hr.getId()) {
            binding.notifyBebore.setText(R.string._1hr_antes);
            parent.notifyBefore = 60;
            button.setChecked(true);
        }
        if (button.getId() == _30min.getId()) {
            binding.notifyBebore.setText(R.string._30min_antes);
            parent.notifyBefore = 30;
            button.setChecked(true);
        }
        if (button.getId() == _15min.getId()) {
            binding.notifyBebore.setText(R.string._15min_antes);
            parent.notifyBefore = 15;
            button.setChecked(true);
        }
        if (button.getId() == _5min.getId()) {
            binding.notifyBebore.setText(R.string._5min_antes);
            parent.notifyBefore = 5;
            button.setChecked(true);
        }
        if (button.getId() == _desativar.getId()) {
            binding.notifyBebore.setText(R.string.notificacoes_desativadas);
            parent.notifyBefore = -1;
            button.setChecked(true);
            binding.notifyConteiner.setVisibility(View.GONE);
        }

        this.dismiss();
    }

}
