package com.fsmsh.checkpad.activities.main.tags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.FirebaseHelper;
import com.fsmsh.checkpad.util.MyPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

    public GeralFilterBottomSheet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_geral_filter, container, false);


        // Verifica se há parent (ao mudar o tema por exemplo)
        if (parent==null) {
            this.dismiss();
            return view;
        }

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
            chip.setOnLongClickListener(view -> {
                removeTag(chip);
                return true;
            });

            for (String tagPresente : tagsPresentes) { // verificar se o chip gerado está entre as tags presentes na tarefa
                if (s.equals(tagPresente)) chip.setChecked(true);
            }

            chips.add(chip);
            chipGroup.addView(chip);
        }
    }

    public void removeTag(Chip chip) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(GeralFilterBottomSheet.this.getContext());
        builder.setTitle(R.string.remover_tag_q);
        builder.setMessage(parent.getString(R.string.deseja_realmente_remover_a_tag_x_q, chip.getText().toString()));

        builder.setNegativeButton(R.string.manter, null);

        builder.setPositiveButton(R.string.remover, (dialogInterface, i) -> {
            boolean removed = Database.deleteTag(chip.getText().toString());

            for (Tarefa t : Database.getTarefas(Database.PROGRESS_TODOS)) {
                List<String> tags = new ArrayList<>();

                for (String tag : t.getCategoria().split("‖")) {
                    if (!tag.equals(chip.getText().toString())) tags.add(tag);
                }

                String temp = "";
                for (String s : tags) {
                    if (!s.equals("")) {
                        temp += s + "‖";
                    }
                }

                t.setCategoria(temp);
                Database.editTarefa(t);
            }

            parent.setChipTags();
            MyPreferences.setSincronizado(false);
            parent.parent.firebaseHelper.atualizarRemoto();
            criarChips();

            if (removed) Toast.makeText(parent.getContext(), R.string.tag_removida, Toast.LENGTH_SHORT).show();
            else Toast.makeText(parent.getContext(), R.string.erro_ao_remover_tag, Toast.LENGTH_LONG).show();

        });

        builder.show();
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
