package com.fsmsh.kanbase.activities.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.databinding.ActivityEditBinding;
import com.fsmsh.kanbase.model.Tarefa;
import com.fsmsh.kanbase.util.Database;
import com.fsmsh.kanbase.util.FirebaseHelper;
import com.fsmsh.kanbase.util.MyPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "TagsBottomSheet";

    FirebaseHelper firebaseHelper;

    View view;
    Button button;
    Button buttonCreate;
    ChipGroup chipGroup;
    List<Chip> chips = new ArrayList<>();

    EditActivity parent;
    ActivityEditBinding binding;
    List<String> tagsPresentes;

    public TagsBottomSheet(EditActivity parent, ActivityEditBinding binding, List<String> tagsPresentes) {
        this.parent = parent;
        this.binding = binding;
        this.tagsPresentes = tagsPresentes;
    }

    public TagsBottomSheet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_tags, container, false);

        // Verifica se há parent (ao mudar o tema por exemplo)
        if (parent==null) {
            this.dismiss();
            return view;
        }

        firebaseHelper = new FirebaseHelper(getContext());

        button = view.findViewById(R.id.tags_select_sheet_buttom);
        buttonCreate = view.findViewById(R.id.tags_create_sheet_buttom);
        chipGroup = view.findViewById(R.id.chipGroup);

        criarChips();

        button.setOnClickListener(view -> select());

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TagsBottomSheet.this.getContext());
                View dialog = inflater.inflate(R.layout.dialog_add_tag, null);
                builder.setView(dialog);

                AlertDialog alertDialog = builder.show();


                //todo Impossibilitar criação de tags vazias ou repetidas
                dialog.findViewById(R.id.btnDialogSalvar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText tagStr = dialog.findViewById(R.id.txtDialogTag);

                        boolean added = Database.addTag(tagStr.getText().toString());
                        MyPreferences.setSincronizado(false);
                        firebaseHelper.atualizarRemoto();
                        criarChips();

                        if (added) Toast.makeText(parent, R.string.toast_tag_criada, Toast.LENGTH_SHORT).show();
                        else Toast.makeText(parent, R.string.toast_erro_ao_criar_tag, Toast.LENGTH_LONG).show();


                        alertDialog.dismiss();
                    }
                });

            }
        });

        return view;
    }

    public void select() {
        List<String> tags = new ArrayList<>();

        for (Chip chip : chips) {
            if (chip.isChecked()) {
                tags.add(chip.getText().toString());
            }
        }

        parent.tags = tags;
        parent.setChipTags();

        this.dismiss();
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TagsBottomSheet.this.getContext());
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

            // Tag da tarefa atual
            List<String> tags = new ArrayList<>();
            for (String tag : parent.tags) {
                if (!tag.equals(chip.getText().toString())) tags.add(tag);
            }

            String temp = "";
            for (String s : tags) {
                if (!s.equals("")) {
                    temp += s + "‖";
                }
            }
            parent.tarefa.setCategoria(temp);

            String[] tagsArr = parent.tarefa.getCategoria().split("‖");
            tags = Arrays.asList(tagsArr);
            parent.tags = tags;
            parent.setChipTags();

            MyPreferences.setSincronizado(false);
            firebaseHelper.atualizarRemoto();
            criarChips();

            if (removed) Toast.makeText(parent, R.string.tag_removida, Toast.LENGTH_SHORT).show();
            else Toast.makeText(parent, R.string.erro_ao_remover_tag, Toast.LENGTH_LONG).show();

        });

        builder.show();
    }
}
