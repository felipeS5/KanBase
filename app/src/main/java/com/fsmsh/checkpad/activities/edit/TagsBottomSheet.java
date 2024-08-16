package com.fsmsh.checkpad.activities.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.FirebaseHelper;
import com.fsmsh.checkpad.util.MyPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class TagsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "TagsBottomSheet";

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_tags, container, false);

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


                dialog.findViewById(R.id.btnDialogSalvar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText tagStr = dialog.findViewById(R.id.txtDialogTag);

                        boolean added = Database.addTag(tagStr.getText().toString());
                        MyPreferences.isSincronizado(false);
                        FirebaseHelper.atualizarRemoto();
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

            for (String tagPresente : tagsPresentes) { // verificar se o chip gerado está entre as tags presentes na tarefa
                if (s.equals(tagPresente)) chip.setChecked(true);
            }

            chips.add(chip);
            chipGroup.addView(chip);
        }
    }
}
