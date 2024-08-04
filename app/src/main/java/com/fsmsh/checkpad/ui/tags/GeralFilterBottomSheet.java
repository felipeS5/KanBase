package com.fsmsh.checkpad.ui.tags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.util.Database;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class GeralFilterBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ModalBottomSheet";

    View view;
    TagsFragment parent;
    Button confirmButton;

    CheckBox checkBox;
    ChipGroup chipGroup;
    List<Chip> chips;
    Chip[] estadosChips;
    List<String> tagsPresentes;
    boolean[] estadosPresentes;

    public GeralFilterBottomSheet(TagsFragment parent, List<String> tagsPresentes, boolean[] estadosPresentes) {
        this.parent = parent;
        this.tagsPresentes = tagsPresentes;
        this.estadosPresentes = estadosPresentes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_geral_filter, container, false);

        estadosChips = new Chip[] {
                view.findViewById(R.id.chip_filter_not_started),
                view.findViewById(R.id.chip_filter_started),
                view.findViewById(R.id.chip_filter_finished)};

        checkBox = view.findViewById(R.id.check_noTag_filter);
        chipGroup = view.findViewById(R.id.chip_group_filter);
        confirmButton = view.findViewById(R.id.geral_filter_confirm_buttom);
        confirmButton.setOnClickListener(v -> confirmFilter());

        for (int i = 0; i < estadosChips.length; i++) {// seta o check do estadoChip
            estadosChips[i].setChecked(estadosPresentes[i]);
        }

        criarChips();
        checkBox.setChecked(parent.aceitarSemTags);

        return view;
    }

    public void criarChips() {
        chipGroup.removeAllViews();

        chips = new ArrayList<>();
        List<String> allTags = Database.getTags();

        for (String s : allTags) {
            Chip chip = new Chip(getContext());
            chip.setText(s);
            chip.setCheckable(true);

            for (String tagPresente : tagsPresentes) { // verificar se o chip gerado está entre as tags presentes na tarefa
                if (s.equals(tagPresente)) chip.setChecked(true);
            }

            chips.add(chip);
            chipGroup.addView(chip);
        }
    }

    public void confirmFilter() {
        List<String> tags = new ArrayList<>();

        for (Chip chip : chips) {
            if (chip.isChecked()) {
                tags.add(chip.getText().toString());

                // Adiciona os itens checados no começo
                parent.allTags.remove(chip.getText().toString());
                parent.allTags.add(0, chip.getText().toString());
            }
        }

        parent.estadosAtivos = new boolean[] {estadosChips[0].isChecked(), estadosChips[1].isChecked(), estadosChips[2].isChecked()};
        parent.tagsAtivas = tags;
        parent.aceitarSemTags = checkBox.isChecked();
        parent.setChipTags();
        parent.start();

        this.dismiss();
    }

}
